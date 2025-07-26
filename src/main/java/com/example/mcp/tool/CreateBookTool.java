package com.example.mcp.tool;

import com.example.mcp.protocol.Tool;
import org.springframework.graphql.client.GraphQlClient;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

@Component
public class CreateBookTool implements McpTool {
    @Override
    public String getName() {
        return "create_book";
    }

    @Override
    public Tool getToolDefinition() {
        return Tool.builder().name(getName()).description("Create a new book")
                .inputSchema(Map.of("type", "object", "properties", Map.of(
                                "title", Map.of("type", "string"), "isbn", Map.of("type", "string"),
                                "description", Map.of("type", "string"), "publicationYear", Map.of("type", "integer")),
                        "required", List.of("title"))).build();
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