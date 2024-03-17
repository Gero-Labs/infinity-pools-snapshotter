package io.adabox.snapshotter.component;

import com.bloxbean.cardano.client.api.exception.ApiException;
import com.bloxbean.cardano.client.api.model.Result;
import com.bloxbean.cardano.client.backend.api.AssetService;
import com.bloxbean.cardano.client.backend.api.BackendService;
import com.bloxbean.cardano.client.backend.model.AssetAddress;
import com.bloxbean.cardano.client.backend.model.PolicyAsset;
import io.adabox.snapshotter.config.SnapshotterProperties;
import io.adabox.snapshotter.utils.DateUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.MapIterator;
import org.apache.commons.collections4.keyvalue.MultiKey;
import org.apache.commons.collections4.map.MultiKeyMap;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.net.SocketTimeoutException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
public class TokensFetcher {

    private static final int MAX_RETRIES = 5;
    private final SnapshotterProperties snapshotterProperties;
    private final AssetService assetService;
    private final MultiKeyMap<String, List<io.adabox.snapshotter.model.AssetAddress>> multiKeyMap = new MultiKeyMap<>();

    public TokensFetcher(SnapshotterProperties snapshotterProperties, BackendService backendService) {
        this.snapshotterProperties = snapshotterProperties;
        this.assetService = backendService.getAssetService();
    }

    @Scheduled(fixedDelay = 1, timeUnit = TimeUnit.DAYS)
    private void cleanup() throws ParseException {
        long currentTime = System.currentTimeMillis();
        MapIterator<MultiKey<? extends String>, List<io.adabox.snapshotter.model.AssetAddress>> mapIterator = multiKeyMap.mapIterator();
        while (mapIterator.hasNext()) {
            MultiKey<? extends String> multiKey = mapIterator.next();
            String[] keys = multiKey.getKeys();
            long dateTime = DateUtils.convertToDate(keys[0]).getTime();
            if (currentTime - dateTime > 3 * 24 * 60 * 60 * 1000) {
                mapIterator.remove();
            }
        }
    }

    @Scheduled(fixedDelay = 1, timeUnit = TimeUnit.DAYS)
    public void fetch() {
        log.info("Fetching Tokens Addresses Data ...");
        long time = System.currentTimeMillis();
        int tryCount = 1;
        for (String policyId : snapshotterProperties.getSupportedPolicies()) {
            retrieveAssetAddressesData(time, policyId, tryCount);
        }
        log.info("Done Fetching Tokens Addresses Data.");
    }

    public boolean retrieveAssetAddressesData(long time, String policyId, int tryCount) {
        try {
            if (tryCount >= MAX_RETRIES) {
                Result<List<PolicyAsset>> policyAssetsResult = assetService.getAllPolicyAssets(policyId);
                if (!policyAssetsResult.isSuccessful()) {
                    log.error("ERROR: {}", policyAssetsResult.getResponse());
                }
                List<io.adabox.snapshotter.model.AssetAddress> assetAddresses = new ArrayList<>();
                for (PolicyAsset policyAsset : policyAssetsResult.getValue()) {
                    Result<List<AssetAddress>> assetAddressesResult = assetService.getAllAssetAddresses(policyAsset.getAsset());
                    if (!assetAddressesResult.isSuccessful()) {
                        log.error("ERROR: {}", assetAddressesResult.getResponse());
                    }
                    assetAddressesResult.getValue().forEach(assetAddress -> assetAddresses.add(new io.adabox.snapshotter.model.AssetAddress(assetAddress, policyAsset.getAsset())));
                }
                multiKeyMap.put(DateUtils.convertToDateStr(time), policyId, assetAddresses);
                return true;
            } else {
                log.error("retrieveAssetAddressesData - Try Count Exceeded");
                return false;
            }
        } catch (ApiException e) {
            if (e.getCause() instanceof SocketTimeoutException) {
                tryCount++;
                log.warn("Retrying retrieveAssetAddressesData {}/{} ...", tryCount,MAX_RETRIES);
                return retrieveAssetAddressesData(time, policyId, tryCount);
            } else {
                throw new RuntimeException(e);
            }
        }
    }

    public List<io.adabox.snapshotter.model.AssetAddress> getAssetAddressesByPolicyId(long timestamp, String policyId) {
        return multiKeyMap.get(DateUtils.convertToDateStr(timestamp), policyId);
    }
}
