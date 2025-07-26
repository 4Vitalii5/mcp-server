package com.example.mcp.tool;

import com.example.mcp.protocol.Tool;
import org.springframework.graphql.client.GraphQlClient;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

@Component
public class AddAuthorToBookTool implements McpTool {
    @Override
    public String getName() {
        return "add_author_to_book";
    }

    @Override
    public Tool getToolDefinition() {
        return Tool.builder().name(getName()).description("Add an author to a book")
                .inputSchema(Map.of("type", "object", "properties", Map.of(
                                "bookId", Map.of("type", "integer"), "authorId", Map.of("type", "integer")),
                        "required", List.of("bookId", "authorId"))).build();
    }

    @Override
    public Mono<Map> execute(GraphQlClient graphQlClient, Map<String, Object> arguments) {
        String document = "mutation AddAuthorToBook($bookId: ID!, $authorId: ID!) { addAuthorToBook(bookId: "
                + "$bookId, authorId: $authorId) { id title authors { id name } } }";
        return graphQlClient.document(document)
                .variables(arguments)
                .execute().map(response -> response.toEntity(Map.class));
    }
}