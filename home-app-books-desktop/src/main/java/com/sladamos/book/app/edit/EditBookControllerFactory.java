package com.sladamos.book.app.edit;

import com.sladamos.app.util.messages.BindingsCreator;
import com.sladamos.app.util.components.ComponentsGenerator;
import com.sladamos.book.Book;
import com.sladamos.book.BookService;
import com.sladamos.book.app.modify.ModifyBookViewModelConverter;
import com.sladamos.book.app.modify.ModifyBookController;
import com.sladamos.book.app.modify.ModifyBookViewModel;
import com.sladamos.book.app.modify.components.SelectCoverController;
import com.sladamos.book.app.modify.components.SelectRatingController;
import com.sladamos.book.app.modify.components.SelectStatusController;
import com.sladamos.book.app.modify.validation.ValidationsOperator;
import com.sladamos.book.app.modify.validation.ViolationDisplayerFactory;
import com.sladamos.app.util.components.FocusableFinder;
import com.sladamos.app.util.components.NodeScroller;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EditBookControllerFactory {

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

    public ModifyBookController createEditController(Book book) {
        ModifyBookViewModel viewModel = new ModifyBookViewModel(book);
        ModifyBookViewModelConverter converter = new EditBookViewModelConverter();
        return new ModifyBookController(selectCoverController,
                selectRatingController, selectStatusController, applicationEventPublisher, bindingsCreator,
                componentsGenerator, viewModel, bookService, focusableFinder, nodeScroller,
                validationsOperator, violationDisplayerFactory, converter);

    }
}
