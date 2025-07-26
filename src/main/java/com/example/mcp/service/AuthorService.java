package com.example.mcp.service;

import com.example.mcp.dto.AuthorDto;
import com.example.mcp.dto.CreateAuthorRequest;
import com.example.mcp.dto.UpdateAuthorRequest;
import com.example.mcp.entity.Author;
import com.example.mcp.entity.Book;
import com.example.mcp.mapper.AuthorMapper;
import com.example.mcp.repository.AuthorRepository;
import com.example.mcp.repository.BookRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthorService {
    private final AuthorRepository authorRepository;
    private final BookRepository bookRepository;
    private final AuthorMapper authorMapper;

    @Transactional(readOnly = true)
    public List<AuthorDto> getAllAuthors() {
        return authorMapper.toDtoList(authorRepository.findAllWithBooks());
    }

    @Transactional(readOnly = true)
    public Optional<AuthorDto> getAuthorById(Long id) {
        return authorRepository.findByIdWithBooks(id).map(authorMapper::toDto);
    }

    @Transactional(readOnly = true)
    public List<AuthorDto> findAuthorsByName(String name) {
        return authorMapper.toDtoList(authorRepository.findByNameContainingIgnoreCase(name));
    }

    @Transactional
    public AuthorDto createAuthor(CreateAuthorRequest request) {
        Author author = authorMapper.toEntity(request);
        Author savedAuthor = authorRepository.save(author);
        return authorMapper.toDto(savedAuthor);
    }

    @Transactional
    public AuthorDto updateAuthor(Long id, UpdateAuthorRequest request) {
        Author author = authorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Author not found with id: " + id));

        if (request.name() != null) author.setName(request.name());
        if (request.email() != null) author.setEmail(request.email());
        if (request.biography() != null) author.setBiography(request.biography());

        Author updatedAuthor = authorRepository.save(author);
        return authorMapper.toDto(updatedAuthor);
    }

    public void deleteAuthor(Long id) {
        authorRepository.deleteById(id);
    }

    @Transactional
    public AuthorDto addBookToAuthor(Long authorId, Long bookId) {
        Author author = authorRepository.findByIdWithBooks(authorId)
                .orElseThrow(() -> new RuntimeException("Author not found with id: " + authorId));
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new RuntimeException("Book not found with id: " + bookId));

        author.getBooks().add(book);
        book.getAuthors().add(author);

        return authorMapper.toDto(author);
    }

    @Transactional
    public AuthorDto removeBookFromAuthor(Long authorId, Long bookId) {
        Author author = authorRepository.findByIdWithBooks(authorId)
                .orElseThrow(() -> new RuntimeException("Author not found with id: " + authorId));
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new RuntimeException("Book not found with id: " + bookId));

        author.getBooks().remove(book);
        book.getAuthors().remove(author);

        return authorMapper.toDto(author);
    }
}