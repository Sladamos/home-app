package com.sladamos.book.app.edit;

import com.sladamos.book.Book;
import com.sladamos.book.app.modify.ModifyBookViewModel;
import com.sladamos.book.app.modify.ModifyBookViewModelConverter;
import io.micrometer.common.util.StringUtils;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

public class EditBookViewModelConverter implements ModifyBookViewModelConverter {

    @Override
    public Book convert(ModifyBookViewModel modifyBookViewModel) {
        return Book.builder()
                .id(modifyBookViewModel.getId().get())
                .title(modifyBookViewModel.getTitle().get())
                .isbn(modifyBookViewModel.getIsbn().get())
                .description(modifyBookViewModel.getDescription().get())
                .publisher(modifyBookViewModel.getPublisher().get())
                .borrowedBy(modifyBookViewModel.getBorrowedBy().get())
                .pages(modifyBookViewModel.getPages().get())
                .rating(modifyBookViewModel.getRating().get())
                .favorite(modifyBookViewModel.getFavorite().get())
                .readDate(modifyBookViewModel.getReadDate().get())
                .coverImage(modifyBookViewModel.getCoverImage().get())
                .status(modifyBookViewModel.getStatus().get())
                .authors(modifyBookViewModel.getAuthors().stream().filter(StringUtils::isNotBlank).collect(Collectors.toSet()))
                .genres(modifyBookViewModel.getGenres().stream().filter(StringUtils::isNotBlank).collect(Collectors.toSet()))
                .creationDate(modifyBookViewModel.getCreationDate().get())
                .modificationDate(LocalDateTime.now())
                .build();
    }
}
