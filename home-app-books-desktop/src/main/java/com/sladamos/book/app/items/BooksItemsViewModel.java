package com.sladamos.book.app.items;

import com.sladamos.book.BookService;
import com.sladamos.book.app.util.CoverImageProvider;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class BooksItemsViewModel {

    private final BookService bookService;

    private final CoverImageProvider coverImageProvider;

    @Getter
    private final ObservableList<BookItemViewModel> books = FXCollections.observableArrayList();

    @Transactional
    public void loadBooks() {
        books.clear();
        bookService.getAllBooks().stream()
                .map(book -> new BookItemViewModel(book, coverImageProvider.getImageCover(book.getCoverImage())))
                .forEach(books::add);
    }

}
