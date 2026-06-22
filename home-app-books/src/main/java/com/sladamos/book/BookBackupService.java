package com.sladamos.book;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sladamos.book.model.AuthorEntity;
import com.sladamos.book.model.BookEntity;
import com.sladamos.book.model.GenreEntity;
import com.sladamos.book.repository.AuthorRepository;
import com.sladamos.book.repository.BookRepository;
import com.sladamos.book.repository.GenreRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.IOException;
import java.util.*;
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

        List<BookEntity> books = bookRepository.findAll();
        objectMapper.writeValue(new File(targetPath), books);

        log.info("Successfully created backup in: [path: {}]", targetPath);
    }

    @Transactional
    public void restoreBackup(String filePath) throws IOException {
        String targetPath = resolvePath(filePath);
        log.info("Restoring backup from: [path: {}]", targetPath);

        List<BookEntity> booksFromJson = objectMapper.readValue(new File(targetPath),
                objectMapper.getTypeFactory().constructCollectionType(List.class, BookEntity.class));

        Map<String, AuthorEntity> uniqueAuthorsCache = new HashMap<>();
        Map<String, GenreEntity> uniqueGenresCache = new HashMap<>();

        for (BookEntity book : booksFromJson) {
            book.setAuthors(processAuthors(book.getAuthors(), uniqueAuthorsCache));
            book.setGenres(processGenres(book.getGenres(), uniqueGenresCache));
        }

        bookRepository.saveAll(booksFromJson);
        log.info("Successfully restored backup from: [path: {}]", targetPath);
    }

    private String resolvePath(String filePath) {
        return (filePath != null && !filePath.isEmpty()) ? filePath : defaultPath;
    }

    private Set<AuthorEntity> processAuthors(Set<AuthorEntity> rawAuthors, Map<String, AuthorEntity> cache) {
        return Optional.ofNullable(rawAuthors).orElse(Collections.emptySet()).stream()
                .map(rawAuthor -> cache.computeIfAbsent(
                        rawAuthor.getName().trim(),
                        name -> authorRepository.findByName(name).orElseGet(() -> {
                            rawAuthor.setId(null);
                            return authorRepository.save(rawAuthor);
                        })
                ))
                .collect(Collectors.toSet());
    }

    private Set<GenreEntity> processGenres(Set<GenreEntity> rawGenres, Map<String, GenreEntity> cache) {
        return Optional.ofNullable(rawGenres).orElse(Collections.emptySet()).stream()
                .map(rawGenre -> cache.computeIfAbsent(
                        rawGenre.getName().trim(),
                        name -> genreRepository.findByName(name).orElseGet(() -> {
                            rawGenre.setId(null);
                            return genreRepository.save(rawGenre);
                        })
                ))
                .collect(Collectors.toSet());
    }
}