package com.example.mcp.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateBookRequest(
        @NotBlank @Size(min = 1) String title,
        String isbn,
        String description,
        Integer publicationYear
) {
}