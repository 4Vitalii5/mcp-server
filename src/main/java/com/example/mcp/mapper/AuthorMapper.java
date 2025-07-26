package com.example.mcp.mapper;

import com.example.mcp.dto.AuthorDto;
import com.example.mcp.dto.CreateAuthorRequest;
import com.example.mcp.entity.Author;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface AuthorMapper {
    AuthorDto toDto(Author author);

    List<AuthorDto> toDtoList(List<Author> authors);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "books", ignore = true)
    Author toEntity(CreateAuthorRequest request);
}