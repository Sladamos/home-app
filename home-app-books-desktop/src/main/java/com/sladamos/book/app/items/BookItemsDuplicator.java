package com.sladamos.book.app.items;

import com.sladamos.book.model.Book;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class BookItemsDuplicator {

    private static final Pattern COUNTER_SUFFIX = Pattern.compile(" \\((\\d+)\\)$");

    public Book duplicate(Book book, List<String> existingTitles) {
        String originalTitle = book.getTitle();
        validateBookExists(originalTitle, existingTitles);

        String baseTitle = extractBaseTitle(originalTitle);
        long nextNumber = findNextNumber(baseTitle, existingTitles);
        String newTitle = String.format("%s (%d)", baseTitle, nextNumber);

        LocalDateTime now = LocalDateTime.now();
        return book.toBuilder()
                .id(UUID.randomUUID())
                .title(newTitle)
                .creationDate(now)
                .modificationDate(now)
                .build();
    }

    private void validateBookExists(String title, List<String> existingTitles) {
        if (!existingTitles.contains(title)) {
            throw new IllegalArgumentException("Book with title '" + title + "' does not exist in the list");
        }
    }

    private String extractBaseTitle(String title) {
        return COUNTER_SUFFIX.matcher(title).replaceAll("");
    }

    private long extractCounter(String title) {
        Matcher matcher = COUNTER_SUFFIX.matcher(title);
        return matcher.find() ? Long.parseLong(matcher.group(1)) : 0;
    }

    private long findNextNumber(String baseTitle, List<String> existingTitles) {
        return existingTitles.stream()
                .mapToLong(title -> {
                    if (title.equals(baseTitle)) return 0;
                    if (extractBaseTitle(title).equals(baseTitle)) return extractCounter(title);
                    return -1;
                })
                .max()
                .orElse(-1) + 1;
    }
}
