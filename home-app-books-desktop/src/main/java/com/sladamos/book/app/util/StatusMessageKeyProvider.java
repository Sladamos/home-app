package com.sladamos.book.app.util;

import com.sladamos.book.BookStatus;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@NoArgsConstructor
public class StatusMessageKeyProvider {

    public String getStatusMessageKey(BookStatus status) {
        return switch (status) {
            case ON_SHELF -> "books.items.status.onShelf";
            case WANT_TO_READ -> "books.items.status.wantToRead";
            case CURRENTLY_READING -> "books.items.status.currentlyReading";
            case FINISHED_READING -> "books.items.status.finishedReading";
            case BORROWED -> "books.items.status.borrowed";
        };
    }
}