package com.sladamos.book.app.items.viewmodel;

import com.sladamos.app.util.ui.NamedEntityFormatter;
import com.sladamos.book.model.AuthorEntity;
import com.sladamos.book.model.BookEntity;
import com.sladamos.book.model.BookStatus;
import com.sladamos.book.model.GenreEntity;
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

    private BookItemViewModel createViewModel(BookEntity book) {
        BookItemViewModel vm = new BookItemViewModel(formatter);
        vm.init(book);
        return vm;
    }

    @Nested
    class Init {

        @Test
        void shouldMapAllFieldsFromBook() {
            BookEntity book = createBook();

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
            BookEntity book = createBookWithAuthors(Set.of(new AuthorEntity("Zoe"), new AuthorEntity("Alice")));

            BookItemViewModel vm = createViewModel(book);

            assertThat(vm.getAuthors().get()).isEqualTo("Alice, Zoe");
        }

        @Test
        void shouldFormatGenresUsingSortedNames() {
            BookEntity book = createBookWithGenres(Set.of(new GenreEntity("Sci-Fi"), new GenreEntity("Drama")));

            BookItemViewModel vm = createViewModel(book);

            assertThat(vm.getGenres().get()).isEqualTo("Drama, Sci-Fi");
        }

        @Test
        void shouldDefaultRatingToZeroWhenNull() {
            BookEntity book = createBook().toBuilder().rating(null).build();

            BookItemViewModel vm = createViewModel(book);

            assertThat(vm.getRating().get()).isZero();
        }
    }

    @Nested
    class GetBook {

        @Test
        void shouldReturnCopyPreservingAuthorsAndGenres() {
            Set<AuthorEntity> authors = Set.of(new AuthorEntity("Author One"), new AuthorEntity("Author Two"));
            Set<GenreEntity> genres = Set.of(new GenreEntity("Genre One"));
            BookEntity original = createBook().toBuilder().authors(authors).genres(genres).build();

            BookItemViewModel vm = createViewModel(original);
            BookEntity result = vm.getBook();

            assertThat(result.getAuthors()).isEqualTo(authors);
            assertThat(result.getGenres()).isEqualTo(genres);
        }

        @Test
        void shouldReflectPropertyChangesInReturnedBook() {
            BookEntity book = createBook();
            BookItemViewModel vm = createViewModel(book);

            vm.getTitle().set("New Title");
            vm.getRating().set(5);
            vm.getStatus().set(BookStatus.CURRENTLY_READING);

            BookEntity result = vm.getBook();
            assertThat(result.getTitle()).isEqualTo("New Title");
            assertThat(result.getRating()).isEqualTo(5);
            assertThat(result.getStatus()).isEqualTo(BookStatus.CURRENTLY_READING);
        }

        @Test
        void shouldReturnNewInstanceEachTime() {
            BookEntity book = createBook();
            BookItemViewModel vm = createViewModel(book);

            BookEntity first = vm.getBook();
            BookEntity second = vm.getBook();

            assertThat(first).isNotSameAs(second);
        }
    }

    @Nested
    class UpdateFrom {

        @Test
        void shouldUpdateAllFieldsFromNewBook() {
            BookEntity original = createBook();
            BookItemViewModel vm = createViewModel(original);

            BookEntity updated = original.toBuilder()
                    .title("Updated Title")
                    .rating(4)
                    .status(BookStatus.BORROWED)
                    .borrowedBy("John")
                    .authors(Set.of(new AuthorEntity("New Author")))
                    .genres(Set.of(new GenreEntity("New Genre")))
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
            BookEntity original = createBook();
            BookItemViewModel vm = createViewModel(original);

            Set<AuthorEntity> newAuthors = Set.of(new AuthorEntity("Updated Author"));
            BookEntity updated = original.toBuilder().authors(newAuthors).build();
            vm.updateFrom(updated);

            BookEntity result = vm.getBook();
            assertThat(result.getAuthors()).isEqualTo(newAuthors);
        }
    }

    private BookEntity createBook() {
        LocalDateTime now = LocalDateTime.now();
        return BookEntity.builder()
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
                .authors(Set.of(new AuthorEntity("Author One")))
                .genres(Set.of(new GenreEntity("Genre One")))
                .build();
    }

    private BookEntity createBookWithAuthors(Set<AuthorEntity> authors) {
        return createBook().toBuilder().authors(authors).build();
    }

    private BookEntity createBookWithGenres(Set<GenreEntity> genres) {
        return createBook().toBuilder().genres(genres).build();
    }
}

