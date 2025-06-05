// plik: src/test/java/com/sladamos/book/BookServiceImplTest.java
package com.sladamos.book;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookServiceImplTest {

    @Mock
    private BookRepository bookRepository;

    @InjectMocks
    private BookServiceImpl bookService;

    @Test
    void shouldReturnAllBooks() {
        List<Book> books = List.of(new Book(), new Book());
        when(bookRepository.findAll()).thenReturn(books);

        List<Book> result = bookService.getAllBooks();

        assertThat(result).isEqualTo(books);
        verify(bookRepository).findAll();
    }

    @Test
    void shouldReturnBookById() {
        UUID id = UUID.randomUUID();
        Book book = new Book();
        when(bookRepository.findById(id)).thenReturn(Optional.of(book));

        try {
            Book result = bookService.getBookById(id);

            assertThat(result).isEqualTo(book);
            verify(bookRepository).findById(id);
        } catch (BookNotFoundException e) {
            fail("Book not found");
        }
    }

    @Test
    void shouldThrowBookNotFoundExceptionWhenBookNotFound() {
        UUID id = UUID.randomUUID();
        when(bookRepository.findById(id)).thenReturn(Optional.empty());
        
        try {
            bookService.getBookById(id);
            fail("Book found");
        } catch (BookNotFoundException e) {
            assertThat(e).hasMessage("Book not found with id: " + id);
            verify(bookRepository).findById(id);
        }
    }

    @Test
    void shouldCreateBook() {
        Book book = new Book();

        bookService.createBook(book);

        verify(bookRepository).save(book);
    }

    @Test
    void shouldUpdateBook() {
        Book book = new Book();

        bookService.updateBook(book);

        verify(bookRepository).save(book);
    }

    @Test
    void shouldDeleteBook() {
        UUID id = UUID.randomUUID();
        doNothing().when(bookRepository).deleteById(id);

        bookService.deleteBook(id);

        verify(bookRepository).deleteById(id);
    }
}