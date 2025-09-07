package com.sladamos.book;

import com.sladamos.book.dto.GetBooksResponse;
import com.sladamos.book.dto.PatchBookRequest;
import com.sladamos.book.dto.PutBookRequest;
import com.sladamos.book.functions.BooksToResponseFunction;
import com.sladamos.book.functions.RequestToBookFunction;
import com.sladamos.book.functions.RequestToUpdateBookFunction;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookControllerTest {

    @Mock
    private BookService service;

    @Mock
    private BooksToResponseFunction booksToResponse;

    @Mock
    private RequestToBookFunction requestToBook;

    @Mock
    private RequestToUpdateBookFunction requestToUpdateBook;

    @InjectMocks
    private BookController controller;

    @Test
    void shouldReturnBooksProperly() {
        Book book = Book.builder().id(UUID.randomUUID()).title("A").build();
        List<Book> books = List.of(book);
        GetBooksResponse response = GetBooksResponse.builder()
                .books(List.of(GetBooksResponse.Book.builder().id(book.getId()).title(book.getTitle()).build()))
                .build();
        when(service.getAllBooks()).thenReturn(books);
        when(booksToResponse.apply(books)).thenReturn(response);

        GetBooksResponse result = controller.getBooks();

        assertThat(result).isSameAs(response);
    }

    @Test
    void shouldPutBookProperly() throws BookValidationException {
        UUID id = UUID.randomUUID();
        PutBookRequest req = PutBookRequest.builder().title("T").build();
        Book book = Book.builder().id(id).title("T").build();
        when(requestToBook.apply(id, req)).thenReturn(book);

        controller.putBook(id, req);

        verify(service).createBook(book);
    }

    @Test
    void shouldPatchExistingBook() throws BookNotFoundException, BookValidationException {
        UUID id = UUID.randomUUID();
        PatchBookRequest req = PatchBookRequest.builder().title("T2").build();
        Book book = Book.builder().id(id).title("T1").build();
        Book updated = Book.builder().id(id).title("T2").build();

        when(service.getBookById(id)).thenReturn(book);
        when(requestToUpdateBook.apply(book, req)).thenReturn(updated);

        controller.patchBook(id, req);

        verify(service).updateBook(updated);
    }

    @Test
    void shouldDeleteExistingBook() throws BookNotFoundException {
        UUID id = UUID.randomUUID();

        doNothing().when(service).deleteBook(id);

        controller.deleteBook(id);

        verify(service).deleteBook(id);
    }

    @Test
    void shouldThrowResponseStatusExceptionWhenPatchBookNotFound() throws BookNotFoundException {
        UUID id = UUID.randomUUID();
        PatchBookRequest req = PatchBookRequest.builder().title("T2").build();
        when(service.getBookById(id)).thenThrow(new BookNotFoundException("not found"));

        assertThatThrownBy(() -> controller.patchBook(id, req))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("404");
    }

    @Test
    void shouldThrowResponseStatusExceptionWhenPatchNotValidBook() throws BookNotFoundException, BookValidationException {
        UUID id = UUID.randomUUID();
        PatchBookRequest req = PatchBookRequest.builder().title("T2").build();
        Book book = Book.builder().id(id).title("T1").build();
        Book updated = Book.builder().id(id).title("T2").build();

        when(service.getBookById(id)).thenReturn(book);
        when(requestToUpdateBook.apply(book, req)).thenReturn(updated);
        doThrow(new BookValidationException(Set.of())).when(service).updateBook(updated);

        assertThatThrownBy(() -> controller.patchBook(id, req))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("400");
    }

    @Test
    void shouldThrowResponseStatusExceptionWhenDeletedBookNotFound() throws BookNotFoundException {
        UUID id = UUID.randomUUID();
        doThrow(new BookNotFoundException("not found")).when(service).deleteBook(id);

        assertThatThrownBy(() -> controller.deleteBook(id))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("404");
    }
}
