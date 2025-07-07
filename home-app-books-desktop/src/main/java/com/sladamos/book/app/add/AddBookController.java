package com.sladamos.book.app.add;

import com.sladamos.app.util.BindingsCreator;
import com.sladamos.app.util.ComponentsGenerator;
import com.sladamos.book.Book;
import com.sladamos.book.app.common.*;
import com.sladamos.book.app.items.OnDisplayItemsClicked;
import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import java.net.URL;

import static com.sladamos.book.Book.MIN_NUMBER_OF_GENRES;

@Slf4j
@Component
public class AddBookController {

    private static final URL MULTIPLE_FIELDS_COMPONENT_RESOURCE = MultipleFieldsController.class.getResource("MultipleFields.fxml");

    @FXML
    private Label titleLabel;

    @FXML
    private Label publisherLabel;

    @FXML
    private Label pagesLabel;

    @FXML
    private Label descriptionLabel;

    @FXML
    private Label borrowedToLabel;

    @FXML
    private Label addBookLabel;

    @FXML
    private Pane genresWrapper;

    @FXML
    private Pane authorsWrapper;

    @FXML
    private Button returnToItemsButton;

    @FXML
    private Button addBookButton;

    @FXML
    private TextField titleField;

    @FXML
    private TextField isbnField;

    @FXML
    private TextArea descriptionArea;

    @FXML
    private DatePicker readDatePicker;

    @FXML
    private TextField publisherField;

    @FXML
    private TextField borrowedByField;

    @FXML
    private TextField pagesField;

    @FXML
    private Button addAuthorButton;

    @FXML
    private Button addGenreButton;

    private final SelectCoverController selectCoverController;

    private final SelectRatingController selectRatingController;

    private final ApplicationEventPublisher applicationEventPublisher;

    private final BindingsCreator bindingsCreator;

    private final ComponentsGenerator componentsGenerator;

    private final AddBookViewModel viewModel;

    private final MultipleFieldsController authorsMultipleFieldsController;

    private final MultipleFieldsController genresMultipleFieldsController;

    public AddBookController(MultipleFieldsControllerFactory multipleFieldsControllerFactory,
                             SelectCoverController selectCoverController,
                             SelectRatingController selectRatingController,
                             ApplicationEventPublisher applicationEventPublisher,
                             BindingsCreator bindingsCreator,
                             ComponentsGenerator componentsGenerator,
                             AddBookViewModel viewModel) {
        this.selectCoverController = selectCoverController;
        this.selectRatingController = selectRatingController;
        this.applicationEventPublisher = applicationEventPublisher;
        this.bindingsCreator = bindingsCreator;
        this.componentsGenerator = componentsGenerator;
        this.viewModel = viewModel;
        authorsMultipleFieldsController = multipleFieldsControllerFactory.createMultipleFieldsController("books.multipleFields.authors");
        genresMultipleFieldsController = multipleFieldsControllerFactory.createMultipleFieldsController("books.multipleFields.genres");
    }

    @FXML
    public void initialize() {
        componentsGenerator.addComponent(genresMultipleFieldsController, genresWrapper, MULTIPLE_FIELDS_COMPONENT_RESOURCE);
        componentsGenerator.addComponent(authorsMultipleFieldsController, authorsWrapper, MULTIPLE_FIELDS_COMPONENT_RESOURCE);

        setupBindings();
    }

    @FXML
    private void onReturnButtonClicked() {
        log.info("Return to items button clicked");
        applicationEventPublisher.publishEvent(new OnDisplayItemsClicked());
    }

    @FXML
    private void onAddBookClicked() {
        log.info("Add book button clicked");
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

    private void setupBindings() {
        selectCoverController.bindTo(viewModel);
        selectRatingController.bindTo(new SelectRatingViewModel(viewModel.getRating(), viewModel.getFavorite()));
        authorsMultipleFieldsController.bindTo(new MultipleFieldsViewModel(viewModel.getAuthors(), Book.MIN_NUMBER_OF_AUTHORS));
        genresMultipleFieldsController.bindTo(new MultipleFieldsViewModel(viewModel.getGenres(), MIN_NUMBER_OF_GENRES));
        genresWrapper.visibleProperty().bind(Bindings.isEmpty(viewModel.getGenres()).not());
        genresWrapper.managedProperty().bind(genresWrapper.visibleProperty());

        addBookLabel.textProperty().bind(bindingsCreator.createBinding("books.add.name"));
        returnToItemsButton.textProperty().bind(bindingsCreator.createBinding("books.add.returnToBooks"));
        addAuthorButton.textProperty().bind(bindingsCreator.createBinding("books.add.addAuthor"));
        addGenreButton.textProperty().bind(bindingsCreator.createBinding("books.add.addGenre"));

        titleLabel.textProperty().bind(bindingsCreator.createBinding("books.add.title"));
        publisherLabel.textProperty().bind(bindingsCreator.createBinding("books.add.publisher"));
        borrowedToLabel.textProperty().bind(bindingsCreator.createBinding("books.add.borrowedTo"));
        pagesLabel.textProperty().bind(bindingsCreator.createBinding("books.add.pages"));
        descriptionLabel.textProperty().bind(bindingsCreator.createBinding("books.add.description"));

        titleField.textProperty().bindBidirectional(viewModel.getTitle());
        isbnField.textProperty().bindBidirectional(viewModel.getIsbn());
        descriptionArea.textProperty().bindBidirectional(viewModel.getDescription());
        publisherField.textProperty().bindBidirectional(viewModel.getPublisher());
        borrowedByField.textProperty().bindBidirectional(viewModel.getBorrowedBy());
        readDatePicker.valueProperty().bindBidirectional(viewModel.getReadDate());
        bindPagesField();
    }

    private void bindPagesField() {
        TextFormatter<Integer> integerFormatter = new TextFormatter<>(change -> {
            String newText = change.getControlNewText();
            if (newText.matches("\\d*")) {
                return change;
            }
            return null;
        });
        pagesField.setTextFormatter(integerFormatter);

        Bindings.bindBidirectional(
                pagesField.textProperty(),
                viewModel.getPages(),
                new javafx.util.converter.NumberStringConverter()
        );
    }
}
