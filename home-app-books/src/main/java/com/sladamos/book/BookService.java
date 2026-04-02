package com.sladamos.book;

import com.sladamos.book.exception.BookDuplicationException;
import com.sladamos.book.exception.BookNotFoundException;
import com.sladamos.book.exception.BookValidationException;
import com.sladamos.book.model.Book;

import java.util.List;
import java.util.UUID;

public interface BookService {
    List<Book> getAllBooks();
    Book getBookById(UUID id) throws BookNotFoundException;
    void createBook(Book book) throws BookValidationException;
    void updateBook(Book book) throws BookValidationException;
    Book duplicateBook(Book sourceBook, List<String> existingTitles) throws BookValidationException, BookDuplicationException;
    Book duplicateBook(UUID id) throws BookNotFoundException, BookValidationException, BookDuplicationException;
    void deleteBook(UUID id) throws BookNotFoundException;
}
