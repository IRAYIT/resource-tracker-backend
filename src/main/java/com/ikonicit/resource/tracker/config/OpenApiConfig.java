package com.ikonicit.resource.tracker.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.util.List;
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        Server localServer = new Server();
        localServer.setUrl("http://localhost:8098");
        localServer.setDescription("Local Dev Server");

        Server prodServer = new Server();
        prodServer.setUrl("https://resourcetracker.gotracrat.in:8098");
        prodServer.setDescription("Production Server");

        return new OpenAPI().servers(List.of(localServer, prodServer));
    }
}

