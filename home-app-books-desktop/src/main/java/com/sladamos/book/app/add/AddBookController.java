package com.sladamos.book.app.add;

import com.sladamos.book.app.items.OnDisplayItemsClicked;
import com.sladamos.app.util.BindingsCreator;
import com.sladamos.book.app.common.SelectCoverController;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Component
public class AddBookController {

    public static final int MIN_NUMBER_OF_FIELDS = 1;

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
    private VBox authorsBox;

    @FXML
    private Button addAuthorButton;

    @FXML
    private VBox genresBox;

    @FXML
    private Button addGenreButton;

    private final ApplicationEventPublisher applicationEventPublisher;

    private final BindingsCreator bindingsCreator;

    private final AddBookViewModel viewModel;

    private final SelectCoverController selectCoverController;

    @FXML
    public void initialize() {
        setupBindings();

        selectCoverController.bindTo(viewModel);

        initializeCollection(authorsBox, viewModel.getAuthors());
        initializeCollection(genresBox, viewModel.getGenres());
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
        addField("", authorsBox, viewModel.getAuthors());
    }

    @FXML
    private void onAddGenreClicked() {
        log.info("Add genre button clicked");
        addField("", genresBox, viewModel.getGenres());
    }

    private void addField(String value, VBox fieldsContainer, ObservableList<String> viewModelCollection) {
        TextField field = new TextField(value);
        Button removeBtn = createDeleteButton(onDeleteButtonClicked(field, fieldsContainer, viewModelCollection));
        HBox hBox = new HBox(5, field, removeBtn);
        fieldsContainer.getChildren().add(hBox);
        field.textProperty().addListener((obs, oldVal, newVal) -> updateCollectionInViewModel(viewModelCollection, fieldsContainer));
        updateVisibilityOfDeleteButtons(fieldsContainer);
    }

    private Button createDeleteButton(EventHandler<ActionEvent> field) {
        Button removeBtn = new Button("-");
        removeBtn.getStyleClass().add("delete--field-button");
        removeBtn.setOnAction(field);
        return removeBtn;
    }

    private EventHandler<ActionEvent> onDeleteButtonClicked(TextField field, VBox fieldsContainer,  ObservableList<String> viewModelCollection) {
        return e -> {
            log.info("Delete button clicked for field: {}", field.getText());
            fieldsContainer.getChildren().remove(field.getParent());
            updateCollectionInViewModel(viewModelCollection, fieldsContainer);
            updateVisibilityOfDeleteButtons(fieldsContainer);
        };
    }

    private void updateVisibilityOfDeleteButtons(VBox fieldsContainer) {
        fieldsContainer.getChildren().stream()
                .map(HBox.class::cast)
                .map(f -> f.getChildren().get(1))
                .forEach(btn -> btn.setVisible(fieldsContainer.getChildren().size() > MIN_NUMBER_OF_FIELDS));
    }

    private void updateCollectionInViewModel(ObservableList<String> viewModelCollection, VBox fieldsContainer) {
        List<TextField> fields = getFields(fieldsContainer);
        viewModelCollection.setAll(
                fields.stream()
                        .map(TextField::getText)
                        .filter(s -> s != null && !s.isBlank())
                        .toList()
        );
    }

    private List<TextField> getFields(VBox fieldsContainer) {
        return fieldsContainer.getChildren().stream()
                .map(HBox.class::cast)
                .map(e -> e.getChildren().getFirst())
                .map(TextField.class::cast)
                .collect(Collectors.toList());
    }

    private void setupBindings() {
        returnToItemsButton.textProperty().bind(bindingsCreator.createBinding("books.add.returnToBooks"));
        addAuthorButton.textProperty().bind(bindingsCreator.createBinding("books.add.addAuthor"));
        addGenreButton.textProperty().bind(bindingsCreator.createBinding("books.add.addGenre"));
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

    private void initializeCollection(VBox fieldsContainer, ObservableList<String> viewModelCollection) {
        if (viewModelCollection.isEmpty()) {
            addField("", fieldsContainer, viewModelCollection);
        } else {
            viewModelCollection.forEach(author -> addField(author, fieldsContainer, viewModelCollection));
        }
    }
}
