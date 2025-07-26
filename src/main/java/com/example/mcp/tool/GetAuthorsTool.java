package com.example.mcp.tool;

import com.example.mcp.protocol.Tool;
import org.springframework.graphql.client.GraphQlClient;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.Map;

@Component
public class GetAuthorsTool implements McpTool {
    @Override
    public String getName() {
        return "get_authors";
    }

    @Override
    public Tool getToolDefinition() {
        return Tool.builder().name(getName()).description("Get all authors or search by name")
                .inputSchema(Map.of("type", "object", "properties", Map.of("name",
                        Map.of("type", "string", "description", "optional"))))
                .build();
    }

    @Override
    public Mono<Map> execute(GraphQlClient graphQlClient, Map<String, Object> arguments) {
        String name = (String) arguments.get("name");
        if (name != null && !name.isBlank()) {
            String document = "query AuthorsByName($name: String!) { authorsByName(name: $name) { id name } }";
            return graphQlClient.document(document).variable("name", name).execute()
                    .map(r -> r.toEntity(Map.class));
        }
        String document = "query { authors { id name books { id title } } }";
        return graphQlClient.document(document).execute().map(r -> r.toEntity(Map.class));
    }
}