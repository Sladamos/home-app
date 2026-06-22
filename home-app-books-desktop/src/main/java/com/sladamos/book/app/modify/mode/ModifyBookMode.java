package com.sladamos.book.app.modify.mode;

import com.sladamos.book.app.modify.ModifyBookDraft;
import com.sladamos.book.app.modify.ModifyBookViewModel;
import com.sladamos.common.exception.ValidationException;
import com.sladamos.book.model.BookEntity;

public interface ModifyBookMode {

    String getModifyBookLabel();

    String getSubmitBookButtonKey();

    BookEntity convert(ModifyBookViewModel viewModel);

    ModifyBookDraft getBookDraft();

    void persist(BookEntity book) throws ValidationException;

    void onSuccess(BookEntity book);

    void onExit(ModifyBookViewModel viewModel);
}
