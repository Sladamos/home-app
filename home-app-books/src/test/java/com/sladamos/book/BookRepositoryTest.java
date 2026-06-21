package com.sladamos.book;

import com.sladamos.book.model.Author;
import com.sladamos.book.model.Book;
import com.sladamos.book.model.BookStatus;
import com.sladamos.book.model.Genre;
import com.sladamos.book.repository.BookRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@DataJpaTest
class BookRepositoryTest {

    @Autowired
    private BookRepository bookRepository;

    @Test
    void shouldSaveAndFindBookById() {
        LocalDateTime currentDate = LocalDateTime.now();
        Book book = Book.builder()
                .id(UUID.randomUUID())
                .title("Test Book")
                .isbn("1234567890")
                .description("Test description")
                .pages(100)
                .coverImage(new byte[]{1, 2, 3})
                .authors(Set.of(new Author("Author")))
                .genres(Set.of(new Genre("Genre")))
                .status(BookStatus.ON_SHELF)
                .readDate(LocalDate.of(2000, 1, 1))
                .creationDate(currentDate)
                .modificationDate(currentDate)
                .build();

        bookRepository.save(book);

        Optional<Book> foundBook = bookRepository.findById(book.getId());
        assertAll("Saves and finds book by ID",
                () -> assertThat(foundBook).isPresent(),
                () -> assertThat(foundBook.get().getTitle()).isEqualTo("Test Book"),
                () -> assertThat(foundBook.get().getIsbn()).isEqualTo("1234567890"),
                () -> assertThat(foundBook.get().getDescription()).isEqualTo("Test description"),
                () -> assertThat(foundBook.get().getCoverImage()).isEqualTo(new byte[]{1, 2, 3}),
                () -> assertThat(foundBook.get().getStatus()).isEqualTo(BookStatus.ON_SHELF),
                () -> assertThat(foundBook.get().getReadDate()).isEqualTo(LocalDate.of(2000, 1, 1)),
                () -> assertThat(foundBook.get().getCreationDate()).isEqualTo(currentDate),
                () -> assertThat(foundBook.get().getModificationDate()).isEqualTo(currentDate),
                () -> assertThat(foundBook.get().getPages()).isEqualTo(100),
                () -> assertThat(foundBook.get().getAuthors()).extracting(Author::getName).containsExactly("Author"),
                () -> assertThat(foundBook.get().getGenres()).extracting(Genre::getName).containsExactly("Genre")
        );
    }

    @Test
    void shouldUpdateExistingBook() {
        Book book = Book.builder()
                .id(UUID.randomUUID())
                .title("Original Title")
                .isbn("1234567890")
                .publisher("Publisher")
                .description("Desc")
                .pages(100)
                .coverImage(new byte[]{})
                .authors(Set.of(new Author("Author")))
                .genres(Set.of(new Genre("Genre")))
                .build();

        bookRepository.save(book);

        book.setTitle("Updated Title");
        book.setPages(200);
        bookRepository.save(book);

        Optional<Book> updatedBook = bookRepository.findById(book.getId());
        assertAll("Updates book",
                () -> assertThat(updatedBook).isPresent(),
                () -> assertThat(updatedBook.get().getTitle()).isEqualTo("Updated Title"),
                () -> assertThat(updatedBook.get().getPages()).isEqualTo(200)
        );
    }

    @Test
    void shouldDeleteExistingBook() {
        Book book = Book.builder()
                .id(UUID.randomUUID())
                .title("To Delete")
                .isbn("1234567890")
                .publisher("Publisher")
                .description("Desc")
                .pages(100)
                .coverImage(new byte[]{})
                .authors(Set.of(new Author("Author")))
                .genres(Set.of(new Genre("Genre")))
                .build();

        bookRepository.save(book);
        UUID id = book.getId();

        bookRepository.deleteById(id);

        Optional<Book> deleted = bookRepository.findById(id);
        assertThat(deleted).isNotPresent();
    }

    @Test
    void shouldSaveAndLoadBookWithAuthorsAndGenres() {
        UUID id = UUID.randomUUID();
        Book book = Book.builder()
                .id(id)
                .title("Title")
                .authors(Set.of(new Author("A1")))
                .genres(Set.of(new Genre("G1")))
                .build();

        bookRepository.save(book);

        Book loaded = bookRepository.findById(id).orElseThrow();
        assertThat(loaded.getAuthors()).extracting(Author::getName).containsExactly("A1");
        assertThat(loaded.getGenres()).extracting(Genre::getName).containsExactly("G1");
    }

    @Test
    void shouldFindBookByAuthorName() {
        Book book = Book.builder()
                .id(UUID.randomUUID())
                .title("Title 2")
                .authors(Set.of(new Author("A2")))
                .build();
        bookRepository.save(book);

        List<Book> found = bookRepository.findByAuthorsName("A2");

        assertThat(found).isNotEmpty();
        assertThat(found.get(0).getAuthors()).extracting(Author::getName).containsExactly("A2");
    }

    @Test
    void shouldFindBookByGenreName() {
        Book book = Book.builder()
                .id(UUID.randomUUID())
                .title("Title 3")
                .authors(Set.of(new Author("A3")))
                .genres(Set.of(new Genre("G3")))
                .build();
        bookRepository.save(book);

        List<Book> found = bookRepository.findByGenresName("G3");

        assertThat(found).isNotEmpty();
        assertThat(found.get(0).getGenres()).extracting(Genre::getName).containsExactly("G3");
    }
}