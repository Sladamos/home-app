package com.sladamos.book;

import com.sladamos.book.model.AuthorEntity;
import com.sladamos.book.model.BookEntity;
import com.sladamos.book.model.GenreEntity;
import com.sladamos.book.repository.AuthorRepository;
import com.sladamos.book.repository.BookRepository;
import com.sladamos.book.repository.GenreRepository;
import com.sladamos.common.exception.DuplicationException;
import com.sladamos.common.exception.NotFoundException;
import com.sladamos.common.exception.ValidationException;
import com.sladamos.common.image.ImageCoverResizer;
import com.sladamos.common.image.ImageParameters;
import com.sladamos.common.string.StringDuplicator;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

import static com.sladamos.book.model.BookEntity.MAX_COVER_HEIGHT;
import static com.sladamos.book.model.BookEntity.MAX_COVER_WIDTH;

@Slf4j
@Service
@RequiredArgsConstructor
public class BookServiceImpl implements BookService {

    private final BookRepository bookRepository;

    private final AuthorRepository authorRepository;

    private final GenreRepository genreRepository;

    private final Validator validator;

    private final ImageCoverResizer imageCoverResizer;

    private final StringDuplicator stringDuplicator;

    @Override
    public List<BookEntity> getAllBooks() {
        log.info("Fetching all books from the repository");
        return bookRepository.findAll();
    }

    @Override
    public BookEntity getBookById(UUID id) throws NotFoundException {
        log.info("Fetching book: [id: {}]", id);
        return bookRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Book not found with id: " + id));
    }

    @Override
    @Transactional
    public BookEntity createBook(BookEntity book) throws ValidationException {
        log.info("Creating book: [title: {}]", book.getTitle());
        return processAndSaveBook(book);
    }

    @Override
    @Transactional
    public BookEntity updateBook(BookEntity book) throws ValidationException {
        log.info("Updating book: [id: {}, title: {}]", book.getId(), book.getTitle());
        return processAndSaveBook(book);
    }

    @Override
    @Transactional
    public BookEntity duplicateBook(UUID id) throws NotFoundException, ValidationException, DuplicationException {
        BookEntity sourceBook = getBookById(id);
        log.info("Duplicating book: [id: {}, title: {}]", sourceBook.getId(), sourceBook.getTitle());

        List<String> existingTitles = bookRepository.findAll().stream()
                .map(BookEntity::getTitle)
                .distinct()
                .toList();

        return duplicateBook(sourceBook, existingTitles);
    }

    @Override
    @Transactional
    public BookEntity duplicateBook(BookEntity sourceBook, List<String> existingTitles) throws ValidationException, DuplicationException {
        if (sourceBook == null) {
            throw new DuplicationException("Source book cannot be null");
        }

        String duplicatedTitle = stringDuplicator.duplicateStringWithCounter(sourceBook.getTitle(), existingTitles);
        BookEntity duplicatedBook = sourceBook.toBuilder()
                .title(duplicatedTitle)
                .id(UUID.randomUUID())
                .build();

        return processAndSaveBook(duplicatedBook);
    }

    @Override
    @Transactional
    public void deleteBook(UUID id) throws NotFoundException {
        log.info("Deleting book with id: {}", id);
        BookEntity book = getBookById(id);
        bookRepository.delete(book);
        log.info("Book successfully deleted: [id: {}]", id);
    }

    private BookEntity processAndSaveBook(BookEntity book) throws ValidationException {
        validateBook(book);
        resizeBookCover(book);
        attachExistingAuthorsAndGenres(book);
        return bookRepository.save(book);
    }

    private void validateBook(BookEntity book) throws ValidationException {
        Set<ConstraintViolation<BookEntity>> violations = validator.validate(book);
        if (!violations.isEmpty()) {
            log.error("Validation errors occurred for book: [id: {}, title: {}]", book.getId(), book.getTitle());
            throw new ValidationException(violations);
        }
    }

    private void resizeBookCover(BookEntity book) {
        if (book.getCoverImage() != null && book.getCoverImage().length > 0) {
            log.info("Resizing cover image for book: [id: {}, title: {}]", book.getId(), book.getTitle());
            try {
                ImageParameters imageParameters = new ImageParameters(book.getCoverImage(), MAX_COVER_WIDTH, MAX_COVER_HEIGHT);
                byte[] resizedImage = imageCoverResizer.resizeImage(imageParameters);
                book.setCoverImage(resizedImage);
            } catch (Exception e) {
                log.error("Error occurred while resizing cover image for book: [id: {}, title: {}]", book.getId(), book.getTitle(), e);
            }
        }
    }

    private void attachExistingAuthorsAndGenres(BookEntity book) {
        Set<AuthorEntity> authors = Optional.ofNullable(book.getAuthors()).orElse(new HashSet<>());
        Set<AuthorEntity> managedAuthors = authors.stream()
                .map(author -> authorRepository.findByName(author.getName().trim())
                        .orElse(author))
                .collect(Collectors.toSet());
        book.setAuthors(managedAuthors);

        Set<GenreEntity> genres = Optional.ofNullable(book.getGenres()).orElse(new HashSet<>());
        Set<GenreEntity> managedGenres = genres.stream()
                .map(genre -> genreRepository.findByName(genre.getName().trim())
                        .orElse(genre))
                .collect(Collectors.toSet());
        book.setGenres(managedGenres);
    }

}
