package com.sladamos.book;

import com.sladamos.book.dto.GetBooksResponse;
import com.sladamos.book.dto.PatchBookRequest;
import com.sladamos.book.dto.PutBookRequest;
import com.sladamos.common.exception.DuplicationException;
import com.sladamos.common.exception.NotFoundException;
import com.sladamos.common.exception.ValidationException;
import com.sladamos.book.functions.BooksToResponseFunction;
import com.sladamos.book.functions.RequestToBookFunction;
import com.sladamos.book.functions.RequestToUpdateBookFunction;
import com.sladamos.book.model.BookEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/books")
@RequiredArgsConstructor
public class BookController {

    private final BookService service;

    private final BooksToResponseFunction booksToResponse;

    private final RequestToBookFunction requestToBook;

    private final RequestToUpdateBookFunction requestToUpdateBook;

    @GetMapping
    public GetBooksResponse getBooks() {
        log.info("Request fetching all books");
        GetBooksResponse response = booksToResponse.apply(service.getAllBooks());
        log.info("Fetched all books: [count: {}]", response.getBooks().size());
        return response;
    }

    @PutMapping("/{id}")
    public void putBook(@PathVariable("id") UUID id, @RequestBody PutBookRequest request) {
        log.info("Request creating book: [id: {}, title: {}]", id, request.getTitle());
        try {
            service.createBook(requestToBook.apply(id, request));
            log.info("Book created: [id: {}, title: {}]", id, request.getTitle());
        } catch (ValidationException e) {
            onValidationExceptionOccurred(id, request.getTitle(), e);
        }
    }

    @PatchMapping("/{id}")
    public void patchBook(@PathVariable("id") UUID id, @RequestBody PatchBookRequest request) {
        log.info("Request updating book: [id: {}, title: {}]", id, request.getTitle());
        try {
            BookEntity book = service.getBookById(id);
            log.info("Book found for update: [id: {}, title: {}]", book.getId(), book.getTitle());
            service.updateBook(requestToUpdateBook.apply(book, request));
            log.info("Book updated: [id: {}, title: {}]", book.getId(), request.getTitle());
        } catch (NotFoundException e) {
            onNotFoundExceptionOccurred(id);
        } catch (ValidationException e) {
            onValidationExceptionOccurred(id, request.getTitle(), e);
        }
    }

    @DeleteMapping("/{id}")
    public void deleteBook(@PathVariable("id") UUID id) {
        log.info("Request deleting book: [id: {}]", id);
        try {
            service.deleteBook(id);
            log.info("Book deleted: [id: {}]", id);
        } catch (NotFoundException e) {
            onNotFoundExceptionOccurred(id);
        }
    }

    @PostMapping("/{id}/duplicate")
    public void duplicateBook(@PathVariable("id") UUID id) {
        log.info("Request duplicating book: [id: {}]", id);
        try {
            service.duplicateBook(id);
            log.info("Book duplicated: [id: {}]", id);
        } catch (NotFoundException e) {
            onNotFoundExceptionOccurred(id);
        } catch (ValidationException | DuplicationException e) {
            log.info("Book duplication failed: [id: {}, reason: {}]", id, e.getMessage());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
    }

    private static void onNotFoundExceptionOccurred(UUID id) {
        log.info("Book not found: [id: {}]", id);
        throw new ResponseStatusException(HttpStatus.NOT_FOUND);
    }

    private static void onValidationExceptionOccurred(UUID id, String request, ValidationException e) {
        log.info("Book validation failed: [id: {}, title: {}, reason: {}]", id, request, e.getMessage());
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
    }
}
