package com.sladamos.book.app.modify.event;

import com.sladamos.book.model.BookEntity;

public record OnBookCreated(BookEntity book) {
}
