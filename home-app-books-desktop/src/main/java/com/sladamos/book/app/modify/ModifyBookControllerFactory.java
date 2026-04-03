package com.sladamos.book.app.modify;

import com.sladamos.app.util.messages.BindingsCreator;
import com.sladamos.app.util.components.ComponentsGenerator;
import com.sladamos.book.BookService;
import com.sladamos.book.app.modify.component.cover.SelectCoverController;
import com.sladamos.book.app.modify.component.rating.SelectRatingController;
import com.sladamos.book.app.modify.component.status.SelectStatusController;
import com.sladamos.book.app.modify.mode.ModifyBookMode;
import com.sladamos.book.app.modify.mode.AddBookMode;
import com.sladamos.book.app.modify.mode.EditBookMode;
import com.sladamos.book.app.modify.validation.ModifyBookValidationHandler;
import com.sladamos.book.app.modify.validation.ValidationsOperator;
import com.sladamos.book.app.modify.validation.ViolationDisplayerFactory;
import com.sladamos.book.model.Book;
import com.sladamos.app.util.components.FocusableFinder;
import com.sladamos.app.util.components.NodeScroller;
import javafx.util.Callback;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ModifyBookControllerFactory {

    private final ApplicationContext context;
    private final SelectCoverController selectCoverController;
    private final SelectRatingController selectRatingController;
    private final SelectStatusController selectStatusController;
    private final ApplicationEventPublisher applicationEventPublisher;
    private final BindingsCreator bindingsCreator;
    private final ComponentsGenerator componentsGenerator;
    private final BookService bookService;
    private final FocusableFinder focusableFinder;
    private final NodeScroller nodeScroller;
    private final ValidationsOperator validationsOperator;
    private final ViolationDisplayerFactory violationDisplayerFactory;

    private final ModifyBookViewModel addViewModel = new ModifyBookViewModel();

    public Callback<Class<?>, Object> forAdd() {
        log.info("Creating modify book view model for add");
        return toControllerFactory(createController(addViewModel, new AddBookMode()));
    }

    public Callback<Class<?>, Object> forEdit(Book book) {
        log.info("Creating modify book view model for edit");
        ModifyBookViewModel editViewModel = new ModifyBookViewModel();
        editViewModel.initFrom(book);
        return toControllerFactory(createController(editViewModel, new EditBookMode(book)));
    }

    private Callback<Class<?>, Object> toControllerFactory(ModifyBookController controller) {
        return clazz -> ModifyBookController.class.equals(clazz) ? controller : context.getBean(clazz);
    }

    private ModifyBookController createController(ModifyBookViewModel viewModel, ModifyBookMode mode) {
        return new ModifyBookController(
                applicationEventPublisher,
                viewModel,
                mode,
                bookService,
                bindingsCreator,
                componentsGenerator,
                selectCoverController,
                selectRatingController,
                selectStatusController,
                new ModifyBookValidationHandler(validationsOperator, violationDisplayerFactory, focusableFinder, nodeScroller)
        );
    }
}
