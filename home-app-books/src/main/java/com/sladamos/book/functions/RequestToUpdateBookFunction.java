package com.sladamos.book.functions;

import com.sladamos.book.Book;
import com.sladamos.book.dto.PatchBookRequest;
import org.springframework.stereotype.Component;

import java.util.function.BiFunction;

@Component
public class RequestToUpdateBookFunction implements BiFunction<Book, PatchBookRequest, Book> {

    @Override
    public Book apply(Book entity, PatchBookRequest patchBookRequest) {
        return Book.builder()
                .id(entity.getId())
                .title(patchBookRequest.getTitle() == null || patchBookRequest.getTitle().isEmpty() ? entity.getTitle() : patchBookRequest.getTitle())
                .isbn(patchBookRequest.getIsbn() == null || patchBookRequest.getIsbn().isEmpty() ? entity.getIsbn() : patchBookRequest.getIsbn())
                .publisher(patchBookRequest.getPublisher() == null || patchBookRequest.getPublisher().isEmpty() ? entity.getPublisher() : patchBookRequest.getPublisher())
                .description(patchBookRequest.getDescription() == null || patchBookRequest.getDescription().isEmpty() ? entity.getDescription() : patchBookRequest.getDescription())
                .pages(patchBookRequest.getPages() == null ? entity.getPages() : patchBookRequest.getPages())
                .coverImage(patchBookRequest.getCoverImage() == null ? entity.getCoverImage() : patchBookRequest.getCoverImage())
                .authors(patchBookRequest.getAuthors() == null ? entity.getAuthors() : patchBookRequest.getAuthors())
                .genres(patchBookRequest.getGenres() == null ? entity.getGenres() : patchBookRequest.getGenres())
                .build();
    }
}
