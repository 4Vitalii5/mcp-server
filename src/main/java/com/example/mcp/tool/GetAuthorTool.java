package com.example.mcp.tool;

import com.example.mcp.protocol.Tool;
import org.springframework.graphql.client.GraphQlClient;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

@Component
public class GetAuthorTool implements McpTool {
    @Override
    public String getName() {
        return "get_author";
    }

    @Override
    public Tool getToolDefinition() {
        return Tool.builder().name(getName()).description("Get author by ID")
                .inputSchema(Map.of("type", "object", "properties", Map.of("id",
                        Map.of("type", "integer")), "required", List.of("id")))
                .build();
    }

    @Override
    public Mono<Map> execute(GraphQlClient graphQlClient, Map<String, Object> arguments) {
        String document = "query AuthorById($id: ID!) { author(id: $id) { "
                + "id name email biography books { id title } } }";
        return graphQlClient.document(document)
                .variables(arguments)
                .execute().map(response -> response.toEntity(Map.class));
    }
}