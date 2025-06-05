package com.sladamos.book;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@DataJpaTest
class BookRepositoryTest {

    @Autowired
    private BookRepository bookRepository;

    @Test
    void shouldSaveAndFindBookById() {
        Book book = Book.builder()
                .id(UUID.randomUUID())
                .title("Test Book")
                .isbn("1234567890")
                .publisher("Test Publisher")
                .description("Test description")
                .pages(100)
                .coverImage(new byte[]{})
                .authors(List.of("Author"))
                .genres(List.of("Genre"))
                .build();

        bookRepository.save(book);

        Optional<Book> foundBook = bookRepository.findById(book.getId());
        assertAll("Saves and finds book by ID",
                () -> assertThat(foundBook).isPresent(),
                () -> assertThat(foundBook.get().getTitle()).isEqualTo("Test Book"),
                () -> assertThat(foundBook.get().getIsbn()).isEqualTo("1234567890"),
                () -> assertThat(foundBook.get().getPublisher()).isEqualTo("Test Publisher"),
                () -> assertThat(foundBook.get().getDescription()).isEqualTo("Test description"),
                () -> assertThat(foundBook.get().getPages()).isEqualTo(100),
                () -> assertThat(foundBook.get().getAuthors()).containsExactly("Author"),
                () -> assertThat(foundBook.get().getGenres()).containsExactly("Genre")
        );
    }

    @Test
    void shouldUpdateBook() {
        Book book = Book.builder()
                .id(UUID.randomUUID())
                .title("Original Title")
                .isbn("1234567890")
                .publisher("Publisher")
                .description("Desc")
                .pages(100)
                .coverImage(new byte[]{})
                .authors(List.of("Author"))
                .genres(List.of("Genre"))
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
    void shouldDeleteBook() {
        Book book = Book.builder()
                .id(UUID.randomUUID())
                .title("To Delete")
                .isbn("1234567890")
                .publisher("Publisher")
                .description("Desc")
                .pages(100)
                .coverImage(new byte[]{})
                .authors(List.of("Author"))
                .genres(List.of("Genre"))
                .build();

        bookRepository.save(book);
        UUID id = book.getId();

        bookRepository.deleteById(id);

        Optional<Book> deleted = bookRepository.findById(id);
        assertThat(deleted).isNotPresent();
    }
}