package com.sladamos.book;

import com.sladamos.book.exception.BookDuplicationException;
import com.sladamos.book.exception.BookNotFoundException;
import com.sladamos.book.exception.BookValidationException;
import com.sladamos.book.image.ImageCoverResizer;
import com.sladamos.book.model.Author;
import com.sladamos.book.model.Book;
import com.sladamos.book.model.Genre;
import com.sladamos.book.repository.AuthorRepository;
import com.sladamos.book.repository.BookRepository;
import com.sladamos.book.repository.GenreRepository;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class BookServiceImpl implements BookService {

    private static final Pattern COUNTER_SUFFIX = Pattern.compile(" \\((\\d+)\\)$");

    private final BookRepository bookRepository;

    private final AuthorRepository authorRepository;

    private final GenreRepository genreRepository;

    private final Validator validator;

    private final ImageCoverResizer imageCoverResizer;

    @Override
    public List<Book> getAllBooks() {
        log.info("Fetching all books from the repository");
        return bookRepository.findAll();
    }

    @Override
    public Book getBookById(UUID id) throws BookNotFoundException {
        log.info("Fetching book: [id: {}]", id);
        return bookRepository.findById(id)
                .orElseThrow(() -> new BookNotFoundException("Book not found with id: " + id));
    }

    @Override
    @Transactional
    public void createBook(Book book) throws BookValidationException {
        log.info("Creating book: [title: {}]", book.getTitle());
        processAndSaveBook(book);
    }

    @Override
    @Transactional
    public void updateBook(Book book) throws BookValidationException {
        log.info("Updating book: [id: {}, title: {}]", book.getId(), book.getTitle());
        processAndSaveBook(book);
    }

    @Override
    @Transactional
    public Book duplicateBook(UUID id) throws BookNotFoundException, BookValidationException, BookDuplicationException {
        Book sourceBook = getBookById(id);
        log.info("Duplicating book: [id: {}, title: {}]", sourceBook.getId(), sourceBook.getTitle());

        List<String> existingTitles = bookRepository.findAll().stream()
                .map(Book::getTitle)
                .distinct()
                .toList();

        return duplicateBook(sourceBook, existingTitles);
    }

    @Override
    @Transactional
    public Book duplicateBook(Book sourceBook, List<String> existingTitles) throws BookValidationException, BookDuplicationException {
        validateDuplicateInput(sourceBook, existingTitles);

        String duplicatedTitle = buildDuplicateTitle(sourceBook.getTitle(), existingTitles);
        LocalDateTime now = LocalDateTime.now();
        Book duplicatedBook = sourceBook.toBuilder()
                .id(UUID.randomUUID())
                .title(duplicatedTitle)
                .creationDate(now)
                .modificationDate(now)
                .build();

        return processAndSaveBook(duplicatedBook);
    }

    @Override
    @Transactional
    public void deleteBook(UUID id) throws BookNotFoundException {
        log.info("Deleting book with id: {}", id);
        Book book = getBookById(id);
        bookRepository.delete(book);
        log.info("Book successfully deleted: [id: {}]", id);
    }

    private Book processAndSaveBook(Book book) throws BookValidationException {
        validateBook(book);
        resizeBookCover(book);
        attachExistingAuthorsAndGenres(book);
        return bookRepository.save(book);
    }

    private void validateBook(Book book) throws BookValidationException {
        Set<ConstraintViolation<Book>> violations = validator.validate(book);
        if (!violations.isEmpty()) {
            log.error("Validation errors occurred for book: [id: {}, title: {}]", book.getId(), book.getTitle());
            throw new BookValidationException(violations);
        }
    }

    private void resizeBookCover(Book book) {
        if (book.getCoverImage() != null && book.getCoverImage().length > 0) {
            log.info("Resizing cover image for book: [id: {}, title: {}]", book.getId(), book.getTitle());
            try {
                byte[] resizedImage = imageCoverResizer.resizeImage(book.getCoverImage());
                book.setCoverImage(resizedImage);
            } catch (Exception e) {
                log.error("Error occurred while resizing cover image for book: [id: {}, title: {}]", book.getId(), book.getTitle(), e);
            }
        }
    }

    private void attachExistingAuthorsAndGenres(Book book) {
        Set<Author> authors = Optional.ofNullable(book.getAuthors()).orElse(new HashSet<>());
        Set<Author> managedAuthors = authors.stream()
                .map(author -> authorRepository.findByName(author.getName().trim())
                        .orElse(author))
                .collect(Collectors.toSet());
        book.setAuthors(managedAuthors);

        Set<Genre> genres = Optional.ofNullable(book.getGenres()).orElse(new HashSet<>());
        Set<Genre> managedGenres = genres.stream()
                .map(genre -> genreRepository.findByName(genre.getName().trim())
                        .orElse(genre))
                .collect(Collectors.toSet());
        book.setGenres(managedGenres);
    }

    private String buildDuplicateTitle(String sourceTitle, List<String> existingTitles) throws BookDuplicationException {
        String baseTitle = extractBaseTitle(sourceTitle);
        long nextNumber = findNextNumber(baseTitle, existingTitles);
        return String.format("%s (%d)", baseTitle, nextNumber);
    }

    private void validateDuplicateInput(Book sourceBook, List<String> existingTitles) throws BookDuplicationException {
        if (sourceBook == null) {
            throw new BookDuplicationException("Source book cannot be null");
        }
        if (existingTitles == null) {
            throw new BookDuplicationException("Existing titles cannot be null");
        }
        if (!existingTitles.contains(sourceBook.getTitle())) {
            throw new BookDuplicationException("Book with title '" + sourceBook.getTitle() + "' does not exist in the list");
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
