package com.example.mcp.tool;

import com.example.mcp.protocol.Tool;
import org.springframework.graphql.client.GraphQlClient;
import reactor.core.publisher.Mono;

import java.util.Map;

public interface McpTool {
    String getName();

    Tool getToolDefinition();

    Mono<Map> execute(GraphQlClient graphQlClient, Map<String, Object> arguments);
}