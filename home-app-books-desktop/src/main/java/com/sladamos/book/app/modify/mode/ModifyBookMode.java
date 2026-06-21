package com.sladamos.book.app.modify.mode;

import com.sladamos.book.app.modify.ModifyBookDraft;
import com.sladamos.book.app.modify.ModifyBookViewModel;
import com.sladamos.book.exception.BookValidationException;
import com.sladamos.book.model.Book;

public interface ModifyBookMode {

    String getModifyBookLabel();

    String getSubmitBookButtonKey();

    Book convert(ModifyBookViewModel viewModel);

    ModifyBookDraft getBookDraft();

    void persist(Book book) throws BookValidationException;

    void onSuccess(Book book);

    void onExit(ModifyBookViewModel viewModel);
}
