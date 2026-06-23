package com.sladamos.book.app.modify.mode;

import com.sladamos.book.BookService;
import com.sladamos.book.app.modify.ModifyBookDataMapper;
import com.sladamos.book.app.modify.ModifyBookDraft;
import com.sladamos.book.app.modify.ModifyBookViewModel;
import com.sladamos.book.model.BookEntity;
import com.sladamos.book.model.BookStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class AddBookModeTest {

    @Mock
    private BookService bookService;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    private ModifyBookDataMapper dataMapper;
    private ModifyBookDraft draft;
    private AddBookMode mode;

    @BeforeEach
    void setUp() {
        dataMapper = new ModifyBookDataMapper();
        draft = new ModifyBookDraft();
        mode = new AddBookMode(bookService, eventPublisher, dataMapper, draft);
    }

    @Nested
    class Convert {

        @Test
        void shouldMapBasicFields() {
            ModifyBookViewModel vm = createViewModel();
            vm.getTitle().set("My Book");
            vm.getIsbn().set("123");
            vm.getPages().set(200);

            BookEntity result = mode.convert(vm);

            assertThat(result.getTitle()).isEqualTo("My Book");
            assertThat(result.getIsbn()).isEqualTo("123");
            assertThat(result.getPages()).isEqualTo(200);
        }

        @Test
        void shouldFilterBlankAuthors() {
            ModifyBookViewModel vm = createViewModel();
            vm.getAuthors().addAll("Alice", "", "  ", "Bob");

            BookEntity result = mode.convert(vm);

            assertThat(result.getAuthors()).hasSize(2);
            assertThat(result.getAuthors()).extracting("name")
                    .containsExactlyInAnyOrder("Alice", "Bob");
        }

        @Test
        void shouldFilterBlankGenres() {
            ModifyBookViewModel vm = createViewModel();
            vm.getGenres().addAll("Sci-Fi", "", "Fantasy");

            BookEntity result = mode.convert(vm);

            assertThat(result.getGenres()).hasSize(2);
            assertThat(result.getGenres()).extracting("name")
                    .containsExactlyInAnyOrder("Sci-Fi", "Fantasy");
        }
    }

    @Nested
    class ModeProperties {

        @Test
        void shouldReturnAddTitleKey() {
            assertThat(mode.getModifyBookLabel()).isEqualTo("books.add.name");
        }

        @Test
        void shouldReturnAddButtonKey() {
            assertThat(mode.getSubmitBookButtonKey()).isEqualTo("books.add.name");
        }
    }

    private ModifyBookViewModel createViewModel() {
        ModifyBookViewModel vm = new ModifyBookViewModel();
        vm.getStatus().set(BookStatus.ON_SHELF);
        return vm;
    }
}
