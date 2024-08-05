package io.infinitypools.snapshotter.config;

import com.bloxbean.cardano.client.backend.api.BackendService;
import com.bloxbean.cardano.client.backend.blockfrost.common.Constants;
import com.bloxbean.cardano.client.backend.blockfrost.service.BFBackendService;
import com.bloxbean.cardano.client.backend.koios.KoiosBackendService;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

import static com.bloxbean.cardano.client.backend.koios.Constants.*;

@Data
@Configuration
@ConfigurationProperties(prefix = "snapshotter")
public class SnapshotterProperties {

    private Provider provider;
    private List<String> supportedPolicies;

    @Bean
    public BackendService backendService() {
        if (provider.getProviderType() == ProviderType.BLOCKFROST) {
            return new BFBackendService(resolveBlockfrostBaseUrl(), provider.getProviderToken());
        } else if (provider.getProviderType() == ProviderType.KOIOS) {
            return new KoiosBackendService(resolveKoiosBaseUrl(), provider.getProviderToken());
        }
        throw new IllegalStateException("No Support for " + provider + " Provider!");
    }

    private String resolveKoiosBaseUrl() {
        if (provider.getNetwork() == Network.MAINNET) {
            return KOIOS_MAINNET_URL;
        } else if (provider.getNetwork() == Network.PREPROD) {
            return KOIOS_PREPROD_URL;
        } else if (provider.getNetwork() == Network.PREVIEW) {
            return KOIOS_PREVIEW_URL;
        } else {
            throw new IllegalStateException("Koios Base URL: No Support for " + provider.getNetwork() + " Network!");
        }
    }

    private String resolveBlockfrostBaseUrl() {
        if (provider.getNetwork() == Network.MAINNET) {
            return Constants.BLOCKFROST_MAINNET_URL;
        } else if (provider.getNetwork() == Network.PREPROD) {
            return Constants.BLOCKFROST_PREPROD_URL;
        } else if (provider.getNetwork() == Network.PREVIEW) {
            return Constants.BLOCKFROST_PREVIEW_URL;
        } else {
            throw new IllegalStateException("BlockFrost Base URL: No Support for " + provider.getNetwork() + " Network!");
        }
    }

    @Data
    @NoArgsConstructor
    public static class Provider {

        private ProviderType providerType;
        private String providerToken;
        private Network network;
    }

    public enum ProviderType {

        KOIOS,
        BLOCKFROST
    }

    private enum Network {
        MAINNET,
        PREPROD,
        PREVIEW
    }
}
