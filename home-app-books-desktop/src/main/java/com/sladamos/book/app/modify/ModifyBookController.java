package com.sladamos.book.app.modify;

import com.sladamos.app.util.messages.BindingsCreator;
import com.sladamos.app.util.components.ComponentsGenerator;
import com.sladamos.book.Book;
import com.sladamos.book.BookService;
import com.sladamos.book.BookValidationException;
import com.sladamos.book.app.add.OnBookCreated;
import com.sladamos.book.app.modify.components.*;
import com.sladamos.book.app.modify.validation.ValidationsOperator;
import com.sladamos.book.app.modify.validation.ViolationDisplayer;
import com.sladamos.book.app.modify.validation.ViolationDisplayerFactory;
import com.sladamos.book.app.edit.OnBookEdited;
import com.sladamos.book.app.items.OnDisplayItemsClicked;
import com.sladamos.app.util.components.FocusableFinder;
import com.sladamos.app.util.components.NodeScroller;
import jakarta.validation.ConstraintViolation;
import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.scene.Node;
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
import java.util.Optional;
import java.util.Set;

import static com.sladamos.book.Book.*;

@Slf4j
@Component
@RequiredArgsConstructor
public class ModifyBookController {

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

    private final ModifyBookViewModel viewModel;

    private final BookService bookService;

    private final FocusableFinder focusableFinder;

    private final NodeScroller nodeScroller;

    private final ValidationsOperator validationsOperator;

    private final ViolationDisplayerFactory violationDisplayerFactory;

    private final ModifyBookViewModelConverter viewModelConverter;

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
        Book book = viewModelConverter.convert(viewModel);
        if (viewModel.isEditMode()) {
            updateBook(book);
        } else {
            saveBook(book);
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

        if (viewModel.isEditMode()) {
            addBookLabel.setText(bindingsCreator.getMessage("books.edit.name"));
            addBookButton.setText(bindingsCreator.getMessage("books.edit.name"));
        } else {
            addBookLabel.setText(bindingsCreator.getMessage("books.add.name"));
            addBookButton.setText(bindingsCreator.getMessage("books.add.name"));
        }

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

    private void saveBook(Book book) {
        try {
            bookService.createBook(book);
            viewModel.reset();
            applicationEventPublisher.publishEvent(new OnBookCreated(book));
        } catch (BookValidationException e) {
            log.error("Unable to create book: [reason: {}]", e.getReason());
            updateValidationLabels(e.getViolations());
        }
    }

    private void updateBook(Book book) {
        try {
            bookService.updateBook(book);
            applicationEventPublisher.publishEvent(new OnBookEdited(book));
        } catch (BookValidationException e) {
            log.error("Unable to edit book: [reason: {}]", e.getReason());
            updateValidationLabels(e.getViolations());
        }
    }

    private void updateValidationLabels(Set<ConstraintViolation<Book>> violations) {
        Optional<Label> firstLabel = validationsOperator.updateValidationLabels(violationDisplayers, violations);
        firstLabel.ifPresent(this::focusOnFirstFieldConnectedWithLabel);
    }

    private void focusOnFirstFieldConnectedWithLabel(Label label) {
        Pane pane = (Pane) label.getParent();
        Optional<Node> field = focusableFinder.findFirstFocusableNode(pane);
        field.ifPresent(f -> {
            log.info("Focusing on field: [field: {}]", f);
            f.requestFocus();
            nodeScroller.scrollToNode(formScrollPane, f);
        });
    }
}
