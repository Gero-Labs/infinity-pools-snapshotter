package io.adabox.snapshotter.model;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class Asset implements Serializable {

    @Schema(example = "400")
    private String amount;
    private String assetName;
    @Schema(minLength = 59, maxLength = 64, example = "stake123456789012345678901234567890123456789012345678901234")
    private String stakeAddress;
}
