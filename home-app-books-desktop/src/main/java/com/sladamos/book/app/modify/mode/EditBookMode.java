package com.sladamos.book.app.modify.mode;

import com.sladamos.book.app.modify.event.OnBookEdited;
import com.sladamos.book.app.modify.ModifyBookViewModel;
import com.sladamos.book.app.modify.ModifyBookViewModelMapper;
import com.sladamos.book.exception.BookValidationException;
import com.sladamos.book.model.Book;
import com.sladamos.book.BookService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;

import java.time.LocalDateTime;

@RequiredArgsConstructor
public class EditBookMode implements ModifyBookMode {

    private final Book originalBook;

    @Override
    public String getModifyBookLabel() {
        return "books.edit.name";
    }

    @Override
    public String getSubmitBookButtonKey() {
        return "books.edit.name";
    }

    @Override
    public Book convert(ModifyBookViewModel viewModel) {
        return ModifyBookViewModelMapper.toBookBuilder(viewModel)
                .creationDate(originalBook.getCreationDate())
                .modificationDate(LocalDateTime.now())
                .build();
    }

    @Override
    public void persist(BookService bookService, Book book) throws BookValidationException {
        bookService.updateBook(book);
    }

    @Override
    public void onSuccess(ApplicationEventPublisher publisher, Book book) {
        publisher.publishEvent(new OnBookEdited(book));
    }

    @Override
    public boolean shouldResetAfterSubmit() {
        return false;
    }
}
