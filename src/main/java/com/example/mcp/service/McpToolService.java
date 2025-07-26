package com.example.mcp.service;

import com.example.mcp.protocol.Tool;
import com.example.mcp.tool.McpTool;
import org.springframework.graphql.client.GraphQlClient;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class McpToolService {

    private final GraphQlClient graphQlClient;
    private final Map<String, McpTool> tools;

    public McpToolService(GraphQlClient graphQlClient, List<McpTool> toolList) {
        this.graphQlClient = graphQlClient;
        this.tools = toolList.stream()
                .collect(Collectors.toMap(McpTool::getName, Function.identity()));
    }

    public List<Tool> getAvailableTools() {
        return tools.values().stream()
                .map(McpTool::getToolDefinition)
                .collect(Collectors.toList());
    }

    public CompletableFuture<Object> executeTool(String toolName, Map<String, Object> arguments) {
        McpTool tool = tools.get(toolName);
        if (tool == null) {
            return CompletableFuture.failedFuture(new IllegalArgumentException(
                    "Unknown tool: " + toolName));
        }

        return tool.execute(graphQlClient, arguments)
                .map(Object.class::cast)
                .toFuture();
    }
}