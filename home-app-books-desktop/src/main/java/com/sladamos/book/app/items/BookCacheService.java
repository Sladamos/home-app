package com.sladamos.book.app.items;

import com.sladamos.book.BookService;
import com.sladamos.book.app.items.event.OnBookCacheChanged;
import com.sladamos.book.model.Book;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
@Slf4j
@RequiredArgsConstructor
public class BookCacheService {

    private final BookService bookService;

    private final ApplicationEventPublisher eventPublisher;

    private final List<Book> books = new java.util.ArrayList<>();

    private boolean booksLoaded = false;

    public List<Book> getBooks() {
        log.info("Obtaining books to cache");
        loadBooksIfNeeded();
        log.info("Books obtained: [size: {}]", books.size());
        return books;
    }

    public void addBook(Book book) {
        log.info("Adding book to cache: [id: {}, title: {}]", book.getId(), book.getTitle());
        books.add(book);
        eventPublisher.publishEvent(OnBookCacheChanged.Created.of(book));
    }

    public void updateBook(Book book) {
        log.info("Updating book in cache: [id: {}]", book.getId());
        books.stream()
                .filter(e -> e.getId().equals(book.getId()))
                .findFirst()
                .ifPresent(e -> {
                    e.replace(book);
                    eventPublisher.publishEvent(OnBookCacheChanged.Updated.of(book));
                });
    }

    public void deleteBook(UUID bookId) {
        log.info("Deleting book from cache: [id: {}]", bookId);
        books.removeIf(b -> b.getId().equals(bookId));
        eventPublisher.publishEvent(OnBookCacheChanged.Deleted.of(bookId));
    }

    private void loadBooksIfNeeded() {
        if (!booksLoaded) {
            log.info("Loading books to cache");
            books.clear();
            books.addAll(bookService.getAllBooks());
            booksLoaded = true;
        }
    }
}