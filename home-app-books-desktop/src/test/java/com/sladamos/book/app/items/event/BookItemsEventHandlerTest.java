package com.sladamos.book.app.items.event;

import com.sladamos.app.util.messages.BindingsCreator;
import com.sladamos.app.util.messages.TemporaryMessagesFactory;
import com.sladamos.book.BookService;
import com.sladamos.book.app.modify.event.OnBookCreated;
import com.sladamos.book.app.modify.event.OnBookEdited;
import com.sladamos.book.app.items.viewmodel.BookItemViewModel;
import com.sladamos.book.app.items.viewmodel.BookItemsViewModel;
import com.sladamos.book.exception.BookDuplicationException;
import com.sladamos.book.exception.BookNotFoundException;
import com.sladamos.book.exception.BookValidationException;
import com.sladamos.book.model.Book;
import com.sladamos.book.model.BookStatus;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookItemsEventHandlerTest {

    @Mock
    private BookService bookService;

    @Mock
    private BookItemsViewModel viewModel;

    @Mock
    private BindingsCreator bindingsCreator;

    @Mock
    private TemporaryMessagesFactory temporaryMessagesFactory;

    @InjectMocks
    private BookItemsEventHandler handler;

    @Nested
    class OnBookCreatedEvent {

        private StringProperty searchQuery;

        @BeforeEach
        void setUp() {
            searchQuery = new SimpleStringProperty("some search");
            when(viewModel.getSearchQuery()).thenReturn(searchQuery);
        }

        @Test
        void shouldAddBookToViewModel() {
            Book book = createBook("New Book");

            handler.onBookCreated(new OnBookCreated(book));

            verify(viewModel).addBook(book);
        }

        @Test
        void shouldClearSearchQuery() {
            Book book = createBook("New Book");

            handler.onBookCreated(new OnBookCreated(book));

            assertThat(searchQuery.get()).isEmpty();
        }
    }

    @Nested
    class OnBookEditedEvent {

        @Test
        void shouldUpdateBookInViewModel() {
            Book book = createBook("Edited Book");

            handler.onBookEdited(new OnBookEdited(book));

            verify(viewModel).updateBook(book);
        }
    }

    @Nested
    class OnBookDuplicatedEvent {

        @Test
        void shouldDuplicateAndSaveBook() throws BookValidationException, BookDuplicationException {
            Book original = createBook("Eragon");
            Book duplicated = createBook("Eragon (1)");
            BookItemViewModel vm = mock(BookItemViewModel.class);
            when(vm.getTitle()).thenReturn(new SimpleStringProperty("Eragon"));
            when(viewModel.getSortedBooks()).thenReturn(new javafx.collections.transformation.SortedList<>(javafx.collections.FXCollections.observableArrayList(vm)));
            when(bookService.duplicateBook(eq(original), eq(List.of("Eragon")))).thenReturn(duplicated);

            handler.onBookDuplicated(new OnBookDuplicated(original));

            verify(viewModel).addBook(duplicated);
        }

        @Test
        void shouldShowErrorOnValidationException() throws BookValidationException, BookDuplicationException {
            Book original = createBook("Eragon");
            BookItemViewModel vm = mock(BookItemViewModel.class);
            when(vm.getTitle()).thenReturn(new SimpleStringProperty("Eragon"));
            when(viewModel.getSortedBooks()).thenReturn(new javafx.collections.transformation.SortedList<>(javafx.collections.FXCollections.observableArrayList(vm)));
            doThrow(new BookValidationException(Set.of())).when(bookService).duplicateBook(eq(original), eq(List.of("Eragon")));
            when(bindingsCreator.getMessage(anyString())).thenReturn("Error message");

            handler.onBookDuplicated(new OnBookDuplicated(original));

            verify(temporaryMessagesFactory).showError("Error message");
            verify(viewModel, never()).addBook(any());
        }

        @Test
        void shouldShowErrorWhenDuplicationFails() throws BookValidationException, BookDuplicationException {
            Book original = createBook("Eragon");
            BookItemViewModel vm = mock(BookItemViewModel.class);
            when(vm.getTitle()).thenReturn(new SimpleStringProperty("Eragon"));
            when(viewModel.getSortedBooks()).thenReturn(new javafx.collections.transformation.SortedList<>(javafx.collections.FXCollections.observableArrayList(vm)));
            doThrow(new BookDuplicationException("Duplication failed")).when(bookService).duplicateBook(eq(original), eq(List.of("Eragon")));
            when(bindingsCreator.getMessage(anyString())).thenReturn("Error message");

            handler.onBookDuplicated(new OnBookDuplicated(original));

            verify(temporaryMessagesFactory).showError("Error message");
            verify(viewModel, never()).addBook(any());
        }
    }

    @Nested
    class OnBookDeletedEvent {

        @Test
        void shouldDeleteFromServiceAndViewModel() throws BookNotFoundException {
            UUID bookId = UUID.randomUUID();

            handler.onBookDeleted(new OnBookDeleted(bookId, "Test Book"));

            verify(bookService).deleteBook(bookId);
            verify(viewModel).deleteBook(bookId);
        }

        @Test
        void shouldStillDeleteFromViewModelWhenServiceThrows() throws BookNotFoundException {
            UUID bookId = UUID.randomUUID();
            doThrow(new BookNotFoundException("not found")).when(bookService).deleteBook(bookId);
            when(bindingsCreator.getMessage(anyString())).thenReturn("Error message");

            handler.onBookDeleted(new OnBookDeleted(bookId, "Missing Book"));

            verify(viewModel).deleteBook(bookId);
            verify(temporaryMessagesFactory).showError("Error message");
        }
    }

    private Book createBook(String title) {
        LocalDateTime now = LocalDateTime.now();
        return Book.builder()
                .id(UUID.randomUUID())
                .title(title)
                .isbn("")
                .description("")
                .publisher("")
                .borrowedBy("")
                .pages(100)
                .rating(3)
                .favorite(false)
                .status(BookStatus.ON_SHELF)
                .coverImage(new byte[]{})
                .readDate(LocalDate.now())
                .creationDate(now)
                .modificationDate(now)
                .authors(Set.of())
                .genres(Set.of())
                .build();
    }
}
