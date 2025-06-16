package com.sladamos.book;

import java.util.List;
import java.util.UUID;

public interface BookService {
    List<Book> getAllBooks();
    Book getBookById(UUID id) throws BookNotFoundException;
    void createBook(Book book) throws BookValidationException;
    void updateBook(Book book) throws BookValidationException;
    void deleteBook(UUID id) throws BookNotFoundException;
}
