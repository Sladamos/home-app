package com.sladamos.book.app.items;

import com.sladamos.app.util.BindingsCreator;
import com.sladamos.app.util.LocaleProvider;
import com.sladamos.book.app.util.StarsFactory;
import com.sladamos.book.app.util.StatusMessageKeyProvider;
import lombok.AllArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class BookItemControllerFactory {

    private final BindingsCreator bindingsCreator;

    private final StatusMessageKeyProvider statusMessageKeyProvider;

    private final LocaleProvider localeProvider;

    private final StarsFactory starsFactory;

    private final ApplicationEventPublisher applicationEventPublisher;

    public BookItemController createController(BookItemViewModel viewModel) {
        return new BookItemController(viewModel, localeProvider, bindingsCreator, statusMessageKeyProvider, starsFactory, applicationEventPublisher);
    }
}
