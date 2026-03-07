package com.sladamos.book.app.modify;

import com.sladamos.book.model.Book;

public interface ModifyBookViewModelConverter {
    Book convert(ModifyBookViewModel modifyBookViewModel);
}
