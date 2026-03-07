package com.sladamos.book;

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

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class BookServiceImpl implements BookService {

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
    public void deleteBook(UUID id) throws BookNotFoundException {
        log.info("Deleting book with id: {}", id);
        Book book = getBookById(id);
        bookRepository.delete(book);
        log.info("Book successfully deleted: [id: {}]", id);
    }

    private void processAndSaveBook(Book book) throws BookValidationException {
        validateBook(book);
        resizeBookCover(book);
        attachExistingAuthorsAndGenres(book);
        bookRepository.save(book);
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
        List<Author> authors = Optional.ofNullable(book.getAuthors()).orElse(new ArrayList<>());
        List<Author> managedAuthors = authors.stream()
                .map(author -> authorRepository.findByName(author.getName().trim())
                        .orElse(author))
                .collect(Collectors.toList());
        book.setAuthors(managedAuthors);

        List<Genre> genres = Optional.ofNullable(book.getGenres()).orElse(new ArrayList<>());
        List<Genre> managedGenres = genres.stream()
                .map(genre -> genreRepository.findByName(genre.getName().trim())
                        .orElse(genre))
                .collect(Collectors.toList());
        book.setGenres(managedGenres);
    }
}