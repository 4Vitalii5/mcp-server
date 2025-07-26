package com.example.mcp.dto;

import java.util.List;

public record AuthorDto(
        Long id,
        String name,
        String email,
        String biography,
        List<BookInAuthorDto> books
) {
}