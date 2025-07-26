package com.example.mcp.dto;

import java.util.List;

public record BookDto(
        Long id,
        String title,
        String isbn,
        String description,
        Integer publicationYear,
        List<AuthorInBookDto> authors
) {
}