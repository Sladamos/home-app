package com.sladamos.book.app.items;

import com.sladamos.app.util.messages.BindingsCreator;
import com.sladamos.app.util.LocaleProvider;
import com.sladamos.app.util.messages.TemporaryMessagesFactory;
import com.sladamos.book.app.util.CoverImageProvider;
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

    private final CoverImageProvider coverImageProvider;

    private final TemporaryMessagesFactory temporaryMessagesFactory;

    public BookItemController createController(BookItemViewModel viewModel) {
        return new BookItemController(viewModel, localeProvider, bindingsCreator, statusMessageKeyProvider,
                starsFactory, applicationEventPublisher, coverImageProvider, temporaryMessagesFactory);
    }
}
