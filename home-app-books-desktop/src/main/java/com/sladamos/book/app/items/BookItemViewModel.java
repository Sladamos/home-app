package com.sladamos.book.app.items;

import com.sladamos.book.Book;
import javafx.beans.property.*;
import lombok.Getter;

import java.util.Optional;

@Getter
public class BookItemViewModel {

    private final StringProperty title = new SimpleStringProperty();
    private final StringProperty isbn = new SimpleStringProperty();
    private final StringProperty description = new SimpleStringProperty();
    private final StringProperty publisher = new SimpleStringProperty();
    private final StringProperty borrowedTo = new SimpleStringProperty();
    private final IntegerProperty pages = new SimpleIntegerProperty();
    private final IntegerProperty rating = new SimpleIntegerProperty();
    private final BooleanProperty favorite = new SimpleBooleanProperty();
    private final StringProperty status = new SimpleStringProperty();
    private final StringProperty authors = new SimpleStringProperty();
    private final StringProperty genres = new SimpleStringProperty();

    public BookItemViewModel(Book book) {
        title.set(book.getTitle());
        isbn.set(book.getIsbn());
        description.set(book.getDescription());
        publisher.set(book.getPublisher());
        borrowedTo.set(book.getBorrowedTo());
        pages.set(book.getPages());
        rating.set(Optional.ofNullable(book.getRating()).orElse(0));
        favorite.set(book.isFavorite());
        status.set(book.getStatus() != null ? book.getStatus().name() : "");
        authors.set(String.join(", ", book.getAuthors()));
        genres.set(String.join(", ", book.getGenres()));
    }
}
