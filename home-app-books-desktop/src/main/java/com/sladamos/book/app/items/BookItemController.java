package com.sladamos.book.app.items;

import com.sladamos.app.util.messages.BindingsCreator;
import com.sladamos.app.util.LocaleProvider;
import com.sladamos.book.BookStatus;
import com.sladamos.book.app.edit.OnEditBookClicked;
import com.sladamos.book.app.util.CoverImageProvider;
import com.sladamos.book.app.util.StarsFactory;
import com.sladamos.book.app.util.StatusMessageKeyProvider;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.StringBinding;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;

import java.text.MessageFormat;
import java.time.LocalDate;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
public class BookItemController {

    @FXML
    private Label titleLabel;

    @FXML
    private Label descriptionLabel;

    @FXML
    private Label publisherLabel;

    @FXML
    private Label pagesLabel;

    @FXML
    private Label statusLabel;

    @FXML
    private Label authorsLabel;

    @FXML
    private Label genresLabel;

    @FXML
    private ImageView coverImageView;

    @FXML
    private HBox ratingStars;

    @FXML
    private HBox genresBox;

    @FXML
    private HBox publisherBox;

    @FXML
    private HBox pagesBox;

    @FXML
    private Button inspectButton;

    @FXML
    private Button editButton;

    @FXML
    private Button deleteButton;

    private final BookItemViewModel viewModel;

    private final LocaleProvider localeProvider;

    private final BindingsCreator bindingsCreator;

    private final StatusMessageKeyProvider statusMessageKeyProvider;

    private final StarsFactory starsFactory;

    private final ApplicationEventPublisher applicationEventPublisher;

    private final CoverImageProvider coverImageProvider;

    @FXML
    public void initialize() {
        titleLabel.textProperty().bind(viewModel.getTitle());
        descriptionLabel.textProperty().bind(viewModel.getDescription());

        publisherLabel.textProperty().bind(bindingsCreator.createBindingWithKey("books.items.publisher", viewModel.getPublisher()));
        pagesLabel.textProperty().bind(bindingsCreator.createBindingWithKey("books.items.pages", viewModel.getPages()));
        authorsLabel.textProperty().bind(bindingsCreator.createBindingWithKey("books.items.authors", viewModel.getAuthors()));
        genresLabel.textProperty().bind(bindingsCreator.createBindingWithKey("books.items.genres", viewModel.getGenres()));
        statusLabel.textProperty().bind(createStatusBinding());
        coverImageView.imageProperty().bind(Bindings.createObjectBinding(
                () -> coverImageProvider.getImageCover(viewModel.getCoverImage().get()),
                viewModel.getCoverImage()
        ));

        ratingStars.getChildren().clear();
        ratingStars.getChildren().addAll(starsFactory.createStars(viewModel));

        inspectButton.textProperty().bind(bindingsCreator.createBinding("books.items.inspectButton"));
        editButton.textProperty().bind(bindingsCreator.createBinding("books.items.editButton"));
        deleteButton.textProperty().bind(bindingsCreator.createBinding("books.items.deleteButton"));

        setupFieldsVisibility();
    }

    @FXML
    private void onDeleteBookClicked() {
        log.info("Delete book button clicked");
        applicationEventPublisher.publishEvent(new OnBookDeleted(viewModel.getId().get(), viewModel.getTitle().get()));
    }

    @FXML
    private void onEditBookClicked() {
        log.info("Edit book button clicked");
        applicationEventPublisher.publishEvent(new OnEditBookClicked(viewModel.getBook()));
    }

    private void setupFieldsVisibility() {
        genresBox.visibleProperty().bind(viewModel.getGenres().isNotEmpty());
        genresBox.managedProperty().bind(genresBox.visibleProperty());
        publisherBox.visibleProperty().bind(viewModel.getPublisher().isNotEmpty());
        publisherBox.managedProperty().bind(publisherBox.visibleProperty());
        pagesBox.visibleProperty().bind(viewModel.getPages().isNotEqualTo(0));
        pagesBox.managedProperty().bind(pagesBox.visibleProperty());
    }

    private StringBinding createStatusBinding() {
        return Bindings.createStringBinding(
                () -> {
                    var bookStatus = viewModel.getStatus().get();
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
}
