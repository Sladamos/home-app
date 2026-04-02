package com.sladamos.book.app.items.viewmodel;

import com.sladamos.book.BookService;
import com.sladamos.app.util.ui.NamedEntityFormatter;
import com.sladamos.book.model.Author;
import com.sladamos.book.model.Book;
import com.sladamos.book.model.BookStatus;
import com.sladamos.book.model.Genre;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.ObjectProvider;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BooksItemsViewModelTest {

    @Mock
    private ObjectProvider<BookItemViewModel> viewModelProvider;

    @Mock
    private BookService bookService;

    private NamedEntityFormatter formatter;

    private BooksItemsViewModel viewModel;

    @BeforeEach
    void setUp() {
        formatter = new NamedEntityFormatter();
        lenient().when(viewModelProvider.getObject()).thenAnswer(invocation -> new BookItemViewModel(formatter));
        viewModel = new BooksItemsViewModel(viewModelProvider, bookService);
        viewModel.init();
    }

    @Nested
    class AreBooksNotLoaded {

        @Test
        void shouldReturnTrueBeforeLoadingBooks() {
            assertThat(viewModel.areBooksNotLoaded()).isTrue();
        }

        @Test
        void shouldReturnFalseAfterLoadingEmptyList() {
            when(bookService.getAllBooks()).thenReturn(Collections.emptyList());

            viewModel.loadBooks();

            assertThat(viewModel.areBooksNotLoaded()).isFalse();
        }

        @Test
        void shouldReturnFalseAfterLoadingBooks() {
            when(bookService.getAllBooks()).thenReturn(List.of(createBook("Book A")));

            viewModel.loadBooks();

            assertThat(viewModel.areBooksNotLoaded()).isFalse();
        }
    }

    @Nested
    class LoadBooks {

        @Test
        void shouldPopulateSortedBooksAfterLoading() {
            when(bookService.getAllBooks()).thenReturn(List.of(createBook("Book A"), createBook("Book B")));

            viewModel.loadBooks();

            assertThat(viewModel.getSortedBooks()).hasSize(2);
        }

        @Test
        void shouldClearPreviousBooksOnReload() {
            when(bookService.getAllBooks()).thenReturn(List.of(createBook("Book A")));
            viewModel.loadBooks();

            when(bookService.getAllBooks()).thenReturn(List.of(createBook("Book B")));
            viewModel.loadBooks();

            assertThat(viewModel.getSortedBooks()).hasSize(1);
            assertThat(viewModel.getSortedBooks().getFirst().getTitle().get()).isEqualTo("Book B");
        }
    }

    @Nested
    class AddBook {

        @Test
        void shouldAddBookToSortedBooks() {
            when(bookService.getAllBooks()).thenReturn(Collections.emptyList());
            viewModel.loadBooks();

            viewModel.addBook(createBook("New Book"));

            assertThat(viewModel.getSortedBooks()).hasSize(1);
            assertThat(viewModel.getSortedBooks().getFirst().getTitle().get()).isEqualTo("New Book");
        }
    }

    @Nested
    class DeleteBook {

        @Test
        void shouldRemoveBookById() {
            Book book = createBook("To Delete");
            when(bookService.getAllBooks()).thenReturn(List.of(book));
            viewModel.loadBooks();
            UUID idToDelete = viewModel.getSortedBooks().getFirst().getId().get();

            viewModel.deleteBook(idToDelete);

            assertThat(viewModel.getSortedBooks()).isEmpty();
        }

        @Test
        void shouldNotRemoveOtherBooks() {
            Book bookA = createBook("Book A");
            Book bookB = createBook("Book B");
            when(bookService.getAllBooks()).thenReturn(List.of(bookA, bookB));
            viewModel.loadBooks();
            UUID idA = viewModel.getSortedBooks().stream()
                    .filter(vm -> vm.getTitle().get().equals("Book A"))
                    .findFirst().orElseThrow()
                    .getId().get();

            viewModel.deleteBook(idA);

            assertThat(viewModel.getSortedBooks()).hasSize(1);
            assertThat(viewModel.getSortedBooks().getFirst().getTitle().get()).isEqualTo("Book B");
        }
    }

    @Nested
    class UpdateBook {

        @Test
        void shouldUpdateExistingBookProperties() {
            Book book = createBook("Original");
            when(bookService.getAllBooks()).thenReturn(List.of(book));
            viewModel.loadBooks();

            Book updated = book.toBuilder().title("Updated").build();
            viewModel.updateBook(updated);

            assertThat(viewModel.getSortedBooks().getFirst().getTitle().get()).isEqualTo("Updated");
        }
    }

    @Nested
    class SearchFiltering {

        @Test
        void shouldShowAllBooksWhenSearchQueryIsEmpty() {
            when(bookService.getAllBooks()).thenReturn(List.of(createBook("Alpha"), createBook("Beta")));
            viewModel.loadBooks();

            viewModel.getSearchQuery().set("");

            assertThat(viewModel.getSortedBooks()).hasSize(2);
        }

        @Test
        void shouldFilterBooksByTitlePrefix() {
            when(bookService.getAllBooks()).thenReturn(List.of(createBook("Alpha"), createBook("Beta")));
            viewModel.loadBooks();

            viewModel.getSearchQuery().set("Al");

            assertThat(viewModel.getSortedBooks()).hasSize(1);
            assertThat(viewModel.getSortedBooks().getFirst().getTitle().get()).isEqualTo("Alpha");
        }

        @Test
        void shouldFilterCaseInsensitively() {
            when(bookService.getAllBooks()).thenReturn(List.of(createBook("Alpha"), createBook("Beta")));
            viewModel.loadBooks();

            viewModel.getSearchQuery().set("al");

            assertThat(viewModel.getSortedBooks()).hasSize(1);
            assertThat(viewModel.getSortedBooks().getFirst().getTitle().get()).isEqualTo("Alpha");
        }

        @Test
        void shouldShowNoBooksWhenNoTitleMatches() {
            when(bookService.getAllBooks()).thenReturn(List.of(createBook("Alpha"), createBook("Beta")));
            viewModel.loadBooks();

            viewModel.getSearchQuery().set("Gamma");

            assertThat(viewModel.getSortedBooks()).isEmpty();
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
                .authors(Set.of(new Author("Author")))
                .genres(Set.of(new Genre("Genre")))
                .build();
    }
}
