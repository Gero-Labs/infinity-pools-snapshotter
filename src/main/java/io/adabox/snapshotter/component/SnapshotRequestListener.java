package io.adabox.snapshotter.component;

import com.bloxbean.cardano.client.address.Address;
import com.bloxbean.cardano.client.address.AddressProvider;
import io.adabox.snapshotter.model.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.*;

@Slf4j
@Component
public class SnapshotRequestListener {

    private final CacheManager cacheManager;
    private final TokensFetcher tokensFetcher;

    @Autowired
    public SnapshotRequestListener(CacheManager cacheManager, TokensFetcher tokensFetcher) {
        this.cacheManager = cacheManager;
        this.tokensFetcher = tokensFetcher;
    }

    @Async
    @EventListener
    public void handleSnapshotRequestEvent(SnapshotRequest snapshotRequest) {
        log.info("Received Snapshot Request: {}", snapshotRequest);
        Snapshot snapshot = Objects.requireNonNull(cacheManager.getCache("snapshots")).get(snapshotRequest.getId(), Snapshot.class);
        if (snapshot == null) {
            Objects.requireNonNull(cacheManager.getCache("snapshots")).evict(snapshotRequest.getId());
            return;
        }
        snapshot.setStatus(SnapshotRequestStatus.processing);
        AssetListResponse assetListResponse = new AssetListResponse();

        List<io.adabox.snapshotter.model.AssetAddress> assetAddresses = tokensFetcher.getAssetAddressesByPolicyId(snapshotRequest.getTimestamp(), snapshotRequest.getPolicyId());
        if (assetAddresses == null) {
            assetListResponse.setStatus(SnapshotStatus.no_snapshot);
            tokensFetcher.fetch();
        }
        List<Asset> assets = new ArrayList<>();
        for (io.adabox.snapshotter.model.AssetAddress assetAddress : assetAddresses) {
            try {
                Address stake = AddressProvider.getStakeAddress(new Address(assetAddress.getAddress()));
                String stakeAddress = stake.getAddress();
                assets.add(new Asset(assetAddress.getQuantity(),
                        assetAddress.getAssetId(),
                        stakeAddress));
            } catch (Exception ignored) {}
        }
        assetListResponse.setAssets(assets);
        assetListResponse.setStatus(SnapshotStatus.success);
        snapshot.setAssetListResponse(assetListResponse);
        snapshot.setStatus(SnapshotRequestStatus.complete);
        log.info("Snapshot Complete: {}", snapshotRequest);
        Objects.requireNonNull(cacheManager.getCache("snapshots")).put(snapshotRequest.getId(), snapshot);
    }
}
