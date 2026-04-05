package com.sladamos.book.app.modify.mode;

import com.sladamos.book.BookService;
import com.sladamos.book.app.modify.ModifyBookViewModel;
import com.sladamos.book.model.Author;
import com.sladamos.book.model.Book;
import com.sladamos.book.model.BookStatus;
import com.sladamos.book.model.Genre;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class EditBookModeTest {

    @Mock
    private BookService bookService;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @Nested
    class Convert {

        @Test
        void shouldPreserveOriginalCreationDate() {
            Book originalBook = createBook();
            EditBookMode mode = createMode(originalBook);
            ModifyBookViewModel vm = createViewModel(originalBook);

            Book result = mode.convert(vm);

            assertThat(result.getCreationDate()).isEqualTo(originalBook.getCreationDate());
        }

        @Test
        void shouldUpdateModificationDate() {
            Book originalBook = createBook();
            EditBookMode mode = createMode(originalBook);
            ModifyBookViewModel vm = createViewModel(originalBook);
            LocalDateTime before = LocalDateTime.now();

            Book result = mode.convert(vm);

            assertThat(result.getModificationDate()).isAfterOrEqualTo(before);
        }

        @Test
        void shouldPreserveOriginalId() {
            Book originalBook = createBook();
            EditBookMode mode = createMode(originalBook);
            ModifyBookViewModel vm = createViewModel(originalBook);

            Book result = mode.convert(vm);

            assertThat(result.getId()).isEqualTo(originalBook.getId());
        }

        @Test
        void shouldMapEditedFields() {
            Book originalBook = createBook();
            EditBookMode mode = createMode(originalBook);
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
            EditBookMode mode = createMode(createBook());
            assertThat(mode.getModifyBookLabel()).isEqualTo("books.edit.name");
        }

        @Test
        void shouldReturnEditButtonKey() {
            EditBookMode mode = createMode(createBook());
            assertThat(mode.getSubmitBookButtonKey()).isEqualTo("books.edit.name");
        }

        @Test
        void shouldNotResetAfterSubmit() {
            EditBookMode mode = createMode(createBook());
            assertThat(mode.shouldResetAfterSubmit()).isFalse();
        }
    }

    private EditBookMode createMode(Book book) {
        EditBookMode mode = new EditBookMode(bookService, eventPublisher);
        mode.init(book);
        return mode;
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