package com.sladamos.book.app.items.event;

import com.sladamos.book.model.Book;

import java.util.UUID;

public sealed interface OnBookCacheChanged {

    record Created(Book book) implements OnBookCacheChanged {
        public static Created of(Book book) {
            return new Created(book);
        }
    }

    record Updated(Book book) implements OnBookCacheChanged {
        public static Updated of(Book book) {
            return new Updated(book);
        }
    }

    record Deleted(UUID bookId) implements OnBookCacheChanged {
        public static Deleted of(UUID id) {
            return new Deleted(id);
        }
    }
}