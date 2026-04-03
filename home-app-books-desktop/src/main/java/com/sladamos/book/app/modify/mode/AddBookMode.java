package com.sladamos.book.app.modify.mode;

import com.sladamos.book.app.modify.event.OnBookCreated;
import com.sladamos.book.app.modify.ModifyBookViewModel;
import com.sladamos.book.app.modify.ModifyBookViewModelMapper;
import com.sladamos.book.exception.BookValidationException;
import com.sladamos.book.model.Book;
import com.sladamos.book.BookService;
import org.springframework.context.ApplicationEventPublisher;

import java.time.LocalDateTime;

public class AddBookMode implements ModifyBookMode {

    @Override
    public String getModifyBookLabel() {
        return "books.add.name";
    }

    @Override
    public String getSubmitBookButtonKey() {
        return "books.add.name";
    }

    @Override
    public Book convert(ModifyBookViewModel viewModel) {
        LocalDateTime now = LocalDateTime.now();
        return ModifyBookViewModelMapper.toBookBuilder(viewModel)
                .creationDate(now)
                .modificationDate(now)
                .build();
    }

    @Override
    public void persist(BookService bookService, Book book) throws BookValidationException {
        bookService.createBook(book);
    }

    @Override
    public void onSuccess(ApplicationEventPublisher publisher, Book book) {
        publisher.publishEvent(new OnBookCreated(book));
    }

    @Override
    public boolean shouldResetAfterSubmit() {
        return true;
    }
}
