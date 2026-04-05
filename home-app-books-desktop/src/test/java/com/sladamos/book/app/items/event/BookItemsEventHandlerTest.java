package com.sladamos.book.app.items.event;

import com.sladamos.app.util.message.BindingsCreator;
import com.sladamos.app.util.message.TemporaryMessagesFactory;
import com.sladamos.book.BookService;
import com.sladamos.book.app.items.BookCacheService;
import com.sladamos.book.app.items.viewmodel.BookItemsActiveState;
import com.sladamos.book.app.modify.event.OnBookCreated;
import com.sladamos.book.app.modify.event.OnBookEdited;
import com.sladamos.book.exception.BookDuplicationException;
import com.sladamos.book.exception.BookNotFoundException;
import com.sladamos.book.exception.BookValidationException;
import com.sladamos.book.model.Book;
import com.sladamos.book.model.BookStatus;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookItemsEventHandlerTest {

    @Mock
    private BookService bookService;

    @Mock
    private BookCacheService bookCacheService;

    @Mock
    private BindingsCreator bindingsCreator;

    @Mock
    private TemporaryMessagesFactory temporaryMessagesFactory;

    @Mock
    private BookItemsActiveState activeState;

    @InjectMocks
    private BookItemsEventHandler handler;

    @Nested
    class OnBookCreatedEvent {

        @Test
        void shouldAddBookToCache() {
            Book book = createBook("New Book");

            handler.onBookCreated(new OnBookCreated(book));

            verify(bookCacheService).addBook(book);
        }
    }

    @Nested
    class OnBookEditedEvent {

        @Test
        void shouldUpdateBookInCache() {
            Book book = createBook("Edited Book");

            handler.onBookEdited(new OnBookEdited(book));

            verify(bookCacheService).updateBook(book);
        }
    }

    @Nested
    class OnBookDeletedEvent {

        @Test
        void shouldDeleteFromCacheAfterService() throws BookNotFoundException {
            UUID bookId = UUID.randomUUID();

            handler.onBookDeleted(new OnBookDeleted(bookId, "Book Title"));

            verify(bookService).deleteBook(bookId);
            verify(bookCacheService).deleteBook(bookId);
        }

        @Test
        void shouldStillDeleteFromCacheWhenServiceThrows() throws BookNotFoundException {
            UUID bookId = UUID.randomUUID();
            doThrow(new BookNotFoundException("Not found")).when(bookService).deleteBook(bookId);

            handler.onBookDeleted(new OnBookDeleted(bookId, "Book Title"));

            verify(bookCacheService).deleteBook(bookId);
        }
    }

    @Nested
    class OnBookDuplicatedEvent {

        @Test
        void shouldDuplicateAndSaveBook() throws BookValidationException, BookDuplicationException {
            Book book = createBook("Original");
            Book duplicatedBook = book.toBuilder().id(UUID.randomUUID()).build();
            when(bookCacheService.getBooks()).thenReturn(List.of(book));
            when(bookService.duplicateBook(eq(book), anyList())).thenReturn(duplicatedBook);

            handler.onBookDuplicated(new OnBookDuplicated(book));

            verify(bookService).duplicateBook(eq(book), anyList());
            verify(bookCacheService).addBook(duplicatedBook);
        }

        @Test
        void shouldShowErrorOnValidationException() throws BookValidationException, BookDuplicationException {
            Book book = createBook("Original");
            when(bookCacheService.getBooks()).thenReturn(Collections.emptyList());
            when(bookService.duplicateBook(any(), anyList()))
                    .thenThrow(new BookValidationException(Collections.emptySet()));
            when(bindingsCreator.getMessage("books.items.duplicateBookError"))
                    .thenReturn("Duplication failed");

            handler.onBookDuplicated(new OnBookDuplicated(book));

            verify(temporaryMessagesFactory).showError("Duplication failed");
        }

        @Test
        void shouldShowErrorWhenDuplicationFails() throws BookValidationException, BookDuplicationException {
            Book book = createBook("Original");
            when(bookCacheService.getBooks()).thenReturn(Collections.emptyList());
            when(bookService.duplicateBook(any(), anyList()))
                    .thenThrow(new BookDuplicationException("Failed"));
            when(bindingsCreator.getMessage("books.items.duplicateBookError"))
                    .thenReturn("Duplication failed");

            handler.onBookDuplicated(new OnBookDuplicated(book));

            verify(temporaryMessagesFactory).showError("Duplication failed");
        }
    }

    private Book createBook(String title) {
        LocalDateTime now = LocalDateTime.now();
        return Book.builder()
                .id(UUID.randomUUID())
                .title(title)
                .isbn("1234567890")
                .description("Desc")
                .publisher("Publisher")
                .borrowedBy("")
                .pages(300)
                .rating(4)
                .favorite(false)
                .readDate(LocalDate.now())
                .creationDate(now)
                .modificationDate(now)
                .coverImage(new byte[]{})
                .status(BookStatus.ON_SHELF)
                .authors(Collections.emptySet())
                .genres(Collections.emptySet())
                .build();
    }
}
