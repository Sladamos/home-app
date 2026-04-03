package com.sladamos.book.app.modify.mode.edit;

import com.sladamos.book.app.modify.mode.EditBookMode;
import com.sladamos.book.app.modify.ModifyBookViewModel;
import com.sladamos.book.model.Author;
import com.sladamos.book.model.Book;
import com.sladamos.book.model.BookStatus;
import com.sladamos.book.model.Genre;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class EditBookModeTest {

    @Nested
    class Convert {

        @Test
        void shouldPreserveOriginalCreationDate() {
            Book originalBook = createBook();
            EditBookMode mode = new EditBookMode(originalBook);
            ModifyBookViewModel vm = createViewModel(originalBook);

            Book result = mode.convert(vm);

            assertThat(result.getCreationDate()).isEqualTo(originalBook.getCreationDate());
        }

        @Test
        void shouldUpdateModificationDate() {
            Book originalBook = createBook();
            EditBookMode mode = new EditBookMode(originalBook);
            ModifyBookViewModel vm = createViewModel(originalBook);
            LocalDateTime before = LocalDateTime.now();

            Book result = mode.convert(vm);

            assertThat(result.getModificationDate()).isAfterOrEqualTo(before);
        }

        @Test
        void shouldPreserveOriginalId() {
            Book originalBook = createBook();
            EditBookMode mode = new EditBookMode(originalBook);
            ModifyBookViewModel vm = createViewModel(originalBook);

            Book result = mode.convert(vm);

            assertThat(result.getId()).isEqualTo(originalBook.getId());
        }

        @Test
        void shouldMapEditedFields() {
            Book originalBook = createBook();
            EditBookMode mode = new EditBookMode(originalBook);
            ModifyBookViewModel vm = createViewModel(originalBook);
            vm.getTitle().set("Updated Title");
            vm.getPages().set(999);

            Book result = mode.convert(vm);

            assertThat(result.getTitle()).isEqualTo("Updated Title");
            assertThat(result.getPages()).isEqualTo(999);
        }
    }

    @Nested
    class ModeProperties {

        @Test
        void shouldReturnEditTitleKey() {
            EditBookMode mode = new EditBookMode(createBook());
            assertThat(mode.getModifyBookLabel()).isEqualTo("books.edit.name");
        }

        @Test
        void shouldReturnEditButtonKey() {
            EditBookMode mode = new EditBookMode(createBook());
            assertThat(mode.getSubmitBookButtonKey()).isEqualTo("books.edit.name");
        }

        @Test
        void shouldNotResetAfterSubmit() {
            EditBookMode mode = new EditBookMode(createBook());
            assertThat(mode.shouldResetAfterSubmit()).isFalse();
        }
    }

    private ModifyBookViewModel createViewModel(Book book) {
        ModifyBookViewModel vm = new ModifyBookViewModel();
        vm.initFrom(book);
        return vm;
    }

    private Book createBook() {
        return Book.builder()
                .id(UUID.randomUUID())
                .title("Original Title")
                .isbn("1234567890")
                .description("Desc")
                .publisher("Publisher")
                .borrowedBy("")
                .pages(300)
                .rating(4)
                .favorite(false)
                .creationDate(LocalDateTime.of(2023, 1, 1, 12, 0))
                .modificationDate(LocalDateTime.of(2024, 6, 15, 10, 0))
                .status(BookStatus.ON_SHELF)
                .authors(Set.of(new Author("Author")))
                .genres(Set.of(new Genre("Genre")))
                .build();
    }
}
