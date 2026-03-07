package com.sladamos.book;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sladamos.book.model.Author;
import com.sladamos.book.model.Book;
import com.sladamos.book.model.Genre;
import com.sladamos.book.repository.AuthorRepository;
import com.sladamos.book.repository.BookRepository;
import com.sladamos.book.repository.GenreRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class BookBackupService {

    private final BookRepository bookRepository;

    private final AuthorRepository authorRepository;

    private final GenreRepository genreRepository;

    private final ObjectMapper objectMapper;

    private final String defaultPath = System.getenv("LOCALAPPDATA")
            + File.separator + "HomeAppBooks" + File.separator + "data" + File.separator + "backup" + File.separator + "book_backup.json";

    @Transactional
    public void createBackup(String filePath) throws IOException {
        String targetPath = resolvePath(filePath);
        log.info("Creating backup in: [path: {}]", targetPath);

        List<Book> books = bookRepository.findAll();
        objectMapper.writeValue(new File(targetPath), books);

        log.info("Successfully created backup in: [path: {}]", targetPath);
    }

    @Transactional
    public void restoreBackup(String filePath) throws IOException {
        String targetPath = resolvePath(filePath);
        log.info("Restoring backup from: [path: {}]", targetPath);

        List<Book> booksFromJson = objectMapper.readValue(new File(targetPath),
                objectMapper.getTypeFactory().constructCollectionType(List.class, Book.class));

        Map<String, Author> uniqueAuthorsCache = new HashMap<>();
        Map<String, Genre> uniqueGenresCache = new HashMap<>();

        for (Book book : booksFromJson) {
            book.setAuthors(processAuthors(book.getAuthors(), uniqueAuthorsCache));
            book.setGenres(processGenres(book.getGenres(), uniqueGenresCache));
        }

        bookRepository.saveAll(booksFromJson);
        log.info("Successfully restored backup from: [path: {}]", targetPath);
    }

    private String resolvePath(String filePath) {
        return (filePath != null && !filePath.isEmpty()) ? filePath : defaultPath;
    }

    private List<Author> processAuthors(List<Author> rawAuthors, Map<String, Author> cache) {
        return Optional.ofNullable(rawAuthors).orElse(Collections.emptyList()).stream()
                .map(rawAuthor -> cache.computeIfAbsent(
                        rawAuthor.getName().trim(),
                        name -> authorRepository.findByName(name).orElseGet(() -> {
                            rawAuthor.setId(null);
                            return authorRepository.save(rawAuthor);
                        })
                ))
                .collect(Collectors.toList());
    }

    private List<Genre> processGenres(List<Genre> rawGenres, Map<String, Genre> cache) {
        return Optional.ofNullable(rawGenres).orElse(Collections.emptyList()).stream()
                .map(rawGenre -> cache.computeIfAbsent(
                        rawGenre.getName().trim(),
                        name -> genreRepository.findByName(name).orElseGet(() -> {
                            rawGenre.setId(null);
                            return genreRepository.save(rawGenre);
                        })
                ))
                .collect(Collectors.toList());
    }
}