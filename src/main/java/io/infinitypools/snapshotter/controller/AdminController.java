package io.infinitypools.snapshotter.controller;

import io.infinitypools.snapshotter.Main;
import io.infinitypools.snapshotter.controller.exception.ProbeFailedException;
import io.infinitypools.snapshotter.model.ErrorResponse;
import io.infinitypools.snapshotter.model.LivenessResponse;
import io.infinitypools.snapshotter.model.ReadinessResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.boot.availability.ApplicationAvailability;
import org.springframework.boot.availability.LivenessState;
import org.springframework.boot.availability.ReadinessState;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Rest Controller for management staff like health checks, version, properties etc.
 */
@RestController
@RequestMapping("/admin")
@Tag(name = "Admin Controller", description = "Provides access end-point for management")
public class AdminController {

    private final ApplicationAvailability applicationAvailability;

    public AdminController(ApplicationAvailability applicationAvailability) {
        this.applicationAvailability = applicationAvailability;
    }

    /**
     * Checks if service is live and ready to get traffic.
     */
    @GetMapping("/readiness")
    @ApiResponse(responseCode = "200", description = "Service is Healthy")
    @ApiResponse(responseCode = "503", description = "Service is not Healthy", content = {@Content(schema = @Schema(implementation = ErrorResponse.class))})
    @Operation(description = "Detect if the service can receive traffic", hidden = true)
    public ResponseEntity<ReadinessResponse> readiness() {
        if (this.applicationAvailability.getReadinessState() != ReadinessState.ACCEPTING_TRAFFIC) {
            throw new ProbeFailedException("Service is not ready for receiving traffic.");
        }
        return ResponseEntity.ok(new ReadinessResponse(ReadinessState.ACCEPTING_TRAFFIC.toString()));
    }

    /**
     * Checks if service is ready.
     */
    @GetMapping("/liveness")
    @ApiResponse(responseCode = "200", description = "Service is Healthy")
    @ApiResponse(responseCode = "503", description = "Service is not Healthy", content = {@Content(schema = @Schema(implementation = ErrorResponse.class))})
    @Operation(description = "Detect if the service need to be restarted", hidden = true)
    ResponseEntity<LivenessResponse> liveness() {
        if (this.applicationAvailability.getLivenessState() != LivenessState.CORRECT) {
            throw new ProbeFailedException("Service is not healthy");
        }
        return ResponseEntity.ok(new LivenessResponse(LivenessState.CORRECT.toString()));
    }

    /**
     * Gets current software version.
     *
     * @return Current version of the application.
     */
    @GetMapping("/version")
    @ApiResponse(responseCode = "200", description = "Version evaluated correctly")
    @Operation(description = "Returns service version")
    ResponseEntity<String> version() {
        return ResponseEntity.ok(Main.class.getPackage().getImplementationVersion());
    }
}