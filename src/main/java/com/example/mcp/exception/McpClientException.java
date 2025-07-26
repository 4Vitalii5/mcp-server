package com.example.mcp.exception;

import com.example.mcp.protocol.McpError;

public class McpClientException extends RuntimeException {
    public McpClientException(McpError error) {
        super(String.format("MCP Error (Code: %d): %s", error.getCode(), error.getMessage()));
    }
}