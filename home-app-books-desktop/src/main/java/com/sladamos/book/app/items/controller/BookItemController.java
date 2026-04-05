package com.sladamos.book.app.items.controller;

import com.sladamos.app.util.LocaleProvider;
import com.sladamos.app.util.message.BindingsCreator;
import com.sladamos.app.util.message.TemporaryMessagesFactory;
import com.sladamos.book.app.items.viewmodel.BookItemViewModel;
import com.sladamos.book.app.items.event.OnBookDeleted;
import com.sladamos.book.app.items.event.OnBookDuplicated;
import com.sladamos.book.model.BookStatus;
import com.sladamos.book.app.items.event.OnEditBookClicked;
import com.sladamos.app.util.ui.LabelTextClamp;
import com.sladamos.book.app.common.CoverImageProvider;
import com.sladamos.book.app.items.StarFactory;
import com.sladamos.book.app.common.StatusMessageKeyProvider;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.StringBinding;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.text.MessageFormat;
import java.time.LocalDate;
import java.util.Optional;

import static javafx.scene.control.ButtonBar.ButtonData;

@Slf4j
@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
@RequiredArgsConstructor
public class BookItemController {

    @FXML
    private Label titleLabel;

    @FXML
    private Label statusLabel;

    @FXML
    private Label authorsLabel;

    @FXML
    private ImageView coverImageView;

    @FXML
    private HBox ratingStars;

    @FXML
    private Button inspectButton;

    @FXML
    private Button editButton;

    @FXML
    private Button duplicateButton;

    @FXML
    private Button deleteButton;

    private final LocaleProvider localeProvider;

    private final BindingsCreator bindingsCreator;

    private final StatusMessageKeyProvider statusMessageKeyProvider;

    private final StarFactory starFactory;

    private final ApplicationEventPublisher applicationEventPublisher;

    private final CoverImageProvider coverImageProvider;

    private final TemporaryMessagesFactory temporaryMessagesFactory;

    private final LabelTextClamp labelTextClamp;

    private BookItemViewModel viewModel;

    public void init(BookItemViewModel viewModel) {
        this.viewModel = viewModel;
    }

    @FXML
    public void initialize() {
        labelTextClamp.bindTwoLineClamp(titleLabel, viewModel.getTitle());
        authorsLabel.textProperty().bind(bindingsCreator.createBindingWithKey("books.items.authors", viewModel.getAuthors()));
        statusLabel.textProperty().bind(createStatusBinding());
        bindCoverImage();
        bindRatingStars();
        bindButton(inspectButton, "books.items.inspectButton");
        bindButton(editButton, "books.items.editButton");
        bindButton(duplicateButton, "books.items.duplicateButton");
        bindButton(deleteButton, "books.items.deleteButton");
    }

    @FXML
    private void onDeleteBookClicked() {
        log.info("Delete book button clicked");
        Alert alert = createDeleteConfirmationAlert();
        alert.setOnHidden(onDeleteConfirmationEnded(alert));
    }

    @FXML
    private void onDuplicateBookClicked() {
        log.info("Duplicate book button clicked");
        applicationEventPublisher.publishEvent(new OnBookDuplicated(viewModel.getBook()));
    }

    @FXML
    private void onEditBookClicked() {
        log.info("Edit book button clicked");
        applicationEventPublisher.publishEvent(new OnEditBookClicked(viewModel.getBook()));
    }

    private StringBinding createStatusBinding() {
        return Bindings.createStringBinding(
                () -> {
                    BookStatus bookStatus = viewModel.getStatus().get();
                    String key = statusMessageKeyProvider.getDisplayStatusMessageKey(bookStatus);
                    String message = bindingsCreator.getMessage(key);
                    if (BookStatus.BORROWED.equals(bookStatus)) {
                        return MessageFormat.format(message, viewModel.getBorrowedBy().get());
                    }
                    if (BookStatus.FINISHED_READING.equals(bookStatus)) {
                        String readDate = Optional.ofNullable(viewModel.getReadDate().get()).map(LocalDate::toString).orElse("");
                        return MessageFormat.format(message, readDate);
                    }
                    return message;
                },
                localeProvider.getLocaleProperty(),
                viewModel.getStatus(),
                viewModel.getBorrowedBy(),
                viewModel.getReadDate()
        );
    }

    private EventHandler<DialogEvent> onDeleteConfirmationEnded(Alert alert) {
        return event -> {
            ButtonType result = alert.getResult();
            log.info("Delete book button result: [result: {}]", result);
            if (result.getButtonData() == ButtonData.OK_DONE) {
                log.info("User confirmed deletion of book: [id: {}]", viewModel.getId().get());
                applicationEventPublisher.publishEvent(new OnBookDeleted(viewModel.getId().get(), viewModel.getTitle().get()));
            }
        };
    }

    private Alert createDeleteConfirmationAlert() {
        String message = bindingsCreator.getMessage("books.items.deletionConfirmed");
        String formattedMessage = MessageFormat.format(message, viewModel.getTitle().get());
        Image cover = coverImageProvider.getImageCover(viewModel.getCoverImage().get());
        return temporaryMessagesFactory.showConfirmation(formattedMessage, cover);
    }

    private void bindButton(Button button, String key) {
        button.textProperty().bind(bindingsCreator.createBinding(key));
    }

    private void bindCoverImage() {
        coverImageView.imageProperty().bind(Bindings.createObjectBinding(
                () -> coverImageProvider.getImageCover(viewModel.getCoverImage().get()),
                viewModel.getCoverImage()
        ));
    }

    private void bindRatingStars() {
        ratingStars.getChildren().setAll(starFactory.createStars(viewModel));
    }
}
