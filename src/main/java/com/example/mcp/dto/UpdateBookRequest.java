package com.example.mcp.dto;

import jakarta.validation.constraints.Size;

public record UpdateBookRequest(
        @Size(min = 1) String title,
        String isbn,
        String description,
        Integer publicationYear
) {
}