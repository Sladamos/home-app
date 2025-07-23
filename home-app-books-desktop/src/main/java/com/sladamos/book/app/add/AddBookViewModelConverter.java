package com.sladamos.book.app.add;

import com.sladamos.book.Book;
import com.sladamos.book.app.modify.ModifyBookViewModel;
import com.sladamos.book.app.modify.ModifyBookViewModelConverter;
import io.micrometer.common.util.StringUtils;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

@NoArgsConstructor
@Component
public class AddBookViewModelConverter implements ModifyBookViewModelConverter {
    @Override
    public Book convert(ModifyBookViewModel modifyBookViewModel) {
        LocalDateTime currentDate = LocalDateTime.now();
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
                .creationDate(currentDate)
                .modificationDate(currentDate)
                .build();
    }
}
