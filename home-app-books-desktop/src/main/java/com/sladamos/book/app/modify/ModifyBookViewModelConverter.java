package com.sladamos.book.app.modify;

import com.sladamos.book.Book;

public interface ModifyBookViewModelConverter {
    Book convert(ModifyBookViewModel modifyBookViewModel);
}
