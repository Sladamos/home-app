package com.sladamos.book.app.items.viewmodel;

import com.sladamos.app.util.ui.NamedEntityFormatter;
import com.sladamos.book.app.items.BookCacheService;
import com.sladamos.book.model.AuthorEntity;
import com.sladamos.book.model.BookEntity;
import com.sladamos.book.model.BookStatus;
import com.sladamos.book.model.GenreEntity;
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
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BookItemsViewModelTest {

    @Mock
    private ObjectProvider<BookItemViewModel> viewModelProvider;

    @Mock
    private BookCacheService bookCacheService;

    @Mock
    private BookItemsActiveState activeState;

    private NamedEntityFormatter formatter;

    private BookItemsViewModel viewModel;

    @BeforeEach
    void setUp() {
        formatter = new NamedEntityFormatter();
        lenient().when(viewModelProvider.getObject()).thenAnswer(invocation -> new BookItemViewModel(formatter));
        when(bookCacheService.getBooks()).thenReturn(Collections.emptyList());
        
        viewModel = new BookItemsViewModel(viewModelProvider, activeState, bookCacheService);
        viewModel.init();
    }

    @Nested
    class AddBook {

        @Test
        void shouldAddBookToList() {
            BookEntity book = createBook("New Book");
            
            viewModel.addBook(book);
            
            assertThat(viewModel.getBooks()).hasSize(1);
            assertThat(viewModel.getBooks().getFirst().getTitle().get()).isEqualTo("New Book");
        }

        @Test
        void shouldResetSearchQueryAfterAddingBook() {
            viewModel.getSearchQuery().set("some search");
            BookEntity book = createBook("New Book");
            
            viewModel.addBook(book);
            
            assertThat(viewModel.getSearchQuery().get()).isEmpty();
        }
    }

    @Nested
    class DeleteBook {

        @Test
        void shouldRemoveBookById() {
            BookEntity book = createBook("To Delete");
            viewModel.addBook(book);
            UUID idToDelete = viewModel.getBooks().getFirst().getId().get();
            
            viewModel.deleteBook(idToDelete);
            
            assertThat(viewModel.getBooks()).isEmpty();
        }

        @Test
        void shouldNotRemoveOtherBooks() {
            BookEntity bookA = createBook("Book A");
            BookEntity bookB = createBook("Book B");
            viewModel.addBook(bookA);
            viewModel.addBook(bookB);
            UUID idA = viewModel.getBooks().stream()
                    .filter(vm -> vm.getTitle().get().equals("Book A"))
                    .findFirst().orElseThrow()
                    .getId().get();
            
            viewModel.deleteBook(idA);
            
            assertThat(viewModel.getBooks()).hasSize(1);
            assertThat(viewModel.getBooks().getFirst().getTitle().get()).isEqualTo("Book B");
        }
    }

    @Nested
    class UpdateBook {

        @Test
        void shouldUpdateExistingBookProperties() {
            BookEntity book = createBook("Original");
            viewModel.addBook(book);
            
            BookEntity updated = book.toBuilder().title("Updated").build();
            viewModel.updateBook(updated);
            
            assertThat(viewModel.getBooks().getFirst().getTitle().get()).isEqualTo("Updated");
        }
    }

    @Nested
    class SearchFiltering {

        @Test
        void shouldShowAllBooksWhenSearchQueryIsEmpty() {
            viewModel.addBook(createBook("Alpha"));
            viewModel.addBook(createBook("Beta"));
            
            viewModel.getSearchQuery().set("");
            
            assertThat(viewModel.getSortedBooks()).hasSize(2);
        }

        @Test
        void shouldFilterBooksByTitlePrefix() {
            viewModel.addBook(createBook("Alpha"));
            viewModel.addBook(createBook("Beta"));
            
            viewModel.getSearchQuery().set("Al");
            
            assertThat(viewModel.getSortedBooks()).hasSize(1);
            assertThat(viewModel.getSortedBooks().getFirst().getTitle().get()).isEqualTo("Alpha");
        }

        @Test
        void shouldFilterCaseInsensitively() {
            viewModel.addBook(createBook("Alpha"));
            viewModel.addBook(createBook("Beta"));
            
            viewModel.getSearchQuery().set("al");
            
            assertThat(viewModel.getSortedBooks()).hasSize(1);
            assertThat(viewModel.getSortedBooks().getFirst().getTitle().get()).isEqualTo("Alpha");
        }

        @Test
        void shouldShowNoBooksWhenNoTitleMatches() {
            viewModel.addBook(createBook("Alpha"));
            viewModel.addBook(createBook("Beta"));
            
            viewModel.getSearchQuery().set("Gamma");
            
            assertThat(viewModel.getSortedBooks()).isEmpty();
        }
    }

    private BookEntity createBook(String title) {
        LocalDateTime now = LocalDateTime.now();
        return BookEntity.builder()
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
                .authors(Set.of(new AuthorEntity("Author")))
                .genres(Set.of(new GenreEntity("Genre")))
                .build();
    }
}

