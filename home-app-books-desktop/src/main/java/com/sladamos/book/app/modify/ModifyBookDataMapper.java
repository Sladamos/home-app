package com.sladamos.book.app.modify;

import com.sladamos.book.model.Author;
import com.sladamos.book.model.Book;
import com.sladamos.book.model.Genre;
import io.micrometer.common.util.StringUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ModifyBookDataMapper {

    public void updateDraftFromViewModel(ModifyBookDraft draft, ModifyBookViewModel vm) {
        draft.setTitle(vm.getTitle().get());
        draft.setIsbn(vm.getIsbn().get());
        draft.setDescription(vm.getDescription().get());
        draft.setPublisher(vm.getPublisher().get());
        draft.setBorrowedBy(vm.getBorrowedBy().get());
        draft.setPages(vm.getPages().get());
        draft.setRating(vm.getRating().get());
        draft.setFavorite(vm.getFavorite().get());
        draft.setReadDate(vm.getReadDate().get());
        draft.setCoverImage(vm.getCoverImage().get());
        draft.setStatus(vm.getStatus().get());

        draft.getAuthors().clear();
        draft.getAuthors().addAll(vm.getAuthors());

        draft.getGenres().clear();
        draft.getGenres().addAll(vm.getGenres());
    }

    public void updateViewModelFromDraft(ModifyBookViewModel vm, ModifyBookDraft draft) {
        vm.getId().set(draft.getId());
        vm.getTitle().set(draft.getTitle());
        vm.getIsbn().set(draft.getIsbn());
        vm.getDescription().set(draft.getDescription());
        vm.getPublisher().set(draft.getPublisher());
        vm.getBorrowedBy().set(draft.getBorrowedBy());
        vm.getPages().set(draft.getPages());
        vm.getRating().set(draft.getRating());
        vm.getFavorite().set(draft.isFavorite());
        vm.getReadDate().set(draft.getReadDate());
        vm.getCoverImage().set(draft.getCoverImage());
        vm.getStatus().set(draft.getStatus());
        vm.getAuthors().setAll(draft.getAuthors());
        vm.getGenres().setAll(draft.getGenres());
    }

    public void updateDraftFromBook(ModifyBookDraft draft, Book book) {
        draft.setId(book.getId());
        draft.setTitle(book.getTitle());
        draft.setIsbn(book.getIsbn());
        draft.setDescription(book.getDescription());
        draft.setPublisher(book.getPublisher());
        draft.setBorrowedBy(book.getBorrowedBy());
        draft.setPages(book.getPages());
        draft.setRating(book.getRating());
        draft.setFavorite(book.isFavorite());
        draft.setReadDate(book.getReadDate());
        draft.setCoverImage(book.getCoverImage());
        draft.setStatus(book.getStatus());
        draft.getAuthors().addAll(book.getAuthors().stream().map(Author::getName).collect(Collectors.toSet()));
        draft.getGenres().addAll(book.getGenres().stream().map(Genre::getName).collect(Collectors.toSet()));
    }

    public Book.BookBuilder toBookBuilder(ModifyBookViewModel viewModel) {
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