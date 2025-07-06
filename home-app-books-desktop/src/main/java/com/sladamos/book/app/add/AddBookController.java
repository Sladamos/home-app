package com.sladamos.book.app.add;

import com.sladamos.app.util.BindingsCreator;
import com.sladamos.app.util.ComponentsGenerator;
import com.sladamos.book.Book;
import com.sladamos.book.app.common.MultipleFieldsController;
import com.sladamos.book.app.common.MultipleFieldsControllerFactory;
import com.sladamos.book.app.common.MultipleFieldsViewModel;
import com.sladamos.book.app.common.SelectCoverController;
import com.sladamos.book.app.items.OnDisplayItemsClicked;
import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
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
    private Spinner<Integer> pagesSpinner;

    @FXML
    private HBox ratingBox;

    @FXML
    private CheckBox ratingEnabledCheckBox;

    @FXML
    private Slider ratingSlider;

    @FXML
    private Label ratingValueLabel;

    @FXML
    private CheckBox favoriteCheckBox;

    @FXML
    private Button addAuthorButton;

    @FXML
    private Button addGenreButton;

    private final SelectCoverController selectCoverController;

    private final ApplicationEventPublisher applicationEventPublisher;

    private final BindingsCreator bindingsCreator;

    private final ComponentsGenerator componentsGenerator;

    private final AddBookViewModel viewModel;

    private final MultipleFieldsController authorsMultipleFieldsController;

    private final MultipleFieldsController genresMultipleFieldsController;

    public AddBookController(MultipleFieldsControllerFactory multipleFieldsControllerFactory,
                             SelectCoverController selectCoverController,
                             ApplicationEventPublisher applicationEventPublisher,
                             BindingsCreator bindingsCreator,
                             ComponentsGenerator componentsGenerator,
                             AddBookViewModel viewModel) {
        this.selectCoverController = selectCoverController;
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
        favoriteCheckBox.selectedProperty().bindBidirectional(viewModel.getFavorite());
        pagesSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 10000, 0));
        pagesSpinner.getValueFactory().valueProperty().bindBidirectional(viewModel.getPages().asObject());

        ratingSlider.setDisable(viewModel.getRating().get() == null);
        if (viewModel.getRating().get() != null) {
            ratingSlider.setValue(viewModel.getRating().get());
            ratingEnabledCheckBox.setSelected(true);
            ratingValueLabel.setText(String.valueOf(viewModel.getRating().get()));
        } else {
            ratingSlider.setValue(1);
            ratingEnabledCheckBox.setSelected(false);
            ratingValueLabel.setText("brak");
        }

        ratingEnabledCheckBox.selectedProperty().addListener((obs, oldVal, newVal) -> {
            ratingSlider.setDisable(!newVal);
            if (newVal) {
                int val = (viewModel.getRating().get() != null) ? viewModel.getRating().get() : 1;
                ratingSlider.setValue(val);
                viewModel.getRating().set(val);
                ratingValueLabel.setText(String.valueOf(val));
            } else {
                viewModel.getRating().set(null);
                ratingValueLabel.setText("brak");
            }
        });

        ratingSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (ratingEnabledCheckBox.isSelected()) {
                int val = newVal.intValue();
                viewModel.getRating().set(val);
                ratingValueLabel.setText(String.valueOf(val));
            }
        });

        viewModel.getRating().addListener((obs, oldVal, newVal) -> {
            if (newVal == null) {
                ratingValueLabel.setText("brak");
            } else {
                ratingValueLabel.setText(String.valueOf(newVal));
            }
        });
    }
}
