package com.sladamos.book;

import com.sladamos.book.util.ImageCoverResizer;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class BookServiceImpl implements BookService {

    private final BookRepository bookRepository;

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
        return bookRepository.findById(id).orElseThrow(() -> new BookNotFoundException("Book not found with id: " + id));
    }

    @Override
    public void createBook(Book book) throws BookValidationException {
        log.info("Creating book: [id: {}. title: {}]", book.getId(), book.getTitle());
        Set<ConstraintViolation<Book>> violations = validator.validate(book);
        if (!violations.isEmpty()) {
            log.error("Validation errors occurred while creating book: [id: {}, title: {}]", book.getId(), book.getTitle());
            throw new BookValidationException(violations);
        }
        resizeBookCover(book);
        bookRepository.save(book);
    }

    @Override
    public void updateBook(Book book) throws BookValidationException {
        log.info("Updating book: [id: {}, title: {}]", book.getId(), book.getTitle());
        Set<ConstraintViolation<Book>> violations = validator.validate(book);
        if (!violations.isEmpty()) {
            log.error("Validation errors occurred while updating book: [id: {}, title: {}]", book.getId(), book.getTitle());
            throw new BookValidationException(violations);
        }
        resizeBookCover(book);
        bookRepository.save(book);
    }

    @Override
    public void deleteBook(UUID id) throws BookNotFoundException {
        log.info("Deleting book with id: {}", id);
        Book book = bookRepository.findById(id).orElseThrow(() -> new BookNotFoundException("Book not found with id: " + id));
        log.info("Book found, proceeding to delete: [id: {}, title: {}]", book.getId(), book.getTitle());
        bookRepository.delete(book);
    }

    private void resizeBookCover(Book book) {
        if (book.getCoverImage() != null) {
            log.info("Resizing cover image for book: [id: {}, title: {}]", book.getId(), book.getTitle());
            try {
                byte[] resizedImage = imageCoverResizer.resizeImage(book.getCoverImage());
                book.setCoverImage(resizedImage);
            } catch (Exception e) {
                log.error("Error occurred while resizing cover image for book: [id: {}, title: {}]", book.getId(), book.getTitle(), e);
            }
        }
    }
}