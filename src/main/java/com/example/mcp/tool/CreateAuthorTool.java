package com.example.mcp.tool;

import com.example.mcp.protocol.Tool;
import org.springframework.graphql.client.GraphQlClient;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

@Component
public class CreateAuthorTool implements McpTool {
    @Override
    public String getName() {
        return "create_author";
    }

    @Override
    public Tool getToolDefinition() {
        return Tool.builder().name(getName()).description("Create a new author")
                .inputSchema(Map.of("type", "object", "properties", Map.of(
                                "name", Map.of("type", "string"), "email", Map.of("type", "string"),
                                "biography", Map.of("type", "string")),
                        "required", List.of("name"))).build();
    }

    @Override
    public Mono<Map> execute(GraphQlClient graphQlClient, Map<String, Object> arguments) {
        String document = "mutation CreateAuthor($request: CreateAuthorRequest!) { createAuthor(request: $request)"
                + " { id name email } }";

        return graphQlClient.document(document)
                .variable("request", arguments)
                .execute().map(response -> response.toEntity(Map.class));
    }
}