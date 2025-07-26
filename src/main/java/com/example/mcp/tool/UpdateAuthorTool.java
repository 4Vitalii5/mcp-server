package com.example.mcp.tool;

import com.example.mcp.protocol.Tool;
import org.springframework.graphql.client.GraphQlClient;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

@Component
public class UpdateAuthorTool implements McpTool {
    @Override
    public String getName() {
        return "update_author";
    }

    @Override
    public Tool getToolDefinition() {
        return Tool.builder().name(getName()).description("Update an existing author")
                .inputSchema(Map.of("type", "object", "properties", Map.of(
                                "id", Map.of("type", "integer"), "name", Map.of("type", "string"),
                                "email", Map.of("type", "string"), "biography", Map.of("type", "string")),
                        "required", List.of("id"))).build();
    }

    @Override
    public Mono<Map> execute(GraphQlClient graphQlClient, Map<String, Object> arguments) {
        String document = "mutation UpdateAuthor($id: ID!, $request: UpdateAuthorRequest!) { "
                + "updateAuthor(id: $id, request: $request) { id name email } }";

        Map<String, Object> requestInput = Map.of(
                "name", arguments.get("name"),
                "email", arguments.get("email"),
                "biography", arguments.get("biography")
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