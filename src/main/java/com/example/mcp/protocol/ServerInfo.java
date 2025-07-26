package com.example.mcp.protocol;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ServerInfo {
    private String name;
    private String version;
    @Builder.Default
    private String protocolVersion = "2024-11-05";
}
