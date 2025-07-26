package com.example.mcp.service;

import com.example.mcp.dto.BookDto;
import com.example.mcp.dto.CreateBookRequest;
import com.example.mcp.dto.UpdateBookRequest;
import com.example.mcp.entity.Author;
import com.example.mcp.entity.Book;
import com.example.mcp.mapper.BookMapper;
import com.example.mcp.repository.AuthorRepository;
import com.example.mcp.repository.BookRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BookService {
    private final BookRepository bookRepository;
    private final AuthorRepository authorRepository;
    private final BookMapper bookMapper;

    @Transactional(readOnly = true)
    public List<BookDto> getAllBooks() {
        return bookMapper.toDtoList(bookRepository.findAllWithAuthors());
    }

    @Transactional(readOnly = true)
    public Optional<BookDto> getBookById(Long id) {
        return bookRepository.findByIdWithAuthors(id).map(bookMapper::toDto);
    }

    @Transactional(readOnly = true)
    public List<BookDto> findBooksByTitle(String title) {
        return bookMapper.toDtoList(bookRepository.findByTitleContainingIgnoreCase(title));
    }

    @Transactional(readOnly = true)
    public List<BookDto> findBooksByYear(Integer year) {
        return bookMapper.toDtoList(bookRepository.findByPublicationYear(year));
    }

    @Transactional(readOnly = true)
    public List<BookDto> findBooksByAuthor(Long authorId) {
        return bookMapper.toDtoList(bookRepository.findByAuthorId(authorId));
    }

    @Transactional
    public BookDto createBook(CreateBookRequest request) {
        Book book = bookMapper.toEntity(request);
        Book savedBook = bookRepository.save(book);
        return bookMapper.toDto(savedBook);
    }

    @Transactional
    public BookDto updateBook(Long id, UpdateBookRequest request) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Book not found with id: " + id));

        if (request.title() != null) book.setTitle(request.title());
        if (request.isbn() != null) book.setIsbn(request.isbn());
        if (request.description() != null) book.setDescription(request.description());
        if (request.publicationYear() != null) book.setPublicationYear(request.publicationYear());

        Book save = bookRepository.save(book);
        return bookMapper.toDto(save);
    }

    @Transactional
    public void deleteBook(Long id) {
        bookRepository.deleteById(id);
    }

    @Transactional
    public BookDto addAuthorToBook(Long bookId, Long authorId) {
        Book book = bookRepository.findByIdWithAuthors(bookId)
                .orElseThrow(() -> new RuntimeException("Book not found with id: " + bookId));
        Author author = authorRepository.findById(authorId)
                .orElseThrow(() -> new RuntimeException("Author not found with id: " + authorId));

        book.getAuthors().add(author);
        author.getBooks().add(book);

        return bookMapper.toDto(book);
    }

    @Transactional
    public BookDto removeAuthorFromBook(Long bookId, Long authorId) {
        Book book = bookRepository.findByIdWithAuthors(bookId)
                .orElseThrow(() -> new RuntimeException("Book not found with id: " + bookId));
        Author author = authorRepository.findById(authorId)
                .orElseThrow(() -> new RuntimeException("Author not found with id: " + authorId));

        book.getAuthors().remove(author);
        author.getBooks().remove(book);

        return bookMapper.toDto(book);
    }
}