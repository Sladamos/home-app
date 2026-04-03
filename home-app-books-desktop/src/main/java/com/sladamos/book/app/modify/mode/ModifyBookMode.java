package com.sladamos.book.app.modify.mode;

import com.sladamos.book.BookService;
import com.sladamos.book.app.modify.ModifyBookViewModel;
import com.sladamos.book.exception.BookValidationException;
import com.sladamos.book.model.Book;
import org.springframework.context.ApplicationEventPublisher;

public interface ModifyBookMode {

    String getModifyBookLabel();

    String getSubmitBookButtonKey();

    Book convert(ModifyBookViewModel viewModel);

    void persist(BookService bookService, Book book) throws BookValidationException;

    void onSuccess(ApplicationEventPublisher publisher, Book book);

    boolean shouldResetAfterSubmit();
}
