package com.sladamos.book;

import com.sladamos.book.model.AuthorEntity;
import com.sladamos.book.model.BookEntity;
import com.sladamos.book.model.BookStatus;
import com.sladamos.book.model.GenreEntity;
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
        BookEntity book = BookEntity.builder()
                .id(UUID.randomUUID())
                .title("Test Book")
                .isbn("1234567890")
                .description("Test description")
                .pages(100)
                .coverImage(new byte[]{1, 2, 3})
                .authors(Set.of(new AuthorEntity("Author")))
                .genres(Set.of(new GenreEntity("Genre")))
                .status(BookStatus.ON_SHELF)
                .readDate(LocalDate.of(2000, 1, 1))
                .creationDate(currentDate)
                .modificationDate(currentDate)
                .build();

        bookRepository.save(book);

        Optional<BookEntity> foundBook = bookRepository.findById(book.getId());
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
                () -> assertThat(foundBook.get().getAuthors()).extracting(AuthorEntity::getName).containsExactly("Author"),
                () -> assertThat(foundBook.get().getGenres()).extracting(GenreEntity::getName).containsExactly("Genre")
        );
    }

    @Test
    void shouldUpdateExistingBook() {
        BookEntity book = BookEntity.builder()
                .id(UUID.randomUUID())
                .title("Original Title")
                .isbn("1234567890")
                .publisher("Publisher")
                .description("Desc")
                .pages(100)
                .coverImage(new byte[]{})
                .authors(Set.of(new AuthorEntity("Author")))
                .genres(Set.of(new GenreEntity("Genre")))
                .build();

        bookRepository.save(book);

        book.setTitle("Updated Title");
        book.setPages(200);
        bookRepository.save(book);

        Optional<BookEntity> updatedBook = bookRepository.findById(book.getId());
        assertAll("Updates book",
                () -> assertThat(updatedBook).isPresent(),
                () -> assertThat(updatedBook.get().getTitle()).isEqualTo("Updated Title"),
                () -> assertThat(updatedBook.get().getPages()).isEqualTo(200)
        );
    }

    @Test
    void shouldDeleteExistingBook() {
        BookEntity book = BookEntity.builder()
                .id(UUID.randomUUID())
                .title("To Delete")
                .isbn("1234567890")
                .publisher("Publisher")
                .description("Desc")
                .pages(100)
                .coverImage(new byte[]{})
                .authors(Set.of(new AuthorEntity("Author")))
                .genres(Set.of(new GenreEntity("Genre")))
                .build();

        bookRepository.save(book);
        UUID id = book.getId();

        bookRepository.deleteById(id);

        Optional<BookEntity> deleted = bookRepository.findById(id);
        assertThat(deleted).isNotPresent();
    }

    @Test
    void shouldSaveAndLoadBookWithAuthorsAndGenres() {
        UUID id = UUID.randomUUID();
        BookEntity book = BookEntity.builder()
                .id(id)
                .title("Title")
                .authors(Set.of(new AuthorEntity("A1")))
                .genres(Set.of(new GenreEntity("G1")))
                .build();

        bookRepository.save(book);

        BookEntity loaded = bookRepository.findById(id).orElseThrow();
        assertThat(loaded.getAuthors()).extracting(AuthorEntity::getName).containsExactly("A1");
        assertThat(loaded.getGenres()).extracting(GenreEntity::getName).containsExactly("G1");
    }

    @Test
    void shouldFindBookByAuthorName() {
        BookEntity book = BookEntity.builder()
                .id(UUID.randomUUID())
                .status(BookStatus.ON_SHELF)
                .title("Title 2")
                .authors(Set.of(new AuthorEntity("A2")))
                .build();
        bookRepository.save(book);

        List<BookEntity> found = bookRepository.findByAuthorsName("A2");

        assertThat(found).isNotEmpty();
        assertThat(found.get(0).getAuthors()).extracting(AuthorEntity::getName).containsExactly("A2");
    }

    @Test
    void shouldFindBookByGenreName() {
        BookEntity book = BookEntity.builder()
                .id(UUID.randomUUID())
                .status(BookStatus.ON_SHELF)
                .title("Title 3")
                .authors(Set.of(new AuthorEntity("A3")))
                .genres(Set.of(new GenreEntity("G3")))
                .build();

        bookRepository.save(book);

        List<BookEntity> found = bookRepository.findByGenresName("G3");

        assertThat(found).isNotEmpty();
        assertThat(found.get(0).getGenres()).extracting(GenreEntity::getName).containsExactly("G3");
    }
}