package com.example.mcp.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

public record UpdateAuthorRequest(
        @Size(min = 2) String name,
        @Email String email,
        String biography
) {
}