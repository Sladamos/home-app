package com.sladamos.book;

import com.sladamos.book.dto.GetBooksResponse;
import com.sladamos.book.dto.PatchBookRequest;
import com.sladamos.book.dto.PutBookRequest;
import com.sladamos.book.functions.BooksToResponseFunction;
import com.sladamos.book.functions.RequestToBookFunction;
import com.sladamos.book.functions.RequestToUpdateBookFunction;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;

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
        return booksToResponse.apply(service.getAllBooks());
    }

    @PutMapping("/{id}")
    public void putBook(@PathVariable("id") UUID id, @RequestBody PutBookRequest request) {
        try {
            service.createBook(requestToBook.apply(id, request));
        } catch (BookValidationException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
    }

    @PatchMapping("/{id}")
    public void patchBook(@PathVariable("id") UUID id, @RequestBody PatchBookRequest request) {
        try {
            Book book = service.getBookById(id);
            service.updateBook(requestToUpdateBook.apply(book, request));
        } catch (BookNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        } catch (BookValidationException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("/{id}")
    public void deleteBook(@PathVariable("id") UUID id) {
        try {
            service.deleteBook(id);
        } catch (BookNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
    }
}
