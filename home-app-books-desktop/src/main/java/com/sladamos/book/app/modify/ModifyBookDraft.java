package com.sladamos.book.app.modify;

import com.sladamos.book.model.BookStatus;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class ModifyBookDraft {
    private UUID id;
    private String title;
    private String isbn;
    private String description;
    private String publisher;
    private String borrowedBy;
    private int pages;
    private int rating;
    private boolean favorite;
    private LocalDate readDate;
    private byte[] coverImage;
    private BookStatus status;
    private final Set<String> authors = new HashSet<>();
    private final Set<String> genres = new HashSet<>();

    public ModifyBookDraft() {
        reset();
    }

    public void reset() {
        this.id = UUID.randomUUID();
        this.title = "";
        this.isbn = "";
        this.description = "";
        this.publisher = "";
        this.borrowedBy = "";
        this.pages = 0;
        this.rating = 0;
        this.favorite = false;
        this.readDate = null;
        this.coverImage = null;
        this.status = BookStatus.ON_SHELF;
        authors.clear();
        genres.clear();
    }
}
