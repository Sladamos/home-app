package com.sladamos.book.app.modify.mode;

import com.sladamos.book.BookService;
import com.sladamos.book.app.modify.ModifyBookDataMapper;
import com.sladamos.book.app.modify.ModifyBookDraft;
import com.sladamos.book.app.modify.ModifyBookViewModel;
import com.sladamos.book.app.modify.event.OnBookEdited;
import com.sladamos.book.model.BookEntity;
import com.sladamos.common.exception.ValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
@RequiredArgsConstructor
public class EditBookMode implements ModifyBookMode {

    private final BookService bookService;
    private final ApplicationEventPublisher eventPublisher;
    private final ModifyBookDataMapper modifyBookDataMapper;
    private final ModifyBookDraft editBookDraft;

    private BookEntity originalBook;

    public void init(BookEntity originalBook) {
        this.originalBook = originalBook;
        modifyBookDataMapper.updateDraftFromBook(editBookDraft, originalBook);
    }

    @Override
    public String getModifyBookLabel() {
        return "books.edit.name";
    }

    @Override
    public String getSubmitBookButtonKey() {
        return "books.edit.name";
    }

    @Override
    public ModifyBookDraft getBookDraft() {
        return editBookDraft;
    }

    @Override
    public BookEntity convert(ModifyBookViewModel viewModel) {
        return modifyBookDataMapper.toBookBuilder(viewModel)
                .build();
    }

    @Override
    public BookEntity persist(BookEntity book) throws ValidationException {
       return bookService.updateBook(book);
    }

    @Override
    public void onSuccess(BookEntity book) {
        eventPublisher.publishEvent(new OnBookEdited(book));
    }

    @Override
    public void onExit(ModifyBookViewModel viewModel) {
    }
}
