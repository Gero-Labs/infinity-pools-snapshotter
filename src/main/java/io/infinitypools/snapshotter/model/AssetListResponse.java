package io.infinitypools.snapshotter.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AssetListResponse implements Serializable {

    private List<Asset> assets;

    @Schema(example = "an error occurred")
    private String message;

    @Schema(example = "success")
    private SnapshotStatus status;
}
