package com.sladamos.book;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BookServiceImpl implements BookService {

    private final BookRepository bookRepository;
    private final Validator validator;


    @Override
    public List<Book> getAllBooks() {
        return bookRepository.findAll();
    }

    @Override
    public Book getBookById(UUID id) throws BookNotFoundException {
        return bookRepository.findById(id).orElseThrow(() -> new BookNotFoundException("Book not found with id: " + id));
    }

    @Override
    public void createBook(Book book) throws BookValidationException {
        Set<ConstraintViolation<Book>> violations = validator.validate(book);
        if (!violations.isEmpty()) {
            throw new BookValidationException(violations);
        }
        bookRepository.save(book);
    }

    @Override
    public void updateBook(Book book) throws BookValidationException {
        Set<ConstraintViolation<Book>> violations = validator.validate(book);
        if (!violations.isEmpty()) {
            throw new BookValidationException(violations);
        }
        bookRepository.save(book);
    }

    @Override
    public void deleteBook(UUID id) throws BookNotFoundException {
        Book book = bookRepository.findById(id).orElseThrow(() -> new BookNotFoundException("Book not found with id: " + id));
        bookRepository.delete(book);
    }
}