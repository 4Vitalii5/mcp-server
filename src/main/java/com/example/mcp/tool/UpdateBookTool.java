package com.example.mcp.tool;

import com.example.mcp.protocol.Tool;
import org.springframework.graphql.client.GraphQlClient;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

@Component
public class UpdateBookTool implements McpTool {
    @Override
    public String getName() {
        return "update_book";
    }

    @Override
    public Tool getToolDefinition() {
        return Tool.builder().name(getName()).description("Update an existing book")
                .inputSchema(Map.of("type", "object", "properties", Map.of(
                        "id", Map.of("type", "integer"), "title", Map.of("type", "string"),
                        "isbn", Map.of("type", "string"), "description", Map.of("type", "string"),
                        "publicationYear", Map.of("type", "integer")), "required", List.of("id")))
                .build();
    }

    @Override
    public Mono<Map> execute(GraphQlClient graphQlClient, Map<String, Object> arguments) {
        String document = "mutation UpdateBook($id: ID!, $request: UpdateBookRequest!) { "
                + "updateBook(id: $id, request: $request) { id title } }";

        Map<String, Object> requestInput = Map.of(
                "title", arguments.get("title"),
                "isbn", arguments.get("isbn"),
                "description", arguments.get("description"),
                "publicationYear", arguments.get("publicationYear")
        );

        Map<String, Object> variables = Map.of(
                "id", arguments.get("id"),
                "request", requestInput
        );

        return graphQlClient.document(document)
                .variables(variables)
                .execute().map(response -> response.toEntity(Map.class));
    }
}