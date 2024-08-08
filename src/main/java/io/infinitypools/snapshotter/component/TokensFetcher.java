package io.infinitypools.snapshotter.component;

import com.bloxbean.cardano.client.api.exception.ApiException;
import com.bloxbean.cardano.client.api.model.Result;
import com.bloxbean.cardano.client.backend.api.AssetService;
import com.bloxbean.cardano.client.backend.api.BackendService;
import com.bloxbean.cardano.client.backend.model.AssetAddress;
import com.bloxbean.cardano.client.backend.model.PolicyAsset;
import io.infinitypools.snapshotter.config.SnapshotterProperties;
import io.infinitypools.snapshotter.utils.DateUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.MapIterator;
import org.apache.commons.collections4.keyvalue.MultiKey;
import org.apache.commons.collections4.map.MultiKeyMap;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.net.SocketTimeoutException;
import java.text.ParseException;
import java.util.List;
import java.util.concurrent.*;

@Slf4j
@Component
public class TokensFetcher {

    private static final int MAX_RETRIES = 5;
    private final SnapshotterProperties snapshotterProperties;
    private final AssetService assetService;
    private final MultiKeyMap<String, List<io.infinitypools.snapshotter.model.AssetAddress>> multiKeyMap = new MultiKeyMap<>();
    private final ExecutorService executorService;

    public TokensFetcher(SnapshotterProperties snapshotterProperties, BackendService backendService) {
        this.snapshotterProperties = snapshotterProperties;
        this.assetService = backendService.getAssetService();
        this.executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
    }

    @Scheduled(fixedDelay = 1, timeUnit = TimeUnit.DAYS)
    private void cleanup() throws ParseException {
        long currentTime = System.currentTimeMillis();
        MapIterator<MultiKey<? extends String>, List<io.infinitypools.snapshotter.model.AssetAddress>> mapIterator = multiKeyMap.mapIterator();
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

        snapshotterProperties.getSupportedPolicies()
                .forEach(policyId -> executorService.submit(() -> retrieveAssetAddressesData(time, policyId, 1)));
    }

    public boolean retrieveAssetAddressesData(long time, String policyId, int tryCount) {
        try {
            if (tryCount <= MAX_RETRIES) {
                log.info("Retrieve Policy Assets for Policy Id: {}", policyId);
                Result<List<PolicyAsset>> policyAssetsResult = assetService.getAllPolicyAssets(policyId);
                if (!policyAssetsResult.isSuccessful()) {
                    log.error("ERROR: {}", policyAssetsResult.getResponse());
                    return false;
                }
                ConcurrentLinkedQueue<io.infinitypools.snapshotter.model.AssetAddress> assetAddresses = new ConcurrentLinkedQueue<>();
                log.info("Retrieve Asset Addresses for Policy Id: {}", policyId);

                List<CompletableFuture<Void>> assetFutures = policyAssetsResult.getValue().stream()
                        .map(policyAsset -> CompletableFuture.runAsync(() -> {
                            int assetTryCount = 1;
                            boolean success = false;
                            while (assetTryCount <= MAX_RETRIES && !success) {
                                try {
                                    Result<List<AssetAddress>> assetAddressesResult = assetService.getAllAssetAddresses(policyAsset.getAsset());
                                    if (!assetAddressesResult.isSuccessful()) {
                                        log.error("ERROR: {}", assetAddressesResult.getResponse());
                                    } else {
                                        assetAddressesResult.getValue().forEach(assetAddress -> assetAddresses.add(new io.infinitypools.snapshotter.model.AssetAddress(assetAddress, policyAsset.getAsset())));
                                        success = true;
                                    }
                                } catch (ApiException e) {
                                    if (e.getCause() instanceof SocketTimeoutException) {
                                        assetTryCount++;
                                        log.warn("Retrying getAllAssetAddresses {}/{} for asset {} ...", assetTryCount, MAX_RETRIES, policyAsset.getAsset());
                                    } else {
                                        log.error("ApiException while retrieving asset addresses", e);
                                        break;
                                    }
                                }
                            }
                            if (!success) {
                                log.error("Failed to retrieve asset addresses for asset {} after {} retries", policyAsset.getAsset(), MAX_RETRIES);
                            }
                        }))
                        .toList();

                CompletableFuture<Void> allAssets = CompletableFuture.allOf(assetFutures.toArray(new CompletableFuture[0]));
                allAssets.get(); // Wait for all asset retrievals to complete
                log.info("Retrieved Asset Addresses for Policy Id: {}", policyId);
                multiKeyMap.put(DateUtils.convertToDateStr(time), policyId, assetAddresses.stream().toList());
                return true;
            } else {
                log.error("retrieveAssetAddressesData - Try Count Exceeded");
                return false;
            }
        } catch (ApiException e) {
            if (e.getCause() instanceof SocketTimeoutException) {
                tryCount++;
                log.warn("Retrying retrieveAssetAddressesData {}/{} ...", tryCount, MAX_RETRIES);
                return retrieveAssetAddressesData(time, policyId, tryCount);
            } else {
                log.error(e.getMessage(), e);
                throw new RuntimeException(e);
            }
        } catch (InterruptedException | ExecutionException e) {
            log.error("Error while retrieving asset addresses data", e);
            return false;
        }
    }

    public List<io.infinitypools.snapshotter.model.AssetAddress> getAssetAddressesByPolicyId(long timestamp, String policyId) {
        return multiKeyMap.get(DateUtils.convertToDateStr(timestamp), policyId);
    }
}
