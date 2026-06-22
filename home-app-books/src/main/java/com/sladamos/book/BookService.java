package com.sladamos.book;

import com.sladamos.common.exception.DuplicationException;
import com.sladamos.common.exception.NotFoundException;
import com.sladamos.common.exception.ValidationException;
import com.sladamos.book.model.BookEntity;

import java.util.List;
import java.util.UUID;

public interface BookService {
    List<BookEntity> getAllBooks();
    BookEntity getBookById(UUID id) throws NotFoundException;
    void createBook(BookEntity book) throws ValidationException;
    void updateBook(BookEntity book) throws ValidationException;
    BookEntity duplicateBook(BookEntity sourceBook, List<String> existingTitles) throws ValidationException, DuplicationException;
    BookEntity duplicateBook(UUID id) throws NotFoundException, ValidationException, DuplicationException;
    void deleteBook(UUID id) throws NotFoundException;
}
