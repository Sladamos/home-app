package com.sladamos.book.app.modify;

import com.sladamos.app.util.messages.BindingsCreator;
import com.sladamos.book.app.items.event.OnDisplayItemsClicked;
import com.sladamos.book.app.modify.component.cover.SelectCoverController;
import com.sladamos.book.app.modify.component.fields.MultipleFieldsController;
import com.sladamos.book.app.modify.component.fields.MultipleFieldsViewModel;
import com.sladamos.book.app.modify.component.rating.SelectRatingController;
import com.sladamos.book.app.modify.component.rating.SelectRatingViewModel;
import com.sladamos.book.app.modify.component.status.SelectStatusController;
import com.sladamos.book.app.modify.component.status.SelectStatusViewModel;
import com.sladamos.book.app.modify.mode.ModifyBookMode;
import com.sladamos.book.app.modify.validation.ModifyBookValidationHandler;
import com.sladamos.book.exception.BookValidationException;
import com.sladamos.book.model.Book;
import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import static com.sladamos.book.model.Book.MIN_NUMBER_OF_AUTHORS;
import static com.sladamos.book.model.Book.MIN_NUMBER_OF_GENRES;

@Slf4j
@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
@RequiredArgsConstructor
public class ModifyBookController {

    @FXML
    private ScrollPane formScrollPane;

    @FXML
    private Label titleValidationLabel;

    @FXML
    private Label authorsValidationLabel;

    @FXML
    private Label isbnValidationLabel;

    @FXML
    private Label genresValidationLabel;

    @FXML
    private Label pagesValidationLabel;

    @FXML
    private Label descriptionValidationLabel;

    @FXML
    private Label titleLabel;

    @FXML
    private Label authorsLabel;

    @FXML
    private Label publisherLabel;

    @FXML
    private Label genresLabel;

    @FXML
    private Label pagesLabel;

    @FXML
    private Label descriptionLabel;

    @FXML
    private Label submitBookLabel;

    @FXML
    private HBox genresWrapper;

    @FXML
    private Button returnToItemsButton;

    @FXML
    private Button submitBookButton;

    @FXML
    private TextField titleField;

    @FXML
    private TextField isbnField;

    @FXML
    private TextArea descriptionArea;

    @FXML
    private TextField publisherField;

    @FXML
    private TextField pagesField;

    @FXML
    private Button addAuthorButton;

    @FXML
    private Button addGenreButton;

    @FXML
    private SelectCoverController selectCoverController;

    @FXML
    private SelectRatingController selectRatingController;

    @FXML
    private SelectStatusController selectStatusController;

    @FXML
    private MultipleFieldsController authorsMultipleFieldsController;

    @FXML
    private MultipleFieldsController genresMultipleFieldsController;


    private final ApplicationEventPublisher applicationEventPublisher;
    private final BindingsCreator bindingsCreator;
    private final ModifyBookValidationHandler validationHandler;
    private ModifyBookViewModel viewModel;
    private ModifyBookMode mode;

    public void init(ModifyBookViewModel viewModel, ModifyBookMode mode) {
        this.viewModel = viewModel;
        this.mode = mode;
    }

    @FXML
    public void initialize() {
        initializeMultipleFields();
        initializeChildComponents();
        initializeMessages();
        bindScalarFields();
        bindGenresVisibility();
        bindPagesField();
        initializeValidation();
    }

    @FXML
    private void onReturnButtonClicked() {
        log.info("Return to items button clicked");
        mode.onExit(viewModel);
        applicationEventPublisher.publishEvent(new OnDisplayItemsClicked());
    }

    @FXML
    private void onSubmitBookClicked() {
        log.info("Submit button clicked");
        validationHandler.clear();
        Book book = mode.convert(viewModel);
        try {
            mode.persist(book);
            mode.onSuccess(book);
        } catch (BookValidationException e) {
            log.error("Book validation failed: [reason: {}]", e.getMessage());
            validationHandler.display(e.getViolations());
        }
    }

    @FXML
    private void onAddAuthorClicked() {
        log.info("Add author button clicked");
        authorsMultipleFieldsController.addEmptyField();
    }

    @FXML
    private void onAddGenreClicked() {
        log.info("Add genre button clicked");
        genresMultipleFieldsController.addEmptyField();
    }

    private void initializeMultipleFields() {
        authorsMultipleFieldsController.bindTo(new MultipleFieldsViewModel(viewModel.getAuthors(), MIN_NUMBER_OF_AUTHORS));
        genresMultipleFieldsController.bindTo(new MultipleFieldsViewModel(viewModel.getGenres(), MIN_NUMBER_OF_GENRES));
    }

    private void initializeChildComponents() {
        selectCoverController.bindTo(viewModel);
        selectRatingController.bindTo(new SelectRatingViewModel(viewModel.getRating(), viewModel.getFavorite()));
        selectStatusController.bindTo(new SelectStatusViewModel(viewModel.getBorrowedBy(), viewModel.getReadDate(), viewModel.getStatus()));
    }

    private void initializeMessages() {
        submitBookLabel.setText(bindingsCreator.getMessage(mode.getModifyBookLabel()));
        submitBookButton.setText(bindingsCreator.getMessage(mode.getSubmitBookButtonKey()));

        returnToItemsButton.textProperty().bind(bindingsCreator.createBinding("books.add.returnToBooks"));
        addAuthorButton.textProperty().bind(bindingsCreator.createBinding("books.add.addAuthor"));
        addGenreButton.textProperty().bind(bindingsCreator.createBinding("books.add.addGenre"));

        titleLabel.textProperty().bind(bindingsCreator.createBinding("books.add.title"));
        authorsLabel.textProperty().bind(bindingsCreator.createBinding("books.add.authors"));
        publisherLabel.textProperty().bind(bindingsCreator.createBinding("books.add.publisher"));
        genresLabel.textProperty().bind(bindingsCreator.createBinding("books.add.genres"));
        pagesLabel.textProperty().bind(bindingsCreator.createBinding("books.add.pages"));
        descriptionLabel.textProperty().bind(bindingsCreator.createBinding("books.add.description"));
    }

    private void bindScalarFields() {
        titleField.textProperty().bindBidirectional(viewModel.getTitle());
        isbnField.textProperty().bindBidirectional(viewModel.getIsbn());
        descriptionArea.textProperty().bindBidirectional(viewModel.getDescription());
        publisherField.textProperty().bindBidirectional(viewModel.getPublisher());
    }

    private void bindGenresVisibility() {
        genresWrapper.visibleProperty().bind(Bindings.isEmpty(viewModel.getGenres()).not());
        genresWrapper.managedProperty().bind(genresWrapper.visibleProperty());
    }

    private void bindPagesField() {
        TextFormatter<Integer> integerFormatter = new TextFormatter<>(change -> change.getControlNewText().matches("\\d*") ? change : null);
        pagesField.setTextFormatter(integerFormatter);
        Bindings.bindBidirectional(
                pagesField.textProperty(),
                viewModel.getPages(),
                new javafx.util.converter.NumberStringConverter()
        );
    }

    private void initializeValidation() {
        validationHandler.initialize(
                formScrollPane,
                titleValidationLabel,
                authorsValidationLabel,
                isbnValidationLabel,
                genresValidationLabel,
                pagesValidationLabel,
                descriptionValidationLabel,
                selectStatusController.getBorrowedByValidationLabel()
        );
    }
}
