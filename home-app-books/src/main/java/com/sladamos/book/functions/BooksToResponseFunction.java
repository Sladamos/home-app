package com.sladamos.book.functions;

import com.sladamos.book.Book;
import com.sladamos.book.dto.GetBooksResponse;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.function.Function;

@Component
public class BooksToResponseFunction implements Function<List<Book>, GetBooksResponse> {
    @Override
    public GetBooksResponse apply(List<Book> books) {
        return GetBooksResponse.builder()
                .books(
                        books.stream()
                                .map(book -> GetBooksResponse.Book.builder()
                                        .id(book.getId())
                                        .title(book.getTitle())
                                        .isbn(book.getIsbn())
                                        .publisher(book.getPublisher())
                                        .authors(book.getAuthors())
                                        .pages(book.getPages())
                                        .coverImage(book.getCoverImage())
                                        .description(book.getDescription())
                                        .genres(book.getGenres())
                                        .build())
                                .toList()
                )
                .build();
    }
}
