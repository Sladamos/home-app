package com.sladamos.book.app.items;

import com.sladamos.book.app.items.viewmodel.BookItemViewModel;
import com.sladamos.app.util.ui.NamedEntityFormatter;
import com.sladamos.book.model.Author;
import com.sladamos.book.model.Book;
import com.sladamos.book.model.BookStatus;
import com.sladamos.book.model.Genre;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class BookItemsSortOptionTest {

    private static final NamedEntityFormatter FORMATTER = new NamedEntityFormatter();
    private static final LocalDateTime EARLIER = LocalDateTime.of(2025, 1, 1, 0, 0);
    private static final LocalDateTime LATER = LocalDateTime.of(2025, 6, 1, 0, 0);

    @Nested
    class CreateDateSort {

        @Test
        void descShouldPlaceNewerFirst() {
            BookItemViewModel newer = createVM("A", LATER, LATER, 100);
            BookItemViewModel older = createVM("B", EARLIER, EARLIER, 200);

            int result = compare(BookItemsSortOption.CREATE_DATE_DESC, newer, older);

            assertThat(result).isNegative();
        }

        @Test
        void ascShouldPlaceOlderFirst() {
            BookItemViewModel newer = createVM("A", LATER, LATER, 100);
            BookItemViewModel older = createVM("B", EARLIER, EARLIER, 200);

            int result = compare(BookItemsSortOption.CREATE_DATE_ASC, older, newer);

            assertThat(result).isNegative();
        }
    }

    @Nested
    class ModificationDateSort {

        @Test
        void descShouldPlaceNewerFirst() {
            BookItemViewModel newer = createVM("A", EARLIER, LATER, 100);
            BookItemViewModel older = createVM("B", EARLIER, EARLIER, 200);

            int result = compare(BookItemsSortOption.MODIFICATION_DATE_DESC, newer, older);

            assertThat(result).isNegative();
        }

        @Test
        void ascShouldPlaceOlderFirst() {
            BookItemViewModel newer = createVM("A", EARLIER, LATER, 100);
            BookItemViewModel older = createVM("B", EARLIER, EARLIER, 200);

            int result = compare(BookItemsSortOption.MODIFICATION_DATE_ASC, older, newer);

            assertThat(result).isNegative();
        }
    }

    @Nested
    class TitleSort {

        @Test
        void descShouldPlaceZFirst() {
            BookItemViewModel a = createVM("Alpha", EARLIER, EARLIER, 100);
            BookItemViewModel z = createVM("Zulu", EARLIER, EARLIER, 100);

            int result = compare(BookItemsSortOption.TITLE_DESC, z, a);

            assertThat(result).isNegative();
        }

        @Test
        void ascShouldPlaceAFirst() {
            BookItemViewModel a = createVM("Alpha", EARLIER, EARLIER, 100);
            BookItemViewModel z = createVM("Zulu", EARLIER, EARLIER, 100);

            int result = compare(BookItemsSortOption.TITLE_ASC, a, z);

            assertThat(result).isNegative();
        }

        @Test
        void shouldBeCaseInsensitive() {
            BookItemViewModel lower = createVM("alpha", EARLIER, EARLIER, 100);
            BookItemViewModel upper = createVM("Alpha", EARLIER, EARLIER, 100);

            int result = compare(BookItemsSortOption.TITLE_ASC, lower, upper);

            assertThat(result).isZero();
        }
    }

    @Nested
    class PagesSort {

        @Test
        void descShouldPlaceMorePagesFirst() {
            BookItemViewModel more = createVM("A", EARLIER, EARLIER, 500);
            BookItemViewModel less = createVM("B", EARLIER, EARLIER, 100);

            int result = compare(BookItemsSortOption.PAGES_DESC, more, less);

            assertThat(result).isNegative();
        }

        @Test
        void ascShouldPlaceFewerPagesFirst() {
            BookItemViewModel more = createVM("A", EARLIER, EARLIER, 500);
            BookItemViewModel less = createVM("B", EARLIER, EARLIER, 100);

            int result = compare(BookItemsSortOption.PAGES_ASC, less, more);

            assertThat(result).isNegative();
        }
    }

    @Nested
    class TranslationKeys {

        @Test
        void allOptionsShouldHaveNonBlankTranslationKey() {
            for (BookItemsSortOption option : BookItemsSortOption.values()) {
                assertThat(option.getTranslationKey()).isNotBlank();
            }
        }
    }

    private int compare(BookItemsSortOption option, BookItemViewModel a, BookItemViewModel b) {
        Comparator<BookItemViewModel> comparator = option.getComparator();
        return comparator.compare(a, b);
    }

    private BookItemViewModel createVM(String title, LocalDateTime creationDate, LocalDateTime modificationDate, int pages) {
        Book book = Book.builder()
                .id(UUID.randomUUID())
                .title(title)
                .isbn("")
                .description("")
                .publisher("")
                .borrowedBy("")
                .pages(pages)
                .rating(3)
                .favorite(false)
                .status(BookStatus.ON_SHELF)
                .coverImage(new byte[]{})
                .readDate(LocalDate.now())
                .creationDate(creationDate)
                .modificationDate(modificationDate)
                .authors(Set.of(new Author("Author")))
                .genres(Set.of(new Genre("Genre")))
                .build();
        BookItemViewModel vm = new BookItemViewModel(FORMATTER);
        vm.init(book);
        return vm;
    }
}

