package io.adabox.snapshotter.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AssetListResponse {

    private List<Asset> assets;

    @Schema(example = "an error occurred")
    private String message;

    @Schema(example = "success")
    private SnapshotStatus status;
}
