package com.sladamos.book.app.add;

import com.sladamos.book.Book;
import com.sladamos.book.BookStatus;
import com.sladamos.book.app.common.SelectCoverViewModel;
import io.micrometer.common.util.StringUtils;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import lombok.Getter;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Component
@Getter
public class AddBookViewModel implements SelectCoverViewModel {
    private final StringProperty title = new SimpleStringProperty();
    private final StringProperty isbn = new SimpleStringProperty();
    private final StringProperty description = new SimpleStringProperty();
    private final StringProperty publisher = new SimpleStringProperty();
    private final StringProperty borrowedBy = new SimpleStringProperty();
    private final IntegerProperty pages = new SimpleIntegerProperty();
    private final IntegerProperty rating = new SimpleIntegerProperty();
    private final BooleanProperty favorite = new SimpleBooleanProperty();
    private final ObjectProperty<LocalDate> readDate = new SimpleObjectProperty<>();
    private final ObjectProperty<byte[]> coverImage = new SimpleObjectProperty<>();
    private final ObjectProperty<BookStatus> status = new SimpleObjectProperty<>(BookStatus.ON_SHELF);
    private final ObservableList<String> authors = FXCollections.observableArrayList();
    private final ObservableList<String> genres = FXCollections.observableArrayList();

    public Book toBook() {
        return Book.builder()
                .id(UUID.randomUUID())
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
                .authors(authors.stream().filter(StringUtils::isNotBlank).toList())
                .genres(genres.stream().filter(StringUtils::isNotBlank).toList())
                .creationDate(Instant.now())
                .modificationDate(Instant.now())
                .build();
    }

    public void reset() {
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
        status.set(BookStatus.ON_SHELF);
        authors.clear();
        genres.clear();
    }
}