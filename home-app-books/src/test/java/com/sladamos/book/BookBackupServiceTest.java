package com.sladamos.book;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.sladamos.book.model.Author;
import com.sladamos.book.model.Book;
import com.sladamos.book.model.Genre;
import com.sladamos.book.repository.AuthorRepository;
import com.sladamos.book.repository.BookRepository;
import com.sladamos.book.repository.GenreRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.File;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookBackupServiceTest {

    @Mock
    private BookRepository bookRepository;

    @Mock
    private AuthorRepository authorRepository;

    @Mock
    private GenreRepository genreRepository;

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private TypeFactory typeFactory;

    @InjectMocks
    private BookBackupService backupService;

    @Test
    void shouldProperlyCreateBackupWithProvidedPath() throws Exception {
        List<Book> books = List.of(new Book());
        when(bookRepository.findAll()).thenReturn(books);

        backupService.createBackup("custom.json");

        verify(bookRepository).findAll();
        verify(objectMapper).writeValue(any(File.class), eq(books));
    }

    @Test
    void shouldProperlyCreateBackupWithDefaultPathWhenPathIsNull() throws Exception {
        List<Book> books = List.of(new Book());
        when(bookRepository.findAll()).thenReturn(books);

        backupService.createBackup(null);

        verify(bookRepository).findAll();
        verify(objectMapper).writeValue(any(File.class), eq(books));
    }

    @Test
    void shouldRestoreBackupAndHandleNullCollectionsGracefully() throws Exception {
        Book bookWithNulls = Book.builder().authors(null).genres(null).build();
        List<Book> mockedBooksFromJson = List.of(bookWithNulls);

        CollectionType collectionType = mock(CollectionType.class);
        when(objectMapper.getTypeFactory()).thenReturn(typeFactory);
        when(typeFactory.constructCollectionType(List.class, Book.class)).thenReturn(collectionType);
        when(objectMapper.readValue(any(File.class), eq(collectionType))).thenReturn(mockedBooksFromJson);

        backupService.restoreBackup("test.json");

        verify(bookRepository).saveAll(mockedBooksFromJson);
        verifyNoInteractions(authorRepository);
        verifyNoInteractions(genreRepository);
    }

    @Test
    void shouldRestoreBackupAndDeduplicateAuthorsAndGenres() throws Exception {
        Author author1 = new Author("Stephen King");
        Author author2 = new Author("Stephen King");
        Genre genre1 = new Genre("Horror");
        Genre genre2 = new Genre("Horror");

        Book book1 = Book.builder().authors(Set.of(author1)).genres(Set.of(genre1)).build();
        Book book2 = Book.builder().authors(Set.of(author2)).genres(Set.of(genre2)).build();
        List<Book> mockedBooksFromJson = List.of(book1, book2);

        CollectionType collectionType = mock(CollectionType.class);
        when(objectMapper.getTypeFactory()).thenReturn(typeFactory);
        when(typeFactory.constructCollectionType(List.class, Book.class)).thenReturn(collectionType);
        when(objectMapper.readValue(any(File.class), eq(collectionType))).thenReturn(mockedBooksFromJson);

        when(authorRepository.findByName("Stephen King")).thenReturn(Optional.empty());
        when(authorRepository.save(author1)).thenReturn(author1);

        when(genreRepository.findByName("Horror")).thenReturn(Optional.empty());
        when(genreRepository.save(genre1)).thenReturn(genre1);

        backupService.restoreBackup("test.json");

        verify(bookRepository).saveAll(mockedBooksFromJson);

        verify(authorRepository, times(1)).findByName("Stephen King");
        verify(authorRepository, times(1)).save(any(Author.class));

        verify(genreRepository, times(1)).findByName("Horror");
        verify(genreRepository, times(1)).save(any(Genre.class));

        assertThat(book1.getAuthors()).containsExactlyElementsOf(book2.getAuthors());
        assertThat(book1.getGenres()).containsExactlyElementsOf(book2.getGenres());
    }
}