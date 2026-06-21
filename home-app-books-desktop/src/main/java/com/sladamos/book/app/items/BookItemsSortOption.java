package com.sladamos.book.app.items;

import com.sladamos.book.app.items.viewmodel.BookItemViewModel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Comparator;

@Getter
@RequiredArgsConstructor
public enum BookItemsSortOption {
    CREATE_DATE_DESC("books.items.sort.createDateDesc", Comparator.comparing((BookItemViewModel b) -> b.getCreationDate().get()).reversed()),
    CREATE_DATE_ASC("books.items.sort.createDateAsc", Comparator.comparing(b -> b.getCreationDate().get())),
    MODIFICATION_DATE_DESC("books.items.sort.modificationDateDesc", Comparator.comparing((BookItemViewModel b) -> b.getModificationDate().get()).reversed()),
    MODIFICATION_DATE_ASC("books.items.sort.modificationDateAsc", Comparator.comparing(b -> b.getModificationDate().get())),
    TITLE_DESC("books.items.sort.titleDesc", Comparator.comparing((BookItemViewModel b) -> b.getTitle().get().toLowerCase()).reversed()),
    TITLE_ASC("books.items.sort.titleAsc", Comparator.comparing(b -> b.getTitle().get().toLowerCase())),
    PAGES_DESC("books.items.sort.pagesDesc", Comparator.comparing((BookItemViewModel b) -> b.getPages().get(), Comparator.nullsLast(Integer::compareTo)).reversed()),
    PAGES_ASC("books.items.sort.pagesAsc", Comparator.comparing(b -> b.getPages().get(), Comparator.nullsLast(Integer::compareTo)));

    private final String translationKey;
    private final Comparator<BookItemViewModel> comparator;
}
