package com.sladamos.book;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sladamos.book.model.Author;
import com.sladamos.book.model.Book;
import com.sladamos.book.model.Genre;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class BookBackupService {private final BookRepository bookRepository;

    private final ObjectMapper objectMapper;
    private final String defaultPath = System.getProperty("user.home") + File.separator + "book_backup.json";

    @Transactional
    public void createBackup(String filePath) throws IOException {
        String targetPath = (filePath != null && !filePath.isEmpty()) ? filePath : defaultPath;
        log.info("Creating backup in " + targetPath);
        List<Book> books = bookRepository.findAll();
        objectMapper.writeValue(new File(targetPath), books);
        log.info("Successfully created backup in " + targetPath);
    }

    @Transactional
    public void restoreBackup(String filePath) throws IOException {
        String targetPath = (filePath != null && !filePath.isEmpty()) ? filePath : defaultPath;
        log.info("Restoring backup from " + targetPath);

        List<Book> booksFromJson = objectMapper.readValue(new File(targetPath),
                objectMapper.getTypeFactory().constructCollectionType(List.class, Book.class));

        Map<String, Author> uniqueAuthorsCache = new HashMap<>();
        Map<String, Genre> uniqueGenresCache = new HashMap<>();

        for (Book book : booksFromJson) {
            if (book.getAuthors() != null) {
                List<Author> mergedAuthors = new ArrayList<>();
                for (Author rawAuthor : book.getAuthors()) {
                    String authorName = rawAuthor.getName().trim();
                    uniqueAuthorsCache.putIfAbsent(authorName, rawAuthor);
                    mergedAuthors.add(uniqueAuthorsCache.get(authorName));
                }
                book.setAuthors(mergedAuthors);
            }

            if (book.getGenres() != null) {
                List<Genre> mergedGenres = new ArrayList<>();
                for (Genre rawGenre : book.getGenres()) {
                    String genreName = rawGenre.getName().trim();
                    uniqueGenresCache.putIfAbsent(genreName, rawGenre);
                    mergedGenres.add(uniqueGenresCache.get(genreName));
                }
                book.setGenres(mergedGenres);
            }
        }

        log.info("Found unique authors: {}", uniqueAuthorsCache.size());
        log.info("Found unique genres: {}", uniqueGenresCache.size());

        bookRepository.saveAll(booksFromJson);
        log.info("Successfully restored backup from " + targetPath);
    }
}