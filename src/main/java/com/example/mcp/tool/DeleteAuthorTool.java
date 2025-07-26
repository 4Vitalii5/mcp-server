package com.example.mcp.tool;

import com.example.mcp.protocol.Tool;
import org.springframework.graphql.client.GraphQlClient;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

@Component
public class DeleteAuthorTool implements McpTool {
    @Override
    public String getName() {
        return "delete_author";
    }

    @Override
    public Tool getToolDefinition() {
        return Tool.builder().name(getName()).description("Delete an author")
                .inputSchema(Map.of("type", "object", "properties", Map.of("id",
                        Map.of("type", "integer")), "required", List.of("id")))
                .build();
    }

    @Override
    public Mono<Map> execute(GraphQlClient graphQlClient, Map<String, Object> arguments) {
        String document = "mutation DeleteAuthor($id: ID!) { deleteAuthor(id: $id) }";
        return graphQlClient.document(document)
                .variables(arguments)
                .execute().map(response -> response.toEntity(Map.class));
    }
}