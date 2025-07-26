package com.example.mcp.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.graphql.client.HttpGraphQlClient;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class AppConfig {

    @Value("${mcp.server.base-url}")
    private String serverBaseUrl;

    @Value("${mcp.server.graphql-path:/graphql}")
    private String graphqlPath;

    @Bean
    public HttpGraphQlClient graphQlClient() {
        WebClient client = WebClient.builder()
                .baseUrl(serverBaseUrl + graphqlPath)
                .build();
        return HttpGraphQlClient.builder(client).build();
    }
}
