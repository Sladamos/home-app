package com.sladamos.book.app.items;

import java.util.UUID;

public record OnBookDeleted(UUID bookId, String bookTitle) {
}
