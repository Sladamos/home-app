package com.sladamos.book.app.items;

import com.sladamos.book.Book;
import com.sladamos.book.BookService;
import com.sladamos.book.app.add.OnBookCreated;
import com.sladamos.book.app.util.CoverImageProvider;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.function.Function;

@Slf4j
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
                .map(toViewModel())
                .forEach(books::add);
    }

    private Function<Book, BookItemViewModel> toViewModel() {
        return book -> new BookItemViewModel(book, coverImageProvider.getImageCover(book.getCoverImage()));
    }

    @EventListener
    @Order(1)
    public void onBookCreated(OnBookCreated event) {
        Book book = event.book();
        log.info("Adding new book to items: [id:{}, title:{}]", book.getId(), book.getTitle());
        books.add(toViewModel().apply(book));
    }

}
