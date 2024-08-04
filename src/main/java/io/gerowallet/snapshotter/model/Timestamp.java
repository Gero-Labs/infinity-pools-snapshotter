package io.gerowallet.snapshotter.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Timestamp {

    @Schema(description = "UTC timestamp of the snapshot", example = "1657886302")
    private Integer timestamp;
}
