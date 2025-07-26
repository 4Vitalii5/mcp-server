package com.example.mcp.tool;

import com.example.mcp.protocol.Tool;
import org.springframework.graphql.client.GraphQlClient;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.Map;

@Component
public class GetBooksTool implements McpTool {
    @Override
    public String getName() {
        return "get_books";
    }

    @Override
    public Tool getToolDefinition() {
        return Tool.builder().name(getName()).description("Get all books")
                .inputSchema(Map.of("type", "object", "properties", Map.of()))
                .build();
    }

    @Override
    public Mono<Map> execute(GraphQlClient graphQlClient, Map<String, Object> arguments) {
        String document = "query { books { id title authors { id name } } }";
        return graphQlClient.document(document).execute()
                .map(r -> r.toEntity(Map.class));
    }
}