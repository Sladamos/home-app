package com.sladamos.book.app.items;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Comparator;

@Getter
@RequiredArgsConstructor
public enum BooksItemsSortOption {
    CREATE_DATE_DESC("books.items.sort.createDateDesc", Comparator.comparing(b -> b.getCreationDate().get())),
    CREATE_DATE_ASC("books.items.sort.createDateAsc", Comparator.comparing((BookItemViewModel b) -> b.getCreationDate().get()).reversed()),
    MODIFICATION_DATE_DESC("books.items.sort.modificationDateDesc", Comparator.comparing(b -> b.getModificationDate().get())),
    MODIFICATION_DATE_ASC("books.items.sort.modificationDateAsc", Comparator.comparing((BookItemViewModel b) -> b.getModificationDate().get()).reversed()),
    TITLE_DESC("books.items.sort.titleDesc", Comparator.comparing(b -> b.getTitle().get().toLowerCase())),
    TITLE_ASC("books.items.sort.titleAsc", Comparator.comparing((BookItemViewModel b) -> b.getTitle().get().toLowerCase()).reversed()),
    PAGES_DESC("books.items.sort.pagesDesc", Comparator.comparing(b -> b.getPages().get(), Comparator.nullsLast(Integer::compareTo))),
    PAGES_ASC("books.items.sort.pagesAsc", Comparator.comparing((BookItemViewModel b) -> b.getPages().get(), Comparator.nullsLast(Integer::compareTo)).reversed());

    private final String translationKey;
    private final Comparator<BookItemViewModel> comparator;
}
