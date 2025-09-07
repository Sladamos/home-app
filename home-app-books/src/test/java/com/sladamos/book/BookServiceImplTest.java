package com.sladamos.book;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookServiceImplTest {

    @Mock
    private BookRepository bookRepository;

    @Mock
    private Validator validator;

    @InjectMocks
    private BookServiceImpl bookService;

    @Test
    void shouldReturnAllBooks() {
        List<Book> books = List.of(new Book(), new Book());
        when(bookRepository.findAll()).thenReturn(books);

        List<Book> result = bookService.getAllBooks();

        assertThat(result).isEqualTo(books);
    }

    @Test
    void shouldReturnBookById() throws BookNotFoundException {
        UUID id = UUID.randomUUID();
        Book book = new Book();
        when(bookRepository.findById(id)).thenReturn(Optional.of(book));

        Book result = bookService.getBookById(id);
        assertThat(result).isEqualTo(book);
    }

    @Test
    void shouldThrowBookNotFoundExceptionWhenBookNotFound() {
        UUID id = UUID.randomUUID();
        when(bookRepository.findById(id)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> bookService.getBookById(id))
                .isInstanceOf(BookNotFoundException.class)
                .hasMessage("Book not found with id: " + id);
    }

    @Test
    void shouldCreateBook() throws BookValidationException {
        Book book = new Book();

        bookService.createBook(book);

        verify(bookRepository).save(book);
    }

    @Test
    void shouldThrowBookValidationExceptionWhenCreatingNotValidBook() {
        Book book = Book.builder().isbn("123").build();
        Set<ConstraintViolation<Book>> violations = Set.of(mock(ConstraintViolation.class));
        when(validator.validate(book)).thenReturn(violations);

        assertThatThrownBy(() -> bookService.updateBook(book)).isInstanceOf(BookValidationException.class);
    }

    @Test
    void shouldUpdateBook() throws BookValidationException {
        Book book = new Book();

        bookService.updateBook(book);

        verify(bookRepository).save(book);
    }

    @Test
    void shouldThrowBookValidationExceptionWhenUpdatingNotValidBook() {
        Book book = Book.builder().isbn("123").build();
        Set<ConstraintViolation<Book>> violations = Set.of(mock(ConstraintViolation.class));
        when(validator.validate(book)).thenReturn(violations);

        assertThatThrownBy(() -> bookService.createBook(book)).isInstanceOf(BookValidationException.class);
    }

    @Test
    void shouldDeleteExistingBook() throws BookNotFoundException {
        UUID id = UUID.randomUUID();
        Book book = Book.builder().id(id).build();
        doNothing().when(bookRepository).delete(book);
        when(bookRepository.findById(id)).thenReturn(Optional.of(book));

        bookService.deleteBook(id);

        verify(bookRepository).delete(book);
    }

    @Test
    void shouldThrowBookNotFoundExceptionWhenDeletingNotExistingBook() {
        UUID id = UUID.randomUUID();
        when(bookRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> bookService.deleteBook(id))
                .isInstanceOf(BookNotFoundException.class)
                .hasMessage("Book not found with id: " + id);
    }
}