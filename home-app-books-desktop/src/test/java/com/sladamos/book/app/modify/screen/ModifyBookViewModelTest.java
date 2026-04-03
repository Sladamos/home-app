package com.sladamos.book.app.modify.screen;

import com.sladamos.book.app.modify.ModifyBookViewModel;
import com.sladamos.book.model.Author;
import com.sladamos.book.model.Book;
import com.sladamos.book.model.BookStatus;
import com.sladamos.book.model.Genre;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class ModifyBookViewModelTest {

    @Nested
    class DefaultState {

        @Test
        void shouldHaveDefaultStatus() {
            ModifyBookViewModel vm = new ModifyBookViewModel();
            assertThat(vm.getStatus().get()).isEqualTo(BookStatus.ON_SHELF);
        }

        @Test
        void shouldHaveEmptyCollections() {
            ModifyBookViewModel vm = new ModifyBookViewModel();
            assertThat(vm.getAuthors()).isEmpty();
            assertThat(vm.getGenres()).isEmpty();
        }

        @Test
        void shouldHaveNonNullId() {
            ModifyBookViewModel vm = new ModifyBookViewModel();
            assertThat(vm.getId().get()).isNotNull();
        }
    }

    @Nested
    class InitFrom {

        @Test
        void shouldMapAllScalarFields() {
            Book book = createBook();
            ModifyBookViewModel vm = new ModifyBookViewModel();

            vm.initFrom(book);

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
            assertThat(vm.getReadDate().get()).isEqualTo(book.getReadDate());
            assertThat(vm.getCreationDate().get()).isEqualTo(book.getCreationDate());
            assertThat(vm.getCoverImage().get()).isEqualTo(book.getCoverImage());
        }

        @Test
        void shouldMapAuthorsAsStrings() {
            Book book = createBook().toBuilder()
                    .authors(Set.of(new Author("Alice"), new Author("Bob")))
                    .build();
            ModifyBookViewModel vm = new ModifyBookViewModel();

            vm.initFrom(book);

            assertThat(vm.getAuthors()).containsExactlyInAnyOrder("Alice", "Bob");
        }

        @Test
        void shouldMapGenresAsStrings() {
            Book book = createBook().toBuilder()
                    .genres(Set.of(new Genre("Sci-Fi"), new Genre("Fantasy")))
                    .build();
            ModifyBookViewModel vm = new ModifyBookViewModel();

            vm.initFrom(book);

            assertThat(vm.getGenres()).containsExactlyInAnyOrder("Sci-Fi", "Fantasy");
        }

        @Test
        void shouldReplaceExistingCollections() {
            ModifyBookViewModel vm = new ModifyBookViewModel();
            vm.getAuthors().add("OldAuthor");

            Book book = createBook().toBuilder()
                    .authors(Set.of(new Author("NewAuthor")))
                    .build();
            vm.initFrom(book);

            assertThat(vm.getAuthors()).containsExactly("NewAuthor");
        }
    }

    @Nested
    class Reset {

        @Test
        void shouldClearAllFieldsToDefaults() {
            ModifyBookViewModel vm = new ModifyBookViewModel();
            vm.initFrom(createBook());

            vm.reset();

            assertThat(vm.getTitle().get()).isEmpty();
            assertThat(vm.getIsbn().get()).isEmpty();
            assertThat(vm.getDescription().get()).isEmpty();
            assertThat(vm.getPublisher().get()).isEmpty();
            assertThat(vm.getPages().get()).isZero();
            assertThat(vm.getRating().get()).isZero();
            assertThat(vm.getFavorite().get()).isFalse();
            assertThat(vm.getStatus().get()).isEqualTo(BookStatus.ON_SHELF);
            assertThat(vm.getReadDate().get()).isNull();
            assertThat(vm.getCoverImage().get()).isNull();
            assertThat(vm.getAuthors()).isEmpty();
            assertThat(vm.getGenres()).isEmpty();
        }

        @Test
        void shouldGenerateNewId() {
            ModifyBookViewModel vm = new ModifyBookViewModel();
            vm.initFrom(createBook());
            UUID idBeforeReset = vm.getId().get();

            vm.reset();

            assertThat(vm.getId().get()).isNotEqualTo(idBeforeReset);
        }
    }

    private Book createBook() {
        LocalDateTime now = LocalDateTime.now();
        return Book.builder()
                .id(UUID.randomUUID())
                .title("Test Book")
                .isbn("1234567890")
                .description("A test book")
                .publisher("Test Publisher")
                .borrowedBy("Someone")
                .pages(300)
                .rating(4)
                .favorite(true)
                .readDate(LocalDate.of(2024, 1, 15))
                .creationDate(now.minusDays(10))
                .modificationDate(now)
                .coverImage(new byte[]{1, 2, 3})
                .status(BookStatus.FINISHED_READING)
                .authors(Set.of(new Author("Author One")))
                .genres(Set.of(new Genre("Genre One")))
                .build();
    }
}
