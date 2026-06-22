package com.sladamos.book.app.modify.mode;

import com.sladamos.book.BookService;
import com.sladamos.book.app.modify.ModifyBookDataMapper;
import com.sladamos.book.app.modify.ModifyBookDraft;
import com.sladamos.book.app.modify.ModifyBookViewModel;
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

    private ModifyBookDataMapper dataMapper;
    private ModifyBookDraft draft;

    @BeforeEach
    void setUp() {
        dataMapper = new ModifyBookDataMapper();
        draft = new ModifyBookDraft();
    }

    @Nested
    class Convert {

        @Test
        void shouldPreserveOriginalCreationDate() {
            BookEntity originalBook = createBook();
            EditBookMode mode = createMode(originalBook);
            ModifyBookViewModel vm = createViewModel(originalBook);

            BookEntity result = mode.convert(vm);

            assertThat(result.getCreationDate()).isEqualTo(originalBook.getCreationDate());
        }

        @Test
        void shouldUpdateModificationDate() {
            BookEntity originalBook = createBook();
            EditBookMode mode = createMode(originalBook);
            ModifyBookViewModel vm = createViewModel(originalBook);
            LocalDateTime before = LocalDateTime.now();

            BookEntity result = mode.convert(vm);

            assertThat(result.getModificationDate()).isAfterOrEqualTo(before);
        }

        @Test
        void shouldPreserveOriginalId() {
            BookEntity originalBook = createBook();
            EditBookMode mode = createMode(originalBook);
            ModifyBookViewModel vm = createViewModel(originalBook);

            BookEntity result = mode.convert(vm);

            assertThat(result.getId()).isEqualTo(originalBook.getId());
        }

        @Test
        void shouldMapEditedFields() {
            BookEntity originalBook = createBook();
            EditBookMode mode = createMode(originalBook);
            ModifyBookViewModel vm = createViewModel(originalBook);
            vm.getTitle().set("Updated Title");
            vm.getPages().set(999);

            BookEntity result = mode.convert(vm);

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
    }

    private EditBookMode createMode(BookEntity book) {
        ModifyBookDraft editDraft = new ModifyBookDraft();
        dataMapper.updateDraftFromBook(editDraft, book);
        EditBookMode mode = new EditBookMode(bookService, eventPublisher, dataMapper, editDraft);
        mode.init(book);
        return mode;
    }

    private ModifyBookViewModel createViewModel(BookEntity book) {
        ModifyBookViewModel vm = new ModifyBookViewModel();
        ModifyBookDraft tempDraft = new ModifyBookDraft();
        dataMapper.updateDraftFromBook(tempDraft, book);
        dataMapper.updateViewModelFromDraft(vm, tempDraft);
        return vm;
    }

    private BookEntity createBook() {
        return BookEntity.builder()
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
                .authors(Set.of(new AuthorEntity("Author")))
                .genres(Set.of(new GenreEntity("Genre")))
                .build();
    }
}
