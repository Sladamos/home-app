package com.sladamos.book.app.items;

import com.sladamos.book.Book;
import com.sladamos.book.BookStatus;
import com.sladamos.book.app.RateableViewModel;
import javafx.beans.property.*;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Getter
public class BookItemViewModel implements RateableViewModel {

    private final ObjectProperty<UUID> id = new SimpleObjectProperty<>();
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
    private final ObjectProperty<byte[]> coverImage = new SimpleObjectProperty<>();
    private final ObjectProperty<BookStatus> status = new SimpleObjectProperty<>();
    private final ObjectProperty<LocalDate> readDate = new SimpleObjectProperty<>();
    private final ObjectProperty<LocalDateTime> modificationDate = new SimpleObjectProperty<>();
    private final ObjectProperty<LocalDateTime> creationDate = new SimpleObjectProperty<>();

    public BookItemViewModel(Book book) {
        id.set(book.getId());
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
        coverImage.set(book.getCoverImage());
        readDate.set(book.getReadDate());
        modificationDate.set(book.getModificationDate());
        creationDate.set(book.getCreationDate());
    }

    public Book getBook() {
        return Book.builder()
                .id(id.get())
                .title(title.get())
                .isbn(isbn.get())
                .description(description.get())
                .publisher(publisher.get())
                .borrowedBy(borrowedBy.get())
                .pages(pages.get())
                .rating(rating.get())
                .favorite(favorite.get())
                .readDate(readDate.get())
                .coverImage(coverImage.get())
                .status(status.get())
                .authors(authors.get().isEmpty() ? List.of() : List.of(authors.get().split(", ")))
                .genres(genres.get().isEmpty() ? List.of() : List.of(genres.get().split(", ")))
                .modificationDate(modificationDate.get())
                .creationDate(creationDate.get())
                .build();
    }

    public void updateFrom(Book book) {
        id.set(book.getId());
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
        coverImage.set(book.getCoverImage());
        readDate.set(book.getReadDate());
        modificationDate.set(book.getModificationDate());
        creationDate.set(book.getCreationDate());
    }
}
