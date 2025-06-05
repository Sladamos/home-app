package com.sladamos.book;

import java.util.List;
import java.util.UUID;

public interface BookService {
    List<Book> getAllBooks();
    Book getBookById(UUID id) throws BookNotFoundException;
    void createBook(Book book);
    void updateBook(Book book);
    void deleteBook(UUID id);
}
