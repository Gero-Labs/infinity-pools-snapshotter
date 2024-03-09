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
import org.apache.commons.collections4.map.MultiKeyMap;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
public class TokensFetcher {

    private final SnapshotterProperties snapshotterProperties;
    private final AssetService assetService;
    private final MultiKeyMap<String, List<io.adabox.snapshotter.model.AssetAddress>> multiKeyMap = new MultiKeyMap<>();

    public TokensFetcher(SnapshotterProperties snapshotterProperties, BackendService backendService) {
        this.snapshotterProperties = snapshotterProperties;
        this.assetService = backendService.getAssetService();
        fetch();
    }

    @Scheduled(fixedDelay = 1, timeUnit = TimeUnit.DAYS)
    public void fetch() {
        log.info("Fetching Tokens Addresses Data ...");
        long time = System.currentTimeMillis();
        for (String policyId : snapshotterProperties.getSupportedPolicies()) {
            try {
                Result<List<PolicyAsset>> policyAssetsResult = assetService.getAllPolicyAssets(policyId);
                if (!policyAssetsResult.isSuccessful()) {
                    log.error("ERROR");
                }
                List<io.adabox.snapshotter.model.AssetAddress> assetAddresses = new ArrayList<>();
                for (PolicyAsset policyAsset : policyAssetsResult.getValue()) {
                    Result<List<AssetAddress>> assetAddressesResult = assetService.getAllAssetAddresses(policyAsset.getAsset());
                    if (!assetAddressesResult.isSuccessful()) {
                        log.error("ERROR");
                    }
                    assetAddressesResult.getValue().forEach(assetAddress -> assetAddresses.add(new io.adabox.snapshotter.model.AssetAddress(assetAddress, policyAsset.getAsset())));
                }
                multiKeyMap.put(DateUtils.convertToDate(time), policyId, assetAddresses);
            } catch (ApiException e) {
                throw new RuntimeException(e);
            }
        }
        log.info("Done Fetching Tokens Addresses Data.");
    }

    public List<io.adabox.snapshotter.model.AssetAddress> getAssetAddressesByPolicyId(long timestamp, String policyId) {
        return multiKeyMap.get(DateUtils.convertToDate(timestamp), policyId);
    }
}
