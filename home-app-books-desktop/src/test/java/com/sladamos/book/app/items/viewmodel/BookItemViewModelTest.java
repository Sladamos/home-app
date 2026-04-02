package com.sladamos.book.app.items.viewmodel;

import com.sladamos.book.app.util.NamedEntityFormatter;
import com.sladamos.book.model.Author;
import com.sladamos.book.model.Book;
import com.sladamos.book.model.BookStatus;
import com.sladamos.book.model.Genre;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class BookItemViewModelTest {

    private NamedEntityFormatter formatter;

    @BeforeEach
    void setUp() {
        formatter = new NamedEntityFormatter();
    }

    private BookItemViewModel createViewModel(Book book) {
        BookItemViewModel vm = new BookItemViewModel(formatter);
        vm.init(book);
        return vm;
    }

    @Nested
    class Init {

        @Test
        void shouldMapAllFieldsFromBook() {
            Book book = createBook();

            BookItemViewModel vm = createViewModel(book);

            assertThat(vm.getId().get()).isEqualTo(book.getId());
            assertThat(vm.getTitle().get()).isEqualTo(book.getTitle());
            assertThat(vm.getIsbn().get()).isEqualTo(book.getIsbn());
            assertThat(vm.getDescription().get()).isEqualTo(book.getDescription());
            assertThat(vm.getPublisher().get()).isEqualTo(book.getPublisher());
            assertThat(vm.getBorrowedBy().get()).isEqualTo(book.getBorrowedBy());
            assertThat(vm.getPages().get()).isEqualTo(book.getPages());
            assertThat(vm.getRating().get()).isEqualTo(book.getRating());
            assertThat(vm.getFavorite().get()).isEqualTo(book.isFavorite());
            assertThat(vm.getStatus().get()).isEqualTo(book.getStatus());
            assertThat(vm.getCoverImage().get()).isEqualTo(book.getCoverImage());
            assertThat(vm.getReadDate().get()).isEqualTo(book.getReadDate());
            assertThat(vm.getModificationDate().get()).isEqualTo(book.getModificationDate());
            assertThat(vm.getCreationDate().get()).isEqualTo(book.getCreationDate());
        }

        @Test
        void shouldFormatAuthorsUsingSortedNames() {
            Book book = createBookWithAuthors(Set.of(new Author("Zoe"), new Author("Alice")));

            BookItemViewModel vm = createViewModel(book);

            assertThat(vm.getAuthors().get()).isEqualTo("Alice, Zoe");
        }

        @Test
        void shouldFormatGenresUsingSortedNames() {
            Book book = createBookWithGenres(Set.of(new Genre("Sci-Fi"), new Genre("Drama")));

            BookItemViewModel vm = createViewModel(book);

            assertThat(vm.getGenres().get()).isEqualTo("Drama, Sci-Fi");
        }

        @Test
        void shouldDefaultRatingToZeroWhenNull() {
            Book book = createBook().toBuilder().rating(null).build();

            BookItemViewModel vm = createViewModel(book);

            assertThat(vm.getRating().get()).isZero();
        }
    }

    @Nested
    class GetBook {

        @Test
        void shouldReturnCopyPreservingAuthorsAndGenres() {
            Set<Author> authors = Set.of(new Author("Author One"), new Author("Author Two"));
            Set<Genre> genres = Set.of(new Genre("Genre One"));
            Book original = createBook().toBuilder().authors(authors).genres(genres).build();

            BookItemViewModel vm = createViewModel(original);
            Book result = vm.getBook();

            assertThat(result.getAuthors()).isEqualTo(authors);
            assertThat(result.getGenres()).isEqualTo(genres);
        }

        @Test
        void shouldReflectPropertyChangesInReturnedBook() {
            Book book = createBook();
            BookItemViewModel vm = createViewModel(book);

            vm.getTitle().set("New Title");
            vm.getRating().set(5);
            vm.getStatus().set(BookStatus.CURRENTLY_READING);

            Book result = vm.getBook();
            assertThat(result.getTitle()).isEqualTo("New Title");
            assertThat(result.getRating()).isEqualTo(5);
            assertThat(result.getStatus()).isEqualTo(BookStatus.CURRENTLY_READING);
        }

        @Test
        void shouldReturnNewInstanceEachTime() {
            Book book = createBook();
            BookItemViewModel vm = createViewModel(book);

            Book first = vm.getBook();
            Book second = vm.getBook();

            assertThat(first).isNotSameAs(second);
        }
    }

    @Nested
    class UpdateFrom {

        @Test
        void shouldUpdateAllFieldsFromNewBook() {
            Book original = createBook();
            BookItemViewModel vm = createViewModel(original);

            Book updated = original.toBuilder()
                    .title("Updated Title")
                    .rating(4)
                    .status(BookStatus.BORROWED)
                    .borrowedBy("John")
                    .authors(Set.of(new Author("New Author")))
                    .genres(Set.of(new Genre("New Genre")))
                    .build();

            vm.updateFrom(updated);

            assertThat(vm.getTitle().get()).isEqualTo("Updated Title");
            assertThat(vm.getRating().get()).isEqualTo(4);
            assertThat(vm.getStatus().get()).isEqualTo(BookStatus.BORROWED);
            assertThat(vm.getBorrowedBy().get()).isEqualTo("John");
            assertThat(vm.getAuthors().get()).isEqualTo("New Author");
            assertThat(vm.getGenres().get()).isEqualTo("New Genre");
        }

        @Test
        void shouldPreserveOriginalAuthorsInGetBookAfterUpdate() {
            Book original = createBook();
            BookItemViewModel vm = createViewModel(original);

            Set<Author> newAuthors = Set.of(new Author("Updated Author"));
            Book updated = original.toBuilder().authors(newAuthors).build();
            vm.updateFrom(updated);

            Book result = vm.getBook();
            assertThat(result.getAuthors()).isEqualTo(newAuthors);
        }
    }

    private Book createBook() {
        LocalDateTime now = LocalDateTime.now();
        return Book.builder()
                .id(UUID.randomUUID())
                .title("Test Book")
                .isbn("1234567890")
                .description("A test description")
                .publisher("Test Publisher")
                .borrowedBy("")
                .pages(200)
                .rating(3)
                .favorite(false)
                .status(BookStatus.ON_SHELF)
                .coverImage(new byte[]{1, 2, 3})
                .readDate(LocalDate.now())
                .creationDate(now)
                .modificationDate(now)
                .authors(Set.of(new Author("Author One")))
                .genres(Set.of(new Genre("Genre One")))
                .build();
    }

    private Book createBookWithAuthors(Set<Author> authors) {
        return createBook().toBuilder().authors(authors).build();
    }

    private Book createBookWithGenres(Set<Genre> genres) {
        return createBook().toBuilder().genres(genres).build();
    }
}

