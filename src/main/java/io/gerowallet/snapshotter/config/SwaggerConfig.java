package io.gerowallet.snapshotter.config;

import io.gerowallet.snapshotter.Main;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * Configuration of Open API 3.
 */
@Configuration
public class SwaggerConfig {

    @Value("${spring.profiles.active}")
    private String profile;

    @Bean
    public OpenAPI customOpenAPI() {
        OpenAPI openApi = new OpenAPI();
        if (profile.equalsIgnoreCase("production")) {
            openApi = openApi.servers(List.of(new Server().url("https://hera.gerowallet.io")));
        }
        return openApi.components(new Components())
                .info(new Info().title("Gero Snapshotter")
                        .version(Main.class.getPackage().getImplementationVersion())
                        .description("Powered By adabox.io")
                        .license(new License().name("Apache 2.0").url("https://hera.gerowallet.io/")));
    }
}
