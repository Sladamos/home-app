package com.sladamos.book.app.items.event;

import com.sladamos.app.util.messages.BindingsCreator;
import com.sladamos.app.util.messages.TemporaryMessagesFactory;
import com.sladamos.book.exception.BookDuplicationException;
import com.sladamos.book.exception.BookNotFoundException;
import com.sladamos.book.BookService;
import com.sladamos.book.exception.BookValidationException;
import com.sladamos.book.app.add.OnBookCreated;
import com.sladamos.book.app.edit.OnBookEdited;
import com.sladamos.book.app.items.viewmodel.BooksItemsViewModel;
import com.sladamos.book.model.Book;
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

    private final BooksItemsViewModel viewModel;

    private final BindingsCreator bindingsCreator;

    private final TemporaryMessagesFactory temporaryMessagesFactory;

    @EventListener(OnBookCreated.class)
    @Order(1)
    public void onBookCreated(OnBookCreated event) {
        Book book = event.book();
        log.info("Adding new book to items: [id: {}, title: {}]", book.getId(), book.getTitle());
        viewModel.addBook(book);
        viewModel.getSearchQuery().setValue("");
    }

    @EventListener(OnBookEdited.class)
    @Order(1)
    public void onBookEdited(OnBookEdited event) {
        Book book = event.book();
        log.info("Updating book in items: [id: {}, title: {}]", book.getId(), book.getTitle());
        viewModel.updateBook(book);
    }

    @EventListener(OnBookDuplicated.class)
    public void onBookDuplicated(OnBookDuplicated event) {
        Book book = event.book();
        log.info("Duplicating book in items: [id: {}, title: {}]", book.getId(), book.getTitle());
        try {
            List<String> existingTitles = viewModel.getSortedBooks().stream()
                    .map(vm -> vm.getTitle().get())
                    .distinct()
                    .toList();
            Book duplicatedBook = bookService.duplicateBook(book, existingTitles);
            viewModel.addBook(duplicatedBook);
        } catch (BookValidationException | BookDuplicationException e) {
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
        } catch (BookNotFoundException e) {
            log.error("Book not found in service, removing from view: [id: {}, title: {}]", bookId, bookTitle);
            temporaryMessagesFactory.showError(bindingsCreator.getMessage("books.items.deleteBookError"));
        } finally {
            viewModel.deleteBook(bookId);
        }
    }
}
