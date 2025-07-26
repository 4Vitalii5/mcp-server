package com.example.mcp.protocol;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InitializeResult {
    @Builder.Default
    private String protocolVersion = "2024-11-05";
    private ServerInfo serverInfo;
    private Object capabilities;
}
