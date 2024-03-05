package io.adabox.snapshotter.controller;

import io.adabox.snapshotter.model.Timestamp;
import io.adabox.snapshotter.model.AssetListResponse;
import io.adabox.snapshotter.model.ErrorResponse;
import io.adabox.snapshotter.model.SnapshotResponse;
import io.adabox.snapshotter.service.SnapshotterService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    @ApiResponse(responseCode = "500", description = "Unexpected Error", content = {@Content(schema = @Schema(implementation = ErrorResponse.class))})
    @Operation(description = "Get Assets using policy and an optional stake address. There must be a snapshot available for this to succeed.")
    public ResponseEntity<AssetListResponse> assetsByPolicyId(@PathVariable(value = "policy_id")
                                                              @Schema(description = "Policy Id of the Snapshot", example = "476039a0949cf0b22f6a800f56780184c44533887ca6e821007840c3")
                                                              String policyId,
                                                              @RequestParam(value = "stake_address", required = false)
                                                              @Schema(description = "Stake address to filter Response on", example = "stake123456789012345678901234567890123456789012345678901234")
                                                              String stakeAddress,
                                                              @RequestBody Timestamp timestamp) {
        return ResponseEntity.ok().build();
//        return ResponseEntity.ok(snapshotterService.assetsByPolicyId(policyId, stakeAddress, assetListRequest));
    }

    @GetMapping("/assets/{policy_id}/{epoch_no}")
    @ApiResponse(responseCode = "200", description = "OK")
    @ApiResponse(responseCode = "500", description = "Unexpected Error", content = {@Content(schema = @Schema(implementation = ErrorResponse.class))})
    @Operation(description = "Get Assets using policy and epoch and an optional stake address. There must be a snapshot available for this to succeed.")
    public ResponseEntity<AssetListResponse> assetsByPolicyIdAndEpoch(@PathVariable(value = "policy_id")
                                                                      @Schema(description = "Policy Id of the Snapshot", example = "476039a0949cf0b22f6a800f56780184c44533887ca6e821007840c3")
                                                                      String policyId,
                                                                      @Schema(description = "snapshot id / epoch", example = "204")
                                                                      @PathVariable(value = "epoch_no") Integer epochNo,
                                                                      @Schema(description = "Stake address to filter Response on", example = "stake123456789012345678901234567890123456789012345678901234")
                                                                      @RequestParam(value = "stake_address", required = false) String stakeAddress) {
        return ResponseEntity.ok().build();
//        return ResponseEntity.ok(snapshotterService.assetsByPolicyIdAndEpoch(policyId, epochNo, stakeAddress));
    }

    @GetMapping(value = "/policy_snapshot/{policy_id}")
    @ApiResponse(responseCode = "200", description = "OK")
    @ApiResponse(responseCode = "500", description = "Unexpected Error", content = {@Content(schema = @Schema(implementation = ErrorResponse.class))})
    @Operation(description = "Get Assets using policy and epoch and an optional stake address. There must be a snapshot available for this to succeed.")
    public ResponseEntity<SnapshotResponse> policySnapshotByPolicyId(@PathVariable(value = "policy_id")
                                                                     @Schema(description = "Policy Id of the Snapshot", example = "476039a0949cf0b22f6a800f56780184c44533887ca6e821007840c3")
                                                                     String policyId,
                                                                     @RequestBody Timestamp timestamp) {
        return ResponseEntity.ok().build();
//        return ResponseEntity.ok(snapshotterService.assetsByPolicyIdAndEpoch(policyId, epochNo, stakeAddress));
    }

    @PostMapping(value = "/policy_snapshot/{policy_id}")
    @ApiResponse(responseCode = "200", description = "OK")
    @ApiResponse(responseCode = "500", description = "Unexpected Error", content = {@Content(schema = @Schema(implementation = ErrorResponse.class))})
    @Operation(description = "Take snapshot by date")
    public ResponseEntity<SnapshotResponse> postPolicySnapshotByPolicyId(@PathVariable(value = "policy_id")
                                                                         @Schema(description = "Policy Id to be Snapshotted", example = "476039a0949cf0b22f6a800f56780184c44533887ca6e821007840c3")
                                                                         String policyId,
                                                                         @RequestBody Timestamp timestamp) {
        return ResponseEntity.ok().build();
//        return ResponseEntity.ok(snapshotterService.assetsByPolicyIdAndEpoch(policyId, epochNo, stakeAddress));
    }

    @GetMapping(value = "/policy_snapshot/{policy_id}/{epoch_no}")
    @ApiResponse(responseCode = "200", description = "OK")
    @ApiResponse(responseCode = "500", description = "Unexpected Error", content = {@Content(schema = @Schema(implementation = ErrorResponse.class))})
    @Operation(description = "Get Snapshot Status by Epoch")
    public ResponseEntity<SnapshotResponse> getSnapshotStatus(@PathVariable(value = "policy_id")
                                                              @Schema(description = "Policy Id of the Snapshot", example = "476039a0949cf0b22f6a800f56780184c44533887ca6e821007840c3")
                                                              String policyId,
                                                              @PathVariable(value = "epoch_no")
                                                              @Schema(description = "Epoch number which the snapshot was taken for", example = "204")
                                                              String epochNo) {
        return ResponseEntity.ok().build();
//        return ResponseEntity.ok(snapshotterService.assetsByPolicyIdAndEpoch(policyId, epochNo, stakeAddress));
    }

    @PostMapping(value = "/policy_snapshot/{policy_id}/{epoch_no}")
    @ApiResponse(responseCode = "200", description = "OK")
    @ApiResponse(responseCode = "500", description = "Unexpected Error", content = {@Content(schema = @Schema(implementation = ErrorResponse.class))})
    @Operation(description = "Take Snapshot by Epoch")
    public ResponseEntity<SnapshotResponse> postSnapshotStatus(@PathVariable(value = "policy_id")
                                                              @Schema(description = "Policy Id of the Snapshot", example = "476039a0949cf0b22f6a800f56780184c44533887ca6e821007840c3")
                                                              String policyId,
                                                              @PathVariable(value = "epoch_no")
                                                              @Schema(description = "Epoch number which the snapshot was taken for", example = "204")
                                                              String epochNo) {
        return ResponseEntity.ok().build();
//        return ResponseEntity.ok(snapshotterService.assetsByPolicyIdAndEpoch(policyId, epochNo, stakeAddress));
    }
}
