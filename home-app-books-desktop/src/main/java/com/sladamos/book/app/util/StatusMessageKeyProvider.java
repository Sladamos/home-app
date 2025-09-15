package com.sladamos.book.app.util;

import com.sladamos.book.BookStatus;
import org.springframework.stereotype.Component;

@Component
public class StatusMessageKeyProvider {

    public String getDisplayStatusMessageKey(BookStatus status) {
        return switch (status) {
            case ON_SHELF -> "books.items.status.onShelf";
            case WANT_TO_READ -> "books.items.status.wantToRead";
            case CURRENTLY_READING -> "books.items.status.currentlyReading";
            case FINISHED_READING -> "books.items.status.finishedReading";
            case BORROWED -> "books.items.status.borrowed";
        };
    }

    public String getAddStatusMessageKey(BookStatus status) {
        return switch (status) {
            case ON_SHELF -> "books.selectStatus.onShelf";
            case WANT_TO_READ -> "books.selectStatus.wantToRead";
            case CURRENTLY_READING -> "books.selectStatus.currentlyReading";
            case FINISHED_READING -> "books.selectStatus.finishedReading";
            case BORROWED -> "books.selectStatus.borrowed";
        };
    }
}