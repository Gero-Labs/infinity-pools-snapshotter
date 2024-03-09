package io.adabox.snapshotter.service;

import io.adabox.snapshotter.component.SnapshotRequestPublisher;
import io.adabox.snapshotter.config.SnapshotterProperties;
import io.adabox.snapshotter.model.Snapshot;
import io.adabox.snapshotter.utils.DateUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.UUID;

@Service
public class SnapshotterService {

    private final SnapshotterProperties snapshotterProperties;
    private final SnapshotRequestPublisher snapshotRequestPublisher;
    private final CacheManager cacheManager;

    @Autowired
    public SnapshotterService(SnapshotterProperties snapshotterProperties, SnapshotRequestPublisher snapshotRequestPublisher, CacheManager cacheManager) {
        this.snapshotterProperties = snapshotterProperties;
        this.snapshotRequestPublisher = snapshotRequestPublisher;
        this.cacheManager = cacheManager;
    }

    public Snapshot getPolicySnapshot(String policyId) {
        long timestamp = System.currentTimeMillis();
        String id = UUID.nameUUIDFromBytes(String.valueOf(Pair.of(policyId, DateUtils.convertToDate(timestamp)).hashCode()).getBytes(StandardCharsets.UTF_8)).toString();
        return Objects.requireNonNull(cacheManager.getCache("snapshots")).get(id, Snapshot.class);
    }

    public Snapshot createPolicySnapshot(String policyId) {
        long timestamp = System.currentTimeMillis();
        String id = UUID.nameUUIDFromBytes(String.valueOf(Pair.of(policyId, DateUtils.convertToDate(timestamp)).hashCode()).getBytes(StandardCharsets.UTF_8)).toString();
        Snapshot snapshot = Objects.requireNonNull(cacheManager.getCache("snapshots")).get(id, Snapshot.class);
        if (snapshot == null) {
            return snapshotRequestPublisher.publishSnapshot(id, policyId, timestamp);
        } else {
            return snapshot;
        }
    }
}
