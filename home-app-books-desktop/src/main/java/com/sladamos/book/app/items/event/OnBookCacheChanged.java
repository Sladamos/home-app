package com.sladamos.book.app.items.event;

import com.sladamos.book.model.BookEntity;

import java.util.UUID;

public sealed interface OnBookCacheChanged {

    record Created(BookEntity book) implements OnBookCacheChanged {
        public static Created of(BookEntity book) {
            return new Created(book);
        }
    }

    record Updated(BookEntity book) implements OnBookCacheChanged {
        public static Updated of(BookEntity book) {
            return new Updated(book);
        }
    }

    record Deleted(UUID bookId) implements OnBookCacheChanged {
        public static Deleted of(UUID id) {
            return new Deleted(id);
        }
    }
}