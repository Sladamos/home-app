package com.sladamos.book;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import com.sladamos.book.dto.PatchBookRequest;
import com.sladamos.book.dto.PutBookRequest;
import com.sladamos.book.model.AuthorEntity;
import com.sladamos.book.model.BookEntity;
import com.sladamos.book.model.BookStatus;
import com.sladamos.book.model.GenreEntity;
import com.sladamos.book.repository.BookRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.jdbc.JdbcTestUtils;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.not;
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

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private final UUID existingId = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        BookEntity existingBook = BookEntity.builder()
                .id(existingId)
                .title("Test Book")
                .isbn("1234567890")
                .publisher("Test Publisher")
                .description("desc")
                .pages(100)
                .authors(Set.of(new AuthorEntity("Author1")))
                .genres(Set.of(new GenreEntity("Genre1")))
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

    @AfterEach
    void tearDown() {
        JdbcTestUtils.deleteFromTables(
                jdbcTemplate,
                "book_author", "book_genre",
                "book", "author", "genre"
        );
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
        BookEntity updated = bookRepository.findById(existingId).orElseThrow();


        assertAll(
                () -> assertThat(updated.getTitle()).isEqualTo(updatedTitle),
                () -> assertThat(updated.getCreationDate()).isNotNull(),
                () -> assertThat(updated.getModificationDate()).isCloseTo(LocalDateTime.now(), within(1, ChronoUnit.MINUTES))
        );
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
        BookEntity created = bookRepository.findById(id).orElseThrow();
        assertAll(
                () -> assertThat(created.getId()).isEqualTo(id),
                () -> assertThat(created.getTitle()).isEqualTo("New Book"),
                () -> assertThat(created.getIsbn()).isEqualTo("0987654321"),
                () -> assertThat(created.getPublisher()).isEqualTo("New Publisher"),
                () -> assertThat(created.getDescription()).isEqualTo("New description"),
                () -> assertThat(created.getPages()).isEqualTo(200),
                () -> assertThat(created.getAuthors()).extracting(AuthorEntity::getName).containsExactly("New Author"),
                () -> assertThat(created.getGenres()).extracting(GenreEntity::getName).containsExactly("New Genre"),
                () -> assertThat(created.getBorrowedBy()).isEqualTo("Adam Nowak"),
                () -> assertThat(created.getStatus()).isEqualTo(BookStatus.BORROWED),
                () -> assertThat(created.getRating()).isEqualTo(4),
                () -> assertThat(created.isFavorite()).isTrue(),
                () -> assertThat(created.getReadDate()).isEqualTo(LocalDate.of(2024, 3, 3)),
                () -> assertThat(created.getCoverImage()).isEqualTo("newImage".getBytes()),
                () -> assertThat(created.getCreationDate()).isCloseTo(LocalDateTime.now(), within(1, ChronoUnit.MINUTES)),
                () -> assertThat(created.getModificationDate()).isCloseTo(LocalDateTime.now(), within(1, ChronoUnit.MINUTES))
        );
    }

    @Test
    void shouldProperlyDuplicateBook() throws Exception {
        mockMvc.perform(post("/api/books/" + existingId + "/duplicate")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", not(existingId.toString())))
                .andExpect(jsonPath("$.title").value("Test Book (1)"))
                .andExpect(result -> {
                    String json = result.getResponse().getContentAsString();
                    LocalDateTime creationDate = LocalDateTime.parse(JsonPath.read(json, "$.creationDate"));
                    LocalDateTime modificationDate = LocalDateTime.parse(JsonPath.read(json, "$.modificationDate"));
                    assertAll(
                            () -> assertThat(creationDate)
                                    .isCloseTo(LocalDateTime.now(), within(1, ChronoUnit.MINUTES)),
                            () -> assertThat(modificationDate)
                                    .isCloseTo(LocalDateTime.now(), within(1, ChronoUnit.MINUTES))
                    );
                });
    }

    @Test
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
        BookEntity updated = bookRepository.findById(existingId).orElseThrow();
        assertAll(
                () -> assertThat(updated.getId()).isEqualTo(existingId),
                () -> assertThat(updated.getTitle()).isEqualTo("New Book"),
                () -> assertThat(updated.getIsbn()).isEqualTo("0987654321"),
                () -> assertThat(updated.getPublisher()).isEqualTo("New Publisher"),
                () -> assertThat(updated.getDescription()).isEqualTo("New description"),
                () -> assertThat(updated.getPages()).isEqualTo(200),
                () -> assertThat(updated.getAuthors()).extracting(AuthorEntity::getName).containsExactly("New Author"),
                () -> assertThat(updated.getGenres()).extracting(GenreEntity::getName).containsExactly("New Genre"),
                () -> assertThat(updated.getBorrowedBy()).isEqualTo("Adam Nowak"),
                () -> assertThat(updated.getStatus()).isEqualTo(BookStatus.BORROWED),
                () -> assertThat(updated.getRating()).isEqualTo(4),
                () -> assertThat(updated.isFavorite()).isFalse(),
                () -> assertThat(updated.getReadDate()).isEqualTo(LocalDate.of(2024, 4, 4)),
                () -> assertThat(updated.getCoverImage()).isEqualTo("replaceImage".getBytes()),
                () -> assertThat(updated.getCreationDate()).isCloseTo(LocalDateTime.now(), within(1, ChronoUnit.MINUTES)),
                () -> assertThat(updated.getModificationDate()).isCloseTo(LocalDateTime.now(), within(1, ChronoUnit.MINUTES))
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
