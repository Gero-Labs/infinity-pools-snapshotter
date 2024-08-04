package io.gerowallet.snapshotter.model;

import lombok.Getter;
import lombok.ToString;
import org.springframework.context.ApplicationEvent;

@Getter
@ToString
public class SnapshotRequest extends ApplicationEvent {

    private final String id;
    private final String policyId;
    private final Long time;

    public SnapshotRequest(Object source, String id, String policyId, Long time) {
        super(source);
        this.id = id;
        this.policyId = policyId;
        this.time = time;
    }
}
