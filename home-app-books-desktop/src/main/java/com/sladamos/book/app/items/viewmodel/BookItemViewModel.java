package com.sladamos.book.app.items.viewmodel;

import com.sladamos.book.app.util.NamedEntityFormatter;
import com.sladamos.book.app.util.RateableViewModel;
import com.sladamos.book.model.Author;
import com.sladamos.book.model.Book;
import com.sladamos.book.model.BookStatus;
import com.sladamos.book.model.Genre;
import javafx.beans.property.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Getter
@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
@RequiredArgsConstructor
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

    private final NamedEntityFormatter formatter;
    private Book book;

    public void init(Book book) {
        applyFrom(book);
    }

    public Book getBook() {
        return book.toBuilder()
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
                .modificationDate(modificationDate.get())
                .creationDate(creationDate.get())
                .build();
    }

    public void updateFrom(Book book) {
        applyFrom(book);
    }

    private void applyFrom(Book book) {
        this.book = book;
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
        authors.set(formatter.format(book.getAuthors(), Author::getName));
        genres.set(formatter.format(book.getGenres(), Genre::getName));
        coverImage.set(book.getCoverImage());
        readDate.set(book.getReadDate());
        modificationDate.set(book.getModificationDate());
        creationDate.set(book.getCreationDate());
    }
}
