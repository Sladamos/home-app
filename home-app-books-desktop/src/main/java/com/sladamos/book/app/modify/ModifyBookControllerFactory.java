package com.sladamos.book.app.modify;

import com.sladamos.book.app.modify.mode.AddBookMode;
import com.sladamos.book.app.modify.mode.EditBookMode;
import com.sladamos.book.app.modify.mode.ModifyBookMode;
import com.sladamos.book.model.Book;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ModifyBookControllerFactory {

    private final ObjectProvider<ModifyBookController> controllerProvider;
    private final ObjectProvider<EditBookMode> editBookModeProvider;
    private final ModifyBookDataMapper modifyBookDataMapper;
    private final AddBookMode addBookMode;

    public ModifyBookController createForAdd() {
        log.info("Creating modify book controller for add");
        ModifyBookViewModel addViewModel = new ModifyBookViewModel();
        return createController(addViewModel, addBookMode);
    }

    public ModifyBookController createForEdit(Book book) {
        log.info("Creating modify book controller for edit");
        EditBookMode editMode = editBookModeProvider.getObject();
        editMode.init(book);
        ModifyBookViewModel editViewModel = new ModifyBookViewModel();
        return createController(editViewModel, editMode);
    }

    private ModifyBookController createController(ModifyBookViewModel viewModel, ModifyBookMode mode) {
        ModifyBookDraft draft = mode.getBookDraft();
        modifyBookDataMapper.updateViewModelFromDraft(viewModel, draft);
        ModifyBookController controller = controllerProvider.getObject();
        controller.init(viewModel, mode);
        return controller;
    }
}
