package com.sladamos.book.app.items.event;

import com.sladamos.book.model.BookEntity;

public record OnEditBookClicked(BookEntity book) {
}
