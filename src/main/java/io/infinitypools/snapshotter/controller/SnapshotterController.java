package io.infinitypools.snapshotter.controller;

import io.infinitypools.snapshotter.model.*;
import io.infinitypools.snapshotter.service.SnapshotterService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.apache.commons.lang3.SerializationUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Tag(name = "Snapshotter Controller", description = "Provides access end-point for Snapshots interaction")
public class SnapshotterController {

    private final SnapshotterService snapshotterService;

    @Autowired
    public SnapshotterController(SnapshotterService snapshotterService) {
        this.snapshotterService = snapshotterService;
    }

    @GetMapping("/assets/{policy_id}")
    @ApiResponse(responseCode = "200", description = "OK")
    @ApiResponse(responseCode = "404", description = "No Snapshot Found")
    @ApiResponse(responseCode = "500", description = "Unexpected Error", content = {@Content(schema = @Schema(implementation = ErrorResponse.class))})
    @Operation(description = "Get Assets using policy and an optional stake address. There must be a snapshot available for this to succeed.")
    public ResponseEntity<AssetListResponse> assetsByPolicyId(@PathVariable(value = "policy_id")
                                                              @Schema(description = "Policy Id of the Snapshot", example = "83117b5acb702fd086db386d26e83b7de3b19fbfe24c9e96f8f101a3")
                                                              String policyId,
                                                              @RequestParam(value = "stake_address", required = false)
                                                              @Schema(description = "Stake address to filter Response on", example = "stake123456789012345678901234567890123456789012345678901234")
                                                              String stakeAddress) {
        Snapshot snapshot = snapshotterService.getPolicySnapshot(policyId);
        if (snapshot == null || snapshot.getStatus() != SnapshotRequestStatus.complete) {
            return ResponseEntity.notFound().build();
        }
        AssetListResponse assetListResponse = SerializationUtils.clone(snapshot.getAssetListResponse());
        if (StringUtils.isNotBlank(stakeAddress)) {
            List<Asset> assets = assetListResponse.getAssets().stream().filter(asset -> asset.getStakeAddress().equalsIgnoreCase(stakeAddress)).toList();
            assetListResponse.setAssets(assets);
        }
        return ResponseEntity.ok(assetListResponse);
    }

    @GetMapping(value = "/policy_snapshot/{policy_id}")
    @ApiResponse(responseCode = "200", description = "OK")
    @ApiResponse(responseCode = "404", description = "No Snapshot Found")
    @ApiResponse(responseCode = "500", description = "Unexpected Error", content = {@Content(schema = @Schema(implementation = ErrorResponse.class))})
    @Operation(description = "Get Assets using policy and epoch and an optional stake address. There must be a snapshot available for this to succeed.")
    public ResponseEntity<Snapshot> policySnapshotByPolicyId(@PathVariable(value = "policy_id")
                                                             @Schema(description = "Policy Id of the Snapshot", example = "83117b5acb702fd086db386d26e83b7de3b19fbfe24c9e96f8f101a3")
                                                             String policyId) {
        Snapshot snapshot = snapshotterService.getPolicySnapshot(policyId);
        if (snapshot == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(snapshot);
    }

    @PostMapping(value = "/policy_snapshot/{policy_id}")
    @ApiResponse(responseCode = "200", description = "OK")
    @ApiResponse(responseCode = "500", description = "Unexpected Error", content = {@Content(schema = @Schema(implementation = ErrorResponse.class))})
    @Operation(description = "Take snapshot by date")
    public ResponseEntity<Snapshot> postPolicySnapshotByTimestamp(@PathVariable(value = "policy_id")
                                                                  @Schema(description = "Policy Id to be Snapshotted", example = "83117b5acb702fd086db386d26e83b7de3b19fbfe24c9e96f8f101a3")
                                                                  String policyId,
                                                                  @RequestParam(value = "force", defaultValue = "false", required = false) boolean force) {
        return ResponseEntity.ok(snapshotterService.createPolicySnapshot(policyId, force));
    }
}
