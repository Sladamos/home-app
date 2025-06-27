package com.sladamos.book.app.items;

import com.sladamos.book.Book;
import com.sladamos.book.BookStatus;
import com.sladamos.book.app.RateableViewModel;
import javafx.beans.property.*;
import javafx.scene.image.Image;
import lombok.Getter;

import java.time.Instant;
import java.time.LocalDate;
import java.util.Optional;

@Getter
public class BookItemViewModel implements RateableViewModel {

    private final StringProperty title = new SimpleStringProperty();
    private final StringProperty isbn = new SimpleStringProperty();
    private final StringProperty description = new SimpleStringProperty();
    private final StringProperty publisher = new SimpleStringProperty();
    private final StringProperty borrowedBy = new SimpleStringProperty();
    private final IntegerProperty pages = new SimpleIntegerProperty();
    private final IntegerProperty rating = new SimpleIntegerProperty();
    private final BooleanProperty favorite = new SimpleBooleanProperty();
    private final StringProperty authors = new SimpleStringProperty();
    private final StringProperty genres = new SimpleStringProperty();
    private final ObjectProperty<Image> coverImage = new SimpleObjectProperty<>();
    private final ObjectProperty<BookStatus> status = new SimpleObjectProperty<>();
    private final ObjectProperty<LocalDate> readDate = new SimpleObjectProperty<>();
    private final ObjectProperty<Instant> modificationDate = new SimpleObjectProperty<>();
    private final ObjectProperty<Instant> creationDate = new SimpleObjectProperty<>();

    public BookItemViewModel(Book book, Image coverImage) {
        title.set(book.getTitle());
        isbn.set(book.getIsbn());
        description.set(book.getDescription());
        publisher.set(book.getPublisher());
        borrowedBy.set(book.getBorrowedBy());
        pages.set(book.getPages());
        rating.set(Optional.ofNullable(book.getRating()).orElse(0));
        favorite.set(book.isFavorite());
        status.set(book.getStatus());
        authors.set(String.join(", ", book.getAuthors()));
        genres.set(String.join(", ", book.getGenres()));
        this.coverImage.set(coverImage);
        readDate.set(book.getReadDate());
        modificationDate.set(book.getModificationDate());
        creationDate.set(book.getCreationDate());
    }
}
