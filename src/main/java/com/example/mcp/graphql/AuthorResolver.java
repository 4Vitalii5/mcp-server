package com.example.mcp.graphql;

import com.example.mcp.dto.AuthorDto;
import com.example.mcp.dto.CreateAuthorRequest;
import com.example.mcp.dto.UpdateAuthorRequest;
import com.example.mcp.service.AuthorService;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.util.List;
import java.util.Optional;

@Controller
public class AuthorResolver {

    private final AuthorService authorService;

    public AuthorResolver(AuthorService authorService) {
        this.authorService = authorService;
    }

    @QueryMapping
    public List<AuthorDto> authors() {
        return authorService.getAllAuthors();
    }

    @QueryMapping
    public Optional<AuthorDto> author(@Argument Long id) {
        return authorService.getAuthorById(id);
    }

    @QueryMapping
    public List<AuthorDto> authorsByName(@Argument String name) {
        return authorService.findAuthorsByName(name);
    }

    @MutationMapping
    public AuthorDto createAuthor(@Argument CreateAuthorRequest request) {
        return authorService.createAuthor(request);
    }

    @MutationMapping
    public AuthorDto updateAuthor(@Argument Long id, @Argument UpdateAuthorRequest request) {
        return authorService.updateAuthor(id, request);
    }

    @MutationMapping
    public void deleteAuthor(@Argument Long id) {
        authorService.deleteAuthor(id);
    }

    @MutationMapping
    public AuthorDto addBookToAuthor(@Argument Long authorId, @Argument Long bookId) {
        return authorService.addBookToAuthor(authorId, bookId);
    }

    @MutationMapping
    public AuthorDto removeBookFromAuthor(@Argument Long authorId, @Argument Long bookId) {
        return authorService.removeBookFromAuthor(authorId, bookId);
    }
}