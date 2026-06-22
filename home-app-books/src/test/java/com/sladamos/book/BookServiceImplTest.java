package com.sladamos.book;

import com.sladamos.book.model.BookEntity;
import com.sladamos.book.repository.AuthorRepository;
import com.sladamos.book.repository.BookRepository;
import com.sladamos.book.repository.GenreRepository;
import com.sladamos.common.converter.StringDuplicator;
import com.sladamos.common.exception.DuplicationException;
import com.sladamos.common.exception.NotFoundException;
import com.sladamos.common.exception.ValidationException;
import com.sladamos.common.image.ImageCoverResizer;
import com.sladamos.common.image.ImageParameters;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookServiceImplTest {

    @Mock
    private BookRepository bookRepository;

    @Mock
    private Validator validator;

    @Mock
    private AuthorRepository authorRepository;

    @Mock
    private GenreRepository genreRepository;

    @Mock
    private ImageCoverResizer imageCoverResizer;

    @Mock
    private StringDuplicator stringDuplicator;

    @InjectMocks
    private BookServiceImpl bookService;

    @BeforeEach
    void setUp() throws Exception {
        lenient().when(validator.validate(any(BookEntity.class))).thenReturn(Set.of());
        lenient().when(authorRepository.findByName(anyString())).thenReturn(Optional.empty());
        lenient().when(genreRepository.findByName(anyString())).thenReturn(Optional.empty());
        lenient().when(imageCoverResizer.resizeImage(any())).thenAnswer(invocation -> invocation.<ImageParameters>getArgument(0).originalBytes());
        lenient().when(bookRepository.save(any(BookEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));
    }

    @Nested
    class GetAllBooks {

        @Test
        void shouldReturnAllBooks() {
            List<BookEntity> books = List.of(new BookEntity(), new BookEntity());
            when(bookRepository.findAll()).thenReturn(books);

            List<BookEntity> result = bookService.getAllBooks();

            assertThat(result).isEqualTo(books);
        }
    }

    @Nested
    class GetBookById {

        @Test
        void shouldReturnBookById() throws NotFoundException {
            UUID id = UUID.randomUUID();
            BookEntity book = new BookEntity();
            when(bookRepository.findById(id)).thenReturn(Optional.of(book));

            BookEntity result = bookService.getBookById(id);

            assertThat(result).isEqualTo(book);
        }

        @Test
        void shouldThrowBookNotFoundExceptionWhenBookNotFound() {
            UUID id = UUID.randomUUID();
            when(bookRepository.findById(id)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> bookService.getBookById(id))
                    .isInstanceOf(NotFoundException.class)
                    .hasMessage("Book not found with id: " + id);
        }
    }

    @Nested
    class CreateBook {

        @Test
        void shouldCreateBook() throws ValidationException {
            BookEntity book = new BookEntity();

            bookService.createBook(book);

            verify(bookRepository).save(book);
        }

        @Test
        void shouldThrowValidationExceptionWhenCreatingNotValidBook() {
            BookEntity book = BookEntity.builder().isbn("123").build();
            Set<ConstraintViolation<BookEntity>> violations = Set.of(mock(ConstraintViolation.class));
            when(validator.validate(book)).thenReturn(violations);

            assertThatThrownBy(() -> bookService.createBook(book))
                    .isInstanceOf(ValidationException.class);
        }
    }

    @Nested
    class UpdateBook {

        @Test
        void shouldUpdateBook() throws ValidationException {
            BookEntity book = new BookEntity();

            bookService.updateBook(book);

            verify(bookRepository).save(book);
        }

        @Test
        void shouldThrowValidationExceptionWhenUpdatingNotValidBook() {
            BookEntity book = BookEntity.builder().isbn("123").build();
            Set<ConstraintViolation<BookEntity>> violations = Set.of(mock(ConstraintViolation.class));
            when(validator.validate(book)).thenReturn(violations);

            assertThatThrownBy(() -> bookService.updateBook(book))
                    .isInstanceOf(ValidationException.class);
        }
    }

    @Nested
    class DeleteBook {

        @Test
        void shouldDeleteExistingBook() throws NotFoundException {
            UUID id = UUID.randomUUID();
            BookEntity book = BookEntity.builder().id(id).build();
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
                    .isInstanceOf(NotFoundException.class)
                    .hasMessage("Book not found with id: " + id);
        }
    }

    @Nested
    class DuplicateBook {

        @Test
        void shouldThrowBookDuplicationExceptionWhenBookIsNull() {
            assertThatThrownBy(() -> bookService.duplicateBook(null, List.of("Other")))
                    .isInstanceOf(DuplicationException.class)
                    .hasMessage("Source book cannot be null");
        }

        @Test
        void shouldDuplicateBookProperly() throws NotFoundException, ValidationException, DuplicationException {
            UUID id = UUID.randomUUID();
            String title = "Eragon";
            String title2 = "Eragon (1)";
            String expectedTitle = "Eragon (2)";
            BookEntity source = BookEntity.builder().id(id).title(title).build();
            when(bookRepository.findById(id)).thenReturn(Optional.of(source));
            when(bookRepository.findAll()).thenReturn(List.of(
                    source,
                    BookEntity.builder().id(UUID.randomUUID()).title(title2).build()
            ));
            when(stringDuplicator.duplicateStringWithCounter(title, List.of(title, title2))).thenReturn(expectedTitle);

            BookEntity result = bookService.duplicateBook(id);

            assertThat(result.getTitle()).isEqualTo(expectedTitle);
            assertThat(result.getId()).isNotEqualTo(id);
            verify(bookRepository).save(result);
        }
    }
}
