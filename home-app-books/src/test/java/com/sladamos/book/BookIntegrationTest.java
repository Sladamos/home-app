package com.sladamos.book;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sladamos.book.dto.PatchBookRequest;
import com.sladamos.book.dto.PutBookRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class BookIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private UUID existingId;

    @BeforeEach
    void setUp() {
        bookRepository.deleteAll();
        existingId = UUID.randomUUID();
        Book existingBook = Book.builder()
                .id(existingId)
                .title("Test Book")
                .isbn("1234567890")
                .publisher("Test Publisher")
                .description("desc")
                .pages(100)
                .authors(List.of("Author1"))
                .genres(List.of("Genre1"))
                .borrowedBy("Jan Kowalski")
                .status(BookStatus.ON_SHELF)
                .rating(5)
                .favorite(true)
                .readDate(LocalDate.of(2023, 1, 1))
                .coverImage("testImage".getBytes())
                .creationDate(LocalDateTime.parse("2023-01-01T10:00:00"))
                .modificationDate(LocalDateTime.parse("2023-01-02T10:00:00"))
                .build();
        bookRepository.save(existingBook);
    }

    @Test
    void shouldReturnBooksWhenRequestingAllBooks() throws Exception {
        mockMvc.perform(get("/api/books"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.books", hasSize(1)))
                .andExpect(jsonPath("$.books[0].id").value(existingId.toString()));
    }


    @Test
    void shouldDeleteExistingBookProperly() throws Exception {
        mockMvc.perform(delete("/api/books/" + existingId))
                .andExpect(status().isOk());
        assertThat(bookRepository.findById(existingId)).isEmpty();
    }

    @Test
    void shouldReturnNotFoundWhenDeletingNotExistingBook() throws Exception {
        mockMvc.perform(delete("/api/books/" + UUID.randomUUID()))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldUpdateExistingBook() throws Exception {
        String updatedTitle = "Updated title";
        PatchBookRequest patch = PatchBookRequest.builder().title(updatedTitle).build();
        mockMvc.perform(patch("/api/books/" + existingId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(patch)))
                .andExpect(status().isOk());
        Book updated = bookRepository.findById(existingId).orElseThrow();
        assertThat(updated.getTitle()).isEqualTo(updatedTitle);
    }

    @Test
    void shouldReturnNotFoundWhenUpdatingNotExistingBook() throws Exception {
        PatchBookRequest patch = PatchBookRequest.builder().title("New title").build();
        mockMvc.perform(patch("/api/books/" + UUID.randomUUID())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(patch)))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void shouldProperlyCreateNewBook() throws Exception {
        UUID id = UUID.randomUUID();
        PutBookRequest request = PutBookRequest.builder()
                .title("New Book")
                .isbn("0987654321")
                .publisher("New Publisher")
                .description("New description")
                .pages(200)
                .authors(List.of("New Author"))
                .genres(List.of("New Genre"))
                .borrowedBy("Adam Nowak")
                .status("BORROWED")
                .rating(4)
                .favorite(true)
                .readDate(LocalDate.of(2024, 3, 3))
                .coverImage("newImage".getBytes())
                .build();
        mockMvc.perform(put("/api/books/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
        Book created = bookRepository.findById(id).orElseThrow();
        assertAll(
                () -> assertThat(created.getId()).isEqualTo(id),
                () -> assertThat(created.getTitle()).isEqualTo("New Book"),
                () -> assertThat(created.getIsbn()).isEqualTo("0987654321"),
                () -> assertThat(created.getPublisher()).isEqualTo("New Publisher"),
                () -> assertThat(created.getDescription()).isEqualTo("New description"),
                () -> assertThat(created.getPages()).isEqualTo(200),
                () -> assertThat(created.getAuthors()).containsExactly("New Author"),
                () -> assertThat(created.getGenres()).containsExactly("New Genre"),
                () -> assertThat(created.getBorrowedBy()).isEqualTo("Adam Nowak"),
                () -> assertThat(created.getStatus()).isEqualTo(BookStatus.BORROWED),
                () -> assertThat(created.getRating()).isEqualTo(4),
                () -> assertThat(created.isFavorite()).isTrue(),
                () -> assertThat(created.getReadDate()).isEqualTo(LocalDate.of(2024, 3, 3)),
                () -> assertThat(created.getCoverImage()).isEqualTo("newImage".getBytes())
        );
    }

    @Test
    @Transactional
    void shouldProperlyReplaceExistingBook() throws Exception {
        PutBookRequest request = PutBookRequest.builder()
                .title("New Book")
                .isbn("0987654321")
                .publisher("New Publisher")
                .description("New description")
                .pages(200)
                .authors(List.of("New Author"))
                .genres(List.of("New Genre"))
                .borrowedBy("Adam Nowak")
                .status("BORROWED")
                .rating(4)
                .favorite(false)
                .readDate(LocalDate.of(2024, 4, 4))
                .coverImage("replaceImage".getBytes())
                .build();
        mockMvc.perform(put("/api/books/" + existingId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
        Book updated = bookRepository.findById(existingId).orElseThrow();
        assertAll(
                () -> assertThat(updated.getId()).isEqualTo(existingId),
                () -> assertThat(updated.getTitle()).isEqualTo("New Book"),
                () -> assertThat(updated.getIsbn()).isEqualTo("0987654321"),
                () -> assertThat(updated.getPublisher()).isEqualTo("New Publisher"),
                () -> assertThat(updated.getDescription()).isEqualTo("New description"),
                () -> assertThat(updated.getPages()).isEqualTo(200),
                () -> assertThat(updated.getAuthors()).containsExactly("New Author"),
                () -> assertThat(updated.getGenres()).containsExactly("New Genre"),
                () -> assertThat(updated.getBorrowedBy()).isEqualTo("Adam Nowak"),
                () -> assertThat(updated.getStatus()).isEqualTo(BookStatus.BORROWED),
                () -> assertThat(updated.getRating()).isEqualTo(4),
                () -> assertThat(updated.isFavorite()).isFalse(),
                () -> assertThat(updated.getReadDate()).isEqualTo(LocalDate.of(2024, 4, 4)),
                () -> assertThat(updated.getCoverImage()).isEqualTo("replaceImage".getBytes())
        );
    }

    @Test
    void shouldReturnBadRequestWhenReplacingNotValidBook() throws Exception {
        PutBookRequest request = PutBookRequest.builder()
                .title("New Book")
                .isbn("123")
                .publisher("New Publisher")
                .description("New description")
                .pages(200)
                .authors(List.of("New Author"))
                .genres(List.of("New Genre"))
                .build();
        mockMvc.perform(put("/api/books/" + existingId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturnBadRequestWhenCreatingNotValidBook() throws Exception {
        UUID newId = UUID.randomUUID();
        PutBookRequest request = PutBookRequest.builder()
                .title("")
                .isbn("1234567890")
                .publisher("New Publisher")
                .description("New description")
                .pages(200)
                .authors(List.of("New Author"))
                .genres(List.of("New Genre"))
                .build();
        mockMvc.perform(put("/api/books/" + newId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }
}
