package com.sladamos.book.app.modify.mode;

import com.sladamos.book.BookService;
import com.sladamos.book.app.modify.ModifyBookDataMapper;
import com.sladamos.book.app.modify.ModifyBookDraft;
import com.sladamos.book.app.modify.ModifyBookViewModel;
import com.sladamos.book.app.modify.event.OnBookCreated;
import com.sladamos.book.model.BookEntity;
import com.sladamos.common.exception.ValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AddBookMode implements ModifyBookMode {

    private final BookService bookService;
    private final ApplicationEventPublisher eventPublisher;
    private final ModifyBookDataMapper modifyBookDataMapper;
    private final ModifyBookDraft addBookDraft;

    @Override
    public String getModifyBookLabel() {
        return "books.add.name";
    }

    @Override
    public String getSubmitBookButtonKey() {
        return "books.add.name";
    }

    public ModifyBookDraft getBookDraft() {
        return addBookDraft;
    }

    @Override
    public BookEntity convert(ModifyBookViewModel viewModel) {
        return modifyBookDataMapper.toBookBuilder(viewModel)
                .build();
    }

    @Override
    public BookEntity persist(BookEntity book) throws ValidationException {
        return bookService.createBook(book);
    }

    @Override
    public void onSuccess(BookEntity book) {
        addBookDraft.reset();
        eventPublisher.publishEvent(new OnBookCreated(book));
    }

    @Override
    public void onExit(ModifyBookViewModel viewModel) {
        modifyBookDataMapper.updateDraftFromViewModel(addBookDraft, viewModel);
    }
}
