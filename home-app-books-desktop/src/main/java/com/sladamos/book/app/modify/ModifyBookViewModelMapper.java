package com.sladamos.book.app.modify;

import com.sladamos.book.model.Author;
import com.sladamos.book.model.Book;
import com.sladamos.book.model.Genre;
import io.micrometer.common.util.StringUtils;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.stream.Collectors;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ModifyBookViewModelMapper {

    public static Book.BookBuilder toBookBuilder(ModifyBookViewModel viewModel) {
        return Book.builder()
                .id(viewModel.getId().get())
                .title(viewModel.getTitle().get())
                .isbn(viewModel.getIsbn().get())
                .description(viewModel.getDescription().get())
                .publisher(viewModel.getPublisher().get())
                .borrowedBy(viewModel.getBorrowedBy().get())
                .pages(viewModel.getPages().get())
                .rating(viewModel.getRating().get())
                .favorite(viewModel.getFavorite().get())
                .readDate(viewModel.getReadDate().get())
                .coverImage(viewModel.getCoverImage().get())
                .status(viewModel.getStatus().get())
                .authors(viewModel.getAuthors().stream()
                        .filter(StringUtils::isNotBlank)
                        .map(Author::new)
                        .collect(Collectors.toSet()))
                .genres(viewModel.getGenres().stream()
                        .filter(StringUtils::isNotBlank)
                        .map(Genre::new)
                        .collect(Collectors.toSet()));
    }
}
