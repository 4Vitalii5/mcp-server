package com.example.mcp.exception;

import com.example.mcp.protocol.McpError;
import com.example.mcp.protocol.McpMessage;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<McpMessage> handleGenericException(Exception ex) {
        McpMessage errorResponse = new McpMessage();
        errorResponse.setId(null);
        errorResponse.setError(new McpError(-32603,
                "Internal Server Error: " + ex.getMessage(), null));
        return ResponseEntity.status(500).body(errorResponse);
    }
}