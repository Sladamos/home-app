package com.sladamos.book.app.modify.components;

import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Slf4j
@RequiredArgsConstructor
public class MultipleFieldsController {

    private final static int INITIAL_NUMBER_OF_FIELDS = 1;

    @FXML
    private VBox fieldsContainer;

    private MultipleFieldsViewModel viewModel;

    @FXML
    public void initialize() {
    }

    public void bindTo(MultipleFieldsViewModel viewModel) {
        this.viewModel = viewModel;

        ObservableList<String> fields = viewModel.fields();
        if (fields.isEmpty()) {
            int minimalNumberOfFields = Math.max(viewModel.minimalNumberOfFields(), INITIAL_NUMBER_OF_FIELDS);
            IntStream.range(0, minimalNumberOfFields).forEach(i -> addEmptyField());
        } else {
            fields.forEach(this::addField);
        }
    }

    public void addEmptyField() {
        addField("");
        updateCollectionInViewModel();
    }

    private void addField(String value) {
        TextField field = new TextField(value);
        Button removeBtn = createDeleteButton(field);
        HBox hBox = new HBox(5, field, removeBtn);
        fieldsContainer.getChildren().add(hBox);
        field.textProperty().addListener((obs, oldVal, newVal) -> updateCollectionInViewModel());
        updateVisibilityOfDeleteButtons();
    }

    private Button createDeleteButton(TextField field) {
        Button removeBtn = new Button("-");
        removeBtn.getStyleClass().add("delete--field-button");
        removeBtn.setOnAction(onDeleteButtonClicked(field));
        return removeBtn;
    }


    private EventHandler<ActionEvent> onDeleteButtonClicked(TextField field) {
        return e -> {
            log.info("Delete button clicked for field: {}", field.getText());
            fieldsContainer.getChildren().remove(field.getParent());
            updateCollectionInViewModel();
            updateVisibilityOfDeleteButtons();
        };
    }

    private void updateVisibilityOfDeleteButtons() {
        int minimalNumberOfFields = viewModel.minimalNumberOfFields();
        fieldsContainer.getChildren().stream()
                .map(HBox.class::cast)
                .map(f -> f.getChildren().get(1))
                .forEach(btn -> btn.setVisible(fieldsContainer.getChildren().size() > minimalNumberOfFields));
    }

    private void updateCollectionInViewModel() {
        List<TextField> fields = getFields();
        viewModel.fields().setAll(
                fields.stream()
                        .map(TextField::getText)
                        .toList()
        );
    }

    private List<TextField> getFields() {
        return fieldsContainer.getChildren().stream()
                .map(HBox.class::cast)
                .map(e -> e.getChildren().getFirst())
                .map(TextField.class::cast)
                .collect(Collectors.toList());
    }
}
