package com.sladamos.book.app.add;

import com.sladamos.book.app.items.OnDisplayItemsClicked;
import com.sladamos.book.app.util.BindingsCreator;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Component
public class AddBookController {

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
    private Button selectCoverButton;

    @FXML
    private Button removeCoverButton;

    @FXML
    private ImageView coverPreview;

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

    private final List<TextField> authorFields = new ArrayList<>();
    private final List<TextField> genreFields = new ArrayList<>();

    @FXML
    public void initialize() {
        returnToItemsButton.textProperty().bind(bindingsCreator.createBinding("books.add.returnToBooks"));
        addAuthorButton.textProperty().bind(bindingsCreator.createBinding("books.add.addAuthor"));
        addGenreButton.textProperty().bind(bindingsCreator.createBinding("books.add.addGenre"));
        selectCoverButton.textProperty().bind(bindingsCreator.createBinding("books.add.selectCover"));
        removeCoverButton.textProperty().bind(bindingsCreator.createBinding("books.add.removeCover"));
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

        updateCoverPreview(viewModel.getCoverImage().get());
        viewModel.getCoverImage().addListener((obs, oldVal, newVal) -> updateCoverPreview(newVal));

        initializeCollection(authorsBox, authorFields, viewModel.getAuthors());
        initializeCollection(genresBox, genreFields, viewModel.getGenres());
    }

    private void initializeCollection(VBox fieldsContainer, List<TextField> fields, ObservableList<String> viewModelCollection) {
        fields.clear();
        if (viewModelCollection.isEmpty()) {
            addField("", fieldsContainer, fields, viewModelCollection);
        } else {
            viewModelCollection.forEach(author -> addField(author, fieldsContainer, fields, viewModelCollection));
        }
    }

    private void updateCoverPreview(byte[] imageBytes) {
        if (imageBytes != null && imageBytes.length > 0) {
            coverPreview.setImage(new Image(new ByteArrayInputStream(imageBytes)));
            removeCoverButton.setVisible(true);
        } else {
            coverPreview.setImage(null);
            removeCoverButton.setVisible(false);
        }
    }

    @FXML
    private void onSelectCoverClicked() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Wybierz okładkę");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Obrazy", "*.png", "*.jpg", "*.jpeg", "*.gif")
        );
        File file = fileChooser.showOpenDialog(selectCoverButton.getScene().getWindow());
        if (file != null) {
            try {
                byte[] imageBytes = Files.readAllBytes(file.toPath());
                viewModel.getCoverImage().set(imageBytes);
            } catch (IOException e) {
                log.error("Błąd podczas wczytywania obrazka okładki", e);
            }
        }
    }

    @FXML
    private void onRemoveCoverClicked() {
        log.info("Remove cover button clicked");
        viewModel.getCoverImage().set(null);
    }

    @FXML
    private void onReturnButtonClicked() {
        log.info("Return to items button clicked");
        applicationEventPublisher.publishEvent(new OnDisplayItemsClicked());
    }

    @FXML
    private void onAddBookClicked() {
    }

    @FXML
    private void onAddAuthorClicked() {
        addField("", authorsBox, authorFields, viewModel.getAuthors());
        updateCollectionInViewModel(viewModel.getAuthors(), authorFields);
    }

    @FXML
    private void onAddGenreClicked() {
        addField("", genresBox, genreFields, viewModel.getGenres());
        updateCollectionInViewModel(viewModel.getGenres(), genreFields);
    }

    private void addField(String value, VBox fieldsContainer, List<TextField> fields, ObservableList<String> viewModelCollection) {
        TextField field = new TextField(value);
        Button removeBtn = createDeleteButton(onDeleteButtonClicked(field, fieldsContainer, fields, viewModelCollection));
        HBox hBox = new HBox(5, field, removeBtn);
        fieldsContainer.getChildren().add(hBox);
        fields.add(field);
        field.textProperty().addListener((obs, oldVal, newVal) -> updateCollectionInViewModel(viewModelCollection, fields));
    }

    private Button createDeleteButton(EventHandler<ActionEvent> field) {
        Button removeBtn = new Button("-");
        removeBtn.getStyleClass().add("delete--field-button");
        removeBtn.setOnAction(field);
        return removeBtn;
    }

    private EventHandler<ActionEvent> onDeleteButtonClicked(TextField field, VBox fieldsContainer, List<TextField> fields, ObservableList<String> viewModelCollection) {
        return e -> {
            fieldsContainer.getChildren().remove(field.getParent());
            fields.remove(field);
            updateCollectionInViewModel(viewModelCollection, fields);
        };
    }

    private void updateCollectionInViewModel(ObservableList<String> viewModelCollection, List<TextField> fields) {
        viewModelCollection.setAll(
                fields.stream()
                        .map(TextField::getText)
                        .filter(s -> s != null && !s.isBlank())
                        .toList()
        );
    }
}
