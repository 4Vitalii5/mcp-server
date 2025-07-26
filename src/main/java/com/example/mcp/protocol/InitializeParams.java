package com.example.mcp.protocol;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InitializeParams {
    private String protocolVersion;
    private ClientInfo clientInfo;
    private Object capabilities;
}
