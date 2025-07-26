package com.example.mcp.controller;

import com.example.mcp.protocol.McpError;
import com.example.mcp.protocol.McpMessage;
import com.example.mcp.protocol.ToolCallParams;
import com.example.mcp.service.McpToolService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.concurrent.CompletableFuture;

@Slf4j
@RestController
@RequestMapping("/mcp")
@RequiredArgsConstructor
public class McpController {
    private final McpToolService toolService;
    private final ObjectMapper objectMapper;

    @PostMapping(value = "/stream", produces = "application/x-ndjson")
    public void handleStreamableHttp(HttpServletRequest request, HttpServletResponse response) {
        response.setContentType("application/x-ndjson");
        response.setCharacterEncoding("UTF-8");
        log.info("MCP stream connection established.");

        try (BufferedReader reader = request.getReader(); PrintWriter writer = response.getWriter()) {

            writer.println("{\"type\":\"ready\"}");
            writer.flush();
            response.flushBuffer();

            String line;
            while ((line = reader.readLine()) != null) {
                log.debug("--> Received MCP message: {}", line);
                try {
                    McpMessage message = objectMapper.readValue(line, McpMessage.class);
                    processMessage(message, writer);
                } catch (Exception e) {
                    log.error("Error processing message: {}", line, e);
                    writeJson(writer, createErrorResponse(null, e));
                }
            }
        } catch (IOException e) {
            log.warn("MCP stream connection closed: {}", e.getMessage());
        } finally {
            log.info("MCP stream connection finished.");
        }
    }

    private void processMessage(McpMessage message, PrintWriter writer) {
        String method = message.getMethod();
        CompletableFuture<?> resultFuture;

        if ("ping".equals(method)) {
            resultFuture = CompletableFuture.completedFuture("pong");
        } else if ("tools/list".equals(method)) {
            resultFuture = CompletableFuture.completedFuture(toolService.getAvailableTools());
        } else if ("tools/call".equals(method)) {
            ToolCallParams params = objectMapper.convertValue(message.getParams(), ToolCallParams.class);
            resultFuture = toolService.executeTool(params.getName(), params.getArguments());
        } else {
            resultFuture = CompletableFuture.failedFuture(new IllegalArgumentException(
                    "Method not supported: " + method));
        }

        resultFuture.whenComplete((result, ex) -> {
            if (ex != null) {
                log.error("Error executing tool for request id {}", message.getId(), ex);
                writeJson(writer, createErrorResponse(message.getId(),
                        ex.getCause() != null ? ex.getCause() : ex));
            } else {
                McpMessage response = new McpMessage();
                response.setId(message.getId());
                response.setResult(result);
                writeJson(writer, response);
            }
        });
    }

    private void writeJson(PrintWriter writer, Object object) {
        try {
            String jsonResponse = objectMapper.writeValueAsString(object);
            log.debug("<-- Sending MCP response: {}", jsonResponse);
            writer.println(jsonResponse);
            writer.flush();
        } catch (JsonProcessingException e) {
            log.error("Failed to serialize response", e);
        }
    }

    private McpMessage createErrorResponse(String id, Throwable ex) {
        McpMessage errorResponse = new McpMessage();
        errorResponse.setId(id);
        Throwable cause = ex.getCause() != null ? ex.getCause() : ex;
        errorResponse.setError(new McpError(-32603, "Internal error: " + cause.getMessage(), null));
        return errorResponse;
    }
}