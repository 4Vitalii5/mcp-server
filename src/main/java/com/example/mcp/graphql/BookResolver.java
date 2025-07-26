package com.example.mcp.graphql;

import com.example.mcp.dto.BookDto;
import com.example.mcp.dto.CreateBookRequest;
import com.example.mcp.dto.UpdateBookRequest;
import com.example.mcp.service.BookService;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.util.List;
import java.util.Optional;

@Controller
public class BookResolver {

    private final BookService bookService;

    public BookResolver(BookService bookService) {
        this.bookService = bookService;
    }

    @QueryMapping
    public List<BookDto> books() {
        return bookService.getAllBooks();
    }

    @QueryMapping
    public Optional<BookDto> book(@Argument Long id) {
        return bookService.getBookById(id);
    }

    @QueryMapping
    public List<BookDto> booksByTitle(@Argument String title) {
        return bookService.findBooksByTitle(title);
    }

    @QueryMapping
    public List<BookDto> booksByYear(@Argument Integer year) {
        return bookService.findBooksByYear(year);
    }

    @QueryMapping
    public List<BookDto> booksByAuthor(@Argument Long authorId) {
        return bookService.findBooksByAuthor(authorId);
    }

    @MutationMapping
    public BookDto createBook(@Argument CreateBookRequest request) {
        return bookService.createBook(request);
    }

    @MutationMapping
    public BookDto updateBook(@Argument Long id, @Argument UpdateBookRequest request) {
        return bookService.updateBook(id, request);
    }

    @MutationMapping
    public void deleteBook(@Argument Long id) {
        bookService.deleteBook(id);
    }

    @MutationMapping
    public BookDto addAuthorToBook(@Argument Long bookId, @Argument Long authorId) {
        return bookService.addAuthorToBook(bookId, authorId);
    }

    @MutationMapping
    public BookDto removeAuthorFromBook(@Argument Long bookId, @Argument Long authorId) {
        return bookService.removeAuthorFromBook(bookId, authorId);
    }
}