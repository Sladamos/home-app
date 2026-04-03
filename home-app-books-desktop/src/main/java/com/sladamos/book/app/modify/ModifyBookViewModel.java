package com.sladamos.book.app.modify;

import com.sladamos.book.model.Book;
import com.sladamos.book.model.BookStatus;
import com.sladamos.book.model.Author;
import com.sladamos.book.model.Genre;
import com.sladamos.book.app.modify.component.cover.SelectCoverViewModel;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
public class ModifyBookViewModel implements SelectCoverViewModel {

    private final StringProperty title = new SimpleStringProperty();
    private final StringProperty isbn = new SimpleStringProperty();
    private final StringProperty description = new SimpleStringProperty();
    private final StringProperty publisher = new SimpleStringProperty();
    private final StringProperty borrowedBy = new SimpleStringProperty();
    private final IntegerProperty pages = new SimpleIntegerProperty();
    private final IntegerProperty rating = new SimpleIntegerProperty();
    private final BooleanProperty favorite = new SimpleBooleanProperty();
    private final ObjectProperty<LocalDate> readDate = new SimpleObjectProperty<>();
    private final ObjectProperty<LocalDateTime> creationDate = new SimpleObjectProperty<>();
    private final ObjectProperty<byte[]> coverImage = new SimpleObjectProperty<>();
    private final ObjectProperty<UUID> id = new SimpleObjectProperty<>(UUID.randomUUID());
    private final ObjectProperty<BookStatus> status = new SimpleObjectProperty<>(BookStatus.ON_SHELF);
    private final ObservableList<String> authors = FXCollections.observableArrayList();
    private final ObservableList<String> genres = FXCollections.observableArrayList();

    public void initFrom(Book book) {
        id.set(book.getId());
        title.set(book.getTitle());
        isbn.set(book.getIsbn());
        description.set(book.getDescription());
        publisher.set(book.getPublisher());
        borrowedBy.set(book.getBorrowedBy());
        pages.set(book.getPages());
        rating.set(book.getRating());
        favorite.set(book.isFavorite());
        readDate.set(book.getReadDate());
        creationDate.set(book.getCreationDate());
        coverImage.set(book.getCoverImage());
        status.set(book.getStatus());
        authors.setAll(book.getAuthors().stream().map(Author::getName).toList());
        genres.setAll(book.getGenres().stream().map(Genre::getName).toList());
    }

    public void reset() {
        id.set(UUID.randomUUID());
        title.set("");
        isbn.set("");
        description.set("");
        publisher.set("");
        borrowedBy.set("");
        pages.set(0);
        rating.set(0);
        favorite.set(false);
        readDate.set(null);
        coverImage.set(null);
        creationDate.set(null);
        status.set(BookStatus.ON_SHELF);
        authors.clear();
        genres.clear();
    }
}
