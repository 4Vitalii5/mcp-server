package com.example.mcp.service;

import com.example.mcp.dto.CreateAuthorRequest;
import com.example.mcp.dto.CreateBookRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(1)
@Profile("!test")
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {
    private final AuthorService authorService;
    private final BookService bookService;

    @Override
    public void run(String... args) {
        if (authorService.getAllAuthors().isEmpty()) {
            System.out.println("Seeding initial data...");

            var tolkien = authorService.createAuthor(new CreateAuthorRequest("J.R.R. Tolkien",
                    "tolkien@example.com", "English author"));
            var martin = authorService.createAuthor(new CreateAuthorRequest("George R.R. Martin",
                    "martin@example.com", "American novelist"));

            var hobbit = bookService.createBook(new CreateBookRequest("The Hobbit", "isbn-1",
                    "A fantasy novel", 1937));
            var lotr = bookService.createBook(new CreateBookRequest("The Lord of the Rings", "isbn-2",
                    "Epic fantasy", 1954));

            bookService.addAuthorToBook(hobbit.id(), tolkien.id());
            bookService.addAuthorToBook(lotr.id(), martin.id());

            System.out.println("✓ Sample data seeded successfully.");
        }
    }
}