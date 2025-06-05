package com.sladamos.book;

import com.sladamos.book.dto.GetBookResponse;
import com.sladamos.book.dto.GetBooksResponse;
import com.sladamos.book.dto.PatchBookRequest;
import com.sladamos.book.dto.PutBookRequest;

import java.util.UUID;

public interface BookController {
    GetBooksResponse getBooks(String publishingHouseName);
    GetBookResponse getBook(UUID id);
    void putBook(UUID id, PutBookRequest request);
    void patchBook(UUID id, PatchBookRequest request);
    void deleteBook(UUID id);
}
