package com.sladamos.book.app.modify;

import com.sladamos.book.Book;
import com.sladamos.book.BookStatus;
import com.sladamos.book.app.modify.components.SelectCoverViewModel;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Component
@Getter
@NoArgsConstructor
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
    private boolean isEditMode = false;

    public ModifyBookViewModel(Book book) {
        this.isEditMode = true;
        this.id.set(book.getId());
        this.title.set(book.getTitle());
        this.isbn.set(book.getIsbn());
        this.description.set(book.getDescription());
        this.publisher.set(book.getPublisher());
        this.borrowedBy.set(book.getBorrowedBy());
        this.pages.set(book.getPages());
        this.rating.set(book.getRating());
        this.favorite.set(book.isFavorite());
        this.readDate.set(book.getReadDate());
        this.creationDate.set(book.getCreationDate());
        this.coverImage.set(book.getCoverImage());
        this.status.set(book.getStatus());
        this.authors.addAll(book.getAuthors());
        this.genres.addAll(book.getGenres());
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