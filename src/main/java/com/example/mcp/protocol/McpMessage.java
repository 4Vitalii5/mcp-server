package com.example.mcp.protocol;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class McpMessage {
    private String jsonrpc = "2.0";
    private String id;
    private String method;
    private Object params;
    private Object result;
    private McpError error;
    private String type;
}