package com.sladamos.book;

import com.sladamos.book.exception.BookDuplicationException;
import com.sladamos.book.exception.BookNotFoundException;
import com.sladamos.book.exception.BookValidationException;
import com.sladamos.book.model.Book;
import com.sladamos.book.repository.AuthorRepository;
import com.sladamos.book.repository.BookRepository;
import com.sladamos.book.repository.GenreRepository;
import com.sladamos.book.image.ImageCoverResizer;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Stream;

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

    @InjectMocks
    private BookServiceImpl bookService;

    @BeforeEach
    void setUp() throws Exception {
        lenient().when(validator.validate(any(Book.class))).thenReturn(Set.of());
        lenient().when(authorRepository.findByName(anyString())).thenReturn(Optional.empty());
        lenient().when(genreRepository.findByName(anyString())).thenReturn(Optional.empty());
        lenient().when(imageCoverResizer.resizeImage(any())).thenAnswer(invocation -> invocation.getArgument(0));
        lenient().when(bookRepository.save(any(Book.class))).thenAnswer(invocation -> invocation.getArgument(0));
    }

    @Nested
    class GetAllBooks {

        @Test
        void shouldReturnAllBooks() {
            List<Book> books = List.of(new Book(), new Book());
            when(bookRepository.findAll()).thenReturn(books);

            List<Book> result = bookService.getAllBooks();

            assertThat(result).isEqualTo(books);
        }
    }

    @Nested
    class GetBookById {

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
    }

    @Nested
    class CreateBook {

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

            assertThatThrownBy(() -> bookService.createBook(book))
                    .isInstanceOf(BookValidationException.class);
        }
    }

    @Nested
    class UpdateBook {

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

            assertThatThrownBy(() -> bookService.updateBook(book))
                    .isInstanceOf(BookValidationException.class);
        }
    }

    @Nested
    class DeleteBook {

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

    @Nested
    class DuplicateBook {

        @ParameterizedTest(name = "\"{0}\" -> \"{2}\"")
        @MethodSource("duplicateBookScenarios")
        void shouldDuplicateBookWithCorrectTitle(String sourceTitle, List<String> existingTitles, String expectedTitle)
                throws BookValidationException, BookDuplicationException {
            Book source = Book.builder().id(UUID.randomUUID()).title(sourceTitle).build();

            Book result = bookService.duplicateBook(source, existingTitles);

            assertThat(result.getId()).isNotEqualTo(source.getId());
            assertThat(result.getTitle()).isEqualTo(expectedTitle);
            verify(bookRepository).save(any(Book.class));
        }

        @Test
        void shouldThrowBookDuplicationExceptionWhenSourceTitleIsMissingFromExistingTitles() {
            Book source = Book.builder().id(UUID.randomUUID()).title("Eragon").build();

            assertThatThrownBy(() -> bookService.duplicateBook(source, List.of("Other")))
                    .isInstanceOf(BookDuplicationException.class)
                    .hasMessageContaining("Eragon");
        }

        @Test
        void shouldDelegateUuidOverloadToMainDuplicationFlow() throws BookNotFoundException, BookValidationException, BookDuplicationException {
            UUID id = UUID.randomUUID();
            Book source = Book.builder().id(id).title("Eragon").build();
            when(bookRepository.findById(id)).thenReturn(Optional.of(source));
            when(bookRepository.findAll()).thenReturn(List.of(
                    source,
                    Book.builder().id(UUID.randomUUID()).title("Eragon (1)").build()
            ));

            Book result = bookService.duplicateBook(id);

            assertThat(result.getTitle()).isEqualTo("Eragon (2)");
            verify(bookRepository).findById(id);
            verify(bookRepository).findAll();
            verify(bookRepository).save(any(Book.class));
        }

        private static Stream<Arguments> duplicateBookScenarios() {
            return Stream.of(
                    Arguments.of("Eragon", List.of("Eragon"), "Eragon (1)"),
                    Arguments.of("Eragon", List.of("Eragon", "Eragon (1)"), "Eragon (2)"),
                    Arguments.of("Eragon (1)", List.of("Eragon", "Eragon (1)", "Eragon (2)"), "Eragon (3)"),
                    Arguments.of("Eragon (1)", List.of("Eragon (1)", "Eragon (2)"), "Eragon (3)"),
                    Arguments.of("Eragon (1)", List.of("Eragon (1)", "Eragon (3)"), "Eragon (4)"),
                    Arguments.of("Eragon(3)test", List.of("Eragon(3)test"), "Eragon(3)test (1)"),
                    Arguments.of("Eragontest (3)", List.of("Eragontest (3)"), "Eragontest (4)")
            );
        }
    }
}
