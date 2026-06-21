package com.sladamos.book.app.modify;

import com.sladamos.book.model.Author;
import com.sladamos.book.model.Book;
import com.sladamos.book.model.BookStatus;
import com.sladamos.book.model.Genre;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
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
    class Properties {

        @Test
        void shouldSetAndGetTitle() {
            ModifyBookViewModel vm = new ModifyBookViewModel();
            vm.getTitle().set("Test Book");
            assertThat(vm.getTitle().get()).isEqualTo("Test Book");
        }

        @Test
        void shouldSetAndGetAllFields() {
            ModifyBookViewModel vm = new ModifyBookViewModel();
            UUID id = UUID.randomUUID();
            
            vm.getId().set(id);
            vm.getTitle().set("Test");
            vm.getIsbn().set("123");
            vm.getPages().set(100);
            vm.getRating().set(5);
            vm.getFavorite().set(true);
            
            assertThat(vm.getId().get()).isEqualTo(id);
            assertThat(vm.getTitle().get()).isEqualTo("Test");
            assertThat(vm.getIsbn().get()).isEqualTo("123");
            assertThat(vm.getPages().get()).isEqualTo(100);
            assertThat(vm.getRating().get()).isEqualTo(5);
            assertThat(vm.getFavorite().get()).isTrue();
        }

        @Test
        void shouldHandleCollections() {
            ModifyBookViewModel vm = new ModifyBookViewModel();
            vm.getAuthors().addAll("Alice", "Bob");
            vm.getGenres().addAll("Sci-Fi", "Adventure");
            
            assertThat(vm.getAuthors()).containsExactlyInAnyOrder("Alice", "Bob");
            assertThat(vm.getGenres()).containsExactlyInAnyOrder("Sci-Fi", "Adventure");
        }
    }
}

