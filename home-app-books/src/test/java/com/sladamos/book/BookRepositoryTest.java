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
}