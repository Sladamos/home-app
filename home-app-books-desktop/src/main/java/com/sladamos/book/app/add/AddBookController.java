package com.sladamos.book.app.add;

import com.sladamos.app.util.BindingsCreator;
import com.sladamos.app.util.ComponentsGenerator;
import com.sladamos.book.Book;
import com.sladamos.book.BookService;
import com.sladamos.book.BookValidationException;
import com.sladamos.book.app.add.validation.ValidationsOperator;
import com.sladamos.book.app.add.validation.ViolationDisplayer;
import com.sladamos.book.app.add.validation.ViolationDisplayerFactory;
import com.sladamos.book.app.common.*;
import com.sladamos.book.app.items.OnDisplayItemsClicked;
import com.sladamos.book.app.util.FocusableFinder;
import com.sladamos.book.app.util.NodeScroller;
import jakarta.validation.ConstraintViolation;
import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import static com.sladamos.book.Book.*;

@Slf4j
@Component
@RequiredArgsConstructor
public class AddBookController {

    private static final URL MULTIPLE_FIELDS_COMPONENT_RESOURCE = MultipleFieldsController.class.getResource("MultipleFields.fxml");

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
    private Label addBookLabel;

    @FXML
    private HBox genresWrapper;

    @FXML
    private Pane genresPanel;

    @FXML
    private Pane authorsPanel;

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
    private TextField publisherField;

    @FXML
    private TextField pagesField;

    @FXML
    private Button addAuthorButton;

    @FXML
    private Button addGenreButton;

    private final SelectCoverController selectCoverController;

    private final SelectRatingController selectRatingController;

    private final SelectStatusController selectStatusController;

    private final ApplicationEventPublisher applicationEventPublisher;

    private final BindingsCreator bindingsCreator;

    private final ComponentsGenerator componentsGenerator;

    private final AddBookViewModel viewModel;

    private final BookService bookService;

    private final FocusableFinder focusableFinder;

    private final NodeScroller nodeScroller;

    private final ValidationsOperator validationsOperator;

    private final ViolationDisplayerFactory violationDisplayerFactory;

    private final MultipleFieldsController authorsMultipleFieldsController = new MultipleFieldsController();

    private final MultipleFieldsController genresMultipleFieldsController = new MultipleFieldsController();

    private final Map<String, ViolationDisplayer> violationDisplayers = new LinkedHashMap<>();

    @FXML
    public void initialize() {
        componentsGenerator.addComponentAtEnd(genresMultipleFieldsController, genresPanel, MULTIPLE_FIELDS_COMPONENT_RESOURCE);
        componentsGenerator.addComponentAtEnd(authorsMultipleFieldsController, authorsPanel, MULTIPLE_FIELDS_COMPONENT_RESOURCE);
        setupBindings();
        setupViolationDisplayersMap();
        validationsOperator.disableValidationLabels(violationDisplayers);
    }

    @FXML
    private void onReturnButtonClicked() {
        log.info("Return to items button clicked");
        applicationEventPublisher.publishEvent(new OnDisplayItemsClicked());
    }

    @FXML
    private void onAddBookClicked() {
        log.info("Add book button clicked");
        validationsOperator.disableValidationLabels(violationDisplayers);
        Book book = viewModel.toBook();
        try {
            bookService.createBook(book);
            viewModel.reset();
            applicationEventPublisher.publishEvent(new OnBookCreated(book));
        } catch (BookValidationException e) {
            updateValidationLabels(e.getViolations());
            log.error("Unable to create book: [reason: {}]", e.getReason());
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

    private void setupViolationDisplayersMap() {
        violationDisplayers.clear();
        violationDisplayers.put("title", violationDisplayerFactory.createNoArgsViolationsDisplayer(titleValidationLabel));
        violationDisplayers.put("authors", violationDisplayerFactory.createNoArgsViolationsDisplayer(authorsValidationLabel));
        violationDisplayers.put("isbn", violationDisplayerFactory.createNoArgsViolationsDisplayer(isbnValidationLabel));
        violationDisplayers.put("genres", violationDisplayerFactory.createNoArgsViolationsDisplayer(genresValidationLabel));
        violationDisplayers.put("pages", violationDisplayerFactory.createNoArgsViolationsDisplayer(pagesValidationLabel));
        violationDisplayers.put("description", violationDisplayerFactory.createSingleArgViolationsDisplayer(MAX_DESCRIPTION_SIZE, descriptionValidationLabel));
        violationDisplayers.put("borrowedBy", violationDisplayerFactory.createNoArgsViolationsDisplayer(selectStatusController.borrowedByValidationLabel));
    }

    private void setupBindings() {
        selectCoverController.bindTo(viewModel);
        selectRatingController.bindTo(new SelectRatingViewModel(viewModel.getRating(), viewModel.getFavorite()));
        selectStatusController.bindTo(new SelectStatusViewModel(viewModel.getBorrowedBy(), viewModel.getReadDate(), viewModel.getStatus()));
        authorsMultipleFieldsController.bindTo(new MultipleFieldsViewModel(viewModel.getAuthors(), Book.MIN_NUMBER_OF_AUTHORS));
        genresMultipleFieldsController.bindTo(new MultipleFieldsViewModel(viewModel.getGenres(), MIN_NUMBER_OF_GENRES));
        genresWrapper.visibleProperty().bind(Bindings.isEmpty(viewModel.getGenres()).not());
        genresWrapper.managedProperty().bind(genresWrapper.visibleProperty());

        addBookLabel.textProperty().bind(bindingsCreator.createBinding("books.add.name"));
        addBookButton.textProperty().bind(bindingsCreator.createBinding("books.add.name"));
        returnToItemsButton.textProperty().bind(bindingsCreator.createBinding("books.add.returnToBooks"));
        addAuthorButton.textProperty().bind(bindingsCreator.createBinding("books.add.addAuthor"));
        addGenreButton.textProperty().bind(bindingsCreator.createBinding("books.add.addGenre"));

        titleLabel.textProperty().bind(bindingsCreator.createBinding("books.add.title"));
        authorsLabel.textProperty().bind(bindingsCreator.createBinding("books.add.authors"));
        publisherLabel.textProperty().bind(bindingsCreator.createBinding("books.add.publisher"));
        genresLabel.textProperty().bind(bindingsCreator.createBinding("books.add.genres"));
        pagesLabel.textProperty().bind(bindingsCreator.createBinding("books.add.pages"));
        descriptionLabel.textProperty().bind(bindingsCreator.createBinding("books.add.description"));

        titleField.textProperty().bindBidirectional(viewModel.getTitle());
        isbnField.textProperty().bindBidirectional(viewModel.getIsbn());
        descriptionArea.textProperty().bindBidirectional(viewModel.getDescription());
        publisherField.textProperty().bindBidirectional(viewModel.getPublisher());
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

    private void updateValidationLabels(Set<ConstraintViolation<Book>> violations) {
        var firstLabel = validationsOperator.updateValidationLabels(violationDisplayers, violations);
        firstLabel.ifPresent(this::focusOnFirstFieldConnectedWithLabel);
    }

    private void focusOnFirstFieldConnectedWithLabel(Label label) {
        var pane = (Pane) label.getParent();
        var field = focusableFinder.findFirstFocusableNode(pane);
        field.ifPresent(f -> {
            log.info("Focusing on field: [field: {}]", f);
            f.requestFocus();
            nodeScroller.scrollToNode(formScrollPane, f);
        });
    }
}
