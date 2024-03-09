package io.adabox.snapshotter.component;

import io.adabox.snapshotter.model.Snapshot;
import io.adabox.snapshotter.model.SnapshotRequest;
import io.adabox.snapshotter.model.SnapshotRequestStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Slf4j
@Component
public class SnapshotRequestPublisher {

    private final ApplicationEventPublisher snapshotEventPublisher;
    private final CacheManager cacheManager;

    @Autowired
    public SnapshotRequestPublisher(ApplicationEventPublisher snapshotEventPublisher, CacheManager cacheManager) {
        this.snapshotEventPublisher = snapshotEventPublisher;
        this.cacheManager = cacheManager;
    }

    public Snapshot publishSnapshot(final String id, final String policyId, final Long time) {
        SnapshotRequest snapshotRequest = new SnapshotRequest(this, id,  policyId, time);
        log.info("Publishing Snapshot Request: {}", snapshotRequest);
        Snapshot snapshot = new Snapshot(null, snapshotRequest.getId(), SnapshotRequestStatus.queued, null, snapshotRequest.getTime());
        Objects.requireNonNull(cacheManager.getCache("snapshots")).put(id, snapshot);
        snapshotEventPublisher.publishEvent(snapshotRequest);
        return snapshot;
    }
}
