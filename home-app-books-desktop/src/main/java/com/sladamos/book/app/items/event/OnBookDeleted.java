package com.sladamos.book.app.items.event;

import java.util.UUID;

public record OnBookDeleted(UUID bookId, String bookTitle) {
}
