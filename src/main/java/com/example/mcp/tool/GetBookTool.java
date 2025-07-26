package com.example.mcp.tool;

import com.example.mcp.protocol.Tool;
import org.springframework.graphql.client.GraphQlClient;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

@Component
public class GetBookTool implements McpTool {
    @Override
    public String getName() {
        return "get_book";
    }

    @Override
    public Tool getToolDefinition() {
        return Tool.builder().name(getName()).description("Get book by ID")
                .inputSchema(Map.of("type", "object", "properties", Map.of("id",
                        Map.of("type", "integer")), "required", List.of("id")))
                .build();
    }

    @Override
    public Mono<Map> execute(GraphQlClient graphQlClient, Map<String, Object> arguments) {
        String document = "mutation CreateBook($request: CreateBookRequest!) { "
                + "createBook(request: $request) { id title } }";

        return graphQlClient.document(document)
                .variable("request", arguments)
                .execute().map(response -> response.toEntity(Map.class));
    }
}