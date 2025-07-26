package com.example.mcp.tool;

import com.example.mcp.protocol.Tool;
import org.springframework.graphql.client.GraphQlClient;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

@Component
public class RemoveAuthorFromBookTool implements McpTool {
    @Override
    public String getName() {
        return "remove_author_from_book";
    }

    @Override
    public Tool getToolDefinition() {
        return Tool.builder().name(getName()).description("Remove an author from a book")
                .inputSchema(Map.of("type", "object", "properties", Map.of(
                                "bookId", Map.of("type", "integer"), "authorId",
                                Map.of("type", "integer")),
                        "required", List.of("bookId", "authorId"))).build();
    }

    @Override
    public Mono<Map> execute(GraphQlClient graphQlClient, Map<String, Object> arguments) {
        String document = "mutation RemoveAuthorFromBook($bookId: ID!, $authorId: ID!) { "
                + "removeAuthorFromBook(bookId: $bookId, authorId: $authorId) { id title authors { id name } } }";
        return graphQlClient.document(document)
                .variables(arguments)
                .execute().map(response -> response.toEntity(Map.class));
    }
}