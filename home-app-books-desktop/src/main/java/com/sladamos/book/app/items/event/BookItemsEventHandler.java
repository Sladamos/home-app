package com.sladamos.book.app.items.event;

import com.sladamos.app.util.message.BindingsCreator;
import com.sladamos.app.util.message.TemporaryMessagesFactory;
import com.sladamos.book.BookService;
import com.sladamos.book.app.items.BookCacheService;
import com.sladamos.book.app.items.viewmodel.BookItemsActiveState;
import com.sladamos.book.app.items.viewmodel.BookItemsViewModel;
import com.sladamos.book.app.modify.event.OnBookCreated;
import com.sladamos.book.app.modify.event.OnBookEdited;
import com.sladamos.common.exception.DuplicationException;
import com.sladamos.common.exception.NotFoundException;
import com.sladamos.common.exception.ValidationException;
import com.sladamos.book.model.BookEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class BookItemsEventHandler {

    private final BookService bookService;

    private final BookCacheService bookCacheService;

    private final BindingsCreator bindingsCreator;

    private final TemporaryMessagesFactory temporaryMessagesFactory;

    private final BookItemsActiveState activeState;

    @EventListener(OnBookCreated.class)
    @Order(1)
    public void onBookCreated(OnBookCreated event) {
        BookEntity book = event.book();
        log.info("Adding new book to items: [id: {}, title: {}]", book.getId(), book.getTitle());
        bookCacheService.addBook(book);
    }

    @EventListener(OnBookEdited.class)
    @Order(1)
    public void onBookEdited(OnBookEdited event) {
        BookEntity book = event.book();
        log.info("Updating book in items: [id: {}, title: {}]", book.getId(), book.getTitle());
        bookCacheService.updateBook(book);
    }

    @EventListener(OnBookDuplicated.class)
    public void onBookDuplicated(OnBookDuplicated event) {
        BookEntity book = event.book();
        log.info("Duplicating book in items: [id: {}, title: {}]", book.getId(), book.getTitle());
        try {
            List<String> existingTitles = bookCacheService.getBooks().stream()
                    .map(BookEntity::getTitle)
                    .distinct()
                    .toList();
            BookEntity duplicatedBook = bookService.duplicateBook(book, existingTitles);
            bookCacheService.addBook(duplicatedBook);
        } catch (ValidationException | DuplicationException e) {
            log.error("Unable to duplicate book: [reason: {}]", e.getMessage());
            temporaryMessagesFactory.showError(bindingsCreator.getMessage("books.items.duplicateBookError"));
        }
    }

    @EventListener(OnBookDeleted.class)
    public void onBookDeleted(OnBookDeleted event) {
        UUID bookId = event.bookId();
        String bookTitle = event.bookTitle();
        log.info("Deleting book: [id: {}, title: {}]", bookId, bookTitle);
        try {
            bookService.deleteBook(bookId);
        } catch (NotFoundException e) {
            log.error("Book not found in service, removing from view: [id: {}, title: {}]", bookId, bookTitle);
            temporaryMessagesFactory.showError(bindingsCreator.getMessage("books.items.deleteBookError"));
        } finally {
            bookCacheService.deleteBook(bookId);
        }
    }

    @EventListener(OnBookCacheChanged.class)
    public void onBookCacheChanged(OnBookCacheChanged event) {
        log.info("Book cache changed, refreshing items view");
        BookItemsViewModel activeViewModel = activeState.getActive();
        if (activeViewModel != null) {
            switch (event) {
                case OnBookCacheChanged.Created(BookEntity book) -> activeViewModel.addBook(book);
                case OnBookCacheChanged.Updated(BookEntity book) -> activeViewModel.updateBook(book);
                case OnBookCacheChanged.Deleted(UUID bookId) -> activeViewModel.deleteBook(bookId);
            }
        }
    }
}
