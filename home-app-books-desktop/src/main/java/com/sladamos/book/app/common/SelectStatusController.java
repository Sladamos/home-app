package com.sladamos.book.app.common;

import com.sladamos.app.util.BindingsCreator;
import com.sladamos.book.BookStatus;
import com.sladamos.book.app.util.StatusMessageKeyProvider;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.util.Callback;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@RequiredArgsConstructor
@Component
public class SelectStatusController {

    @FXML
    private Label readDateLabel;

    @FXML
    private Label borrowedByLabel;

    @FXML
    private Label selectStatusLabel;

    @FXML
    private DatePicker readDatePicker;

    @FXML
    private TextField borrowedByField;

    @FXML
    private ComboBox<BookStatus> statusComboBox;

    @FXML
    private HBox borrowedByBox;

    @FXML
    private HBox readDateBox;

    private final BindingsCreator bindingsCreator;

    private final StatusMessageKeyProvider statusMessageKeyProvider;

    @FXML
    public void initialize() {
        readDateLabel.textProperty().bind(bindingsCreator.createBinding("books.selectStatus.readDate"));
        borrowedByLabel.textProperty().bind(bindingsCreator.createBinding("books.selectStatus.borrowedBy"));
        selectStatusLabel.textProperty().bind(bindingsCreator.createBinding("books.selectStatus.label"));

        readDatePicker.setDayCellFactory(createCellFactory());

        createStatusComboBox();
    }

    private void createStatusComboBox() {
        statusComboBox.setItems(FXCollections.observableArrayList(BookStatus.values()));
        statusComboBox.setCellFactory(cb -> createListCell());
        statusComboBox.setButtonCell(createListCell());

        borrowedByBox.visibleProperty().bind(Bindings.createBooleanBinding(
                () -> statusComboBox.getValue() == BookStatus.BORROWED,
                statusComboBox.valueProperty()));
        borrowedByBox.managedProperty().bind(borrowedByBox.visibleProperty());

        readDateBox.visibleProperty().bind(Bindings.createBooleanBinding(
                () -> statusComboBox.getValue() == BookStatus.FINISHED_READING,
                statusComboBox.valueProperty()));
        readDateBox.managedProperty().bind(readDateBox.visibleProperty());
    }

    private ListCell<BookStatus> createListCell() {
        return new ListCell<>() {
            @Override
            protected void updateItem(BookStatus item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText("");
                } else {
                    var key = statusMessageKeyProvider.getAddStatusMessageKey(item);
                    var message = bindingsCreator.getMessage(key);
                    setText(message);
                }
            }
        };
    }

    public void bindTo(SelectStatusViewModel viewModel) {
        borrowedByField.textProperty().bindBidirectional(viewModel.borrowedBy());
        readDatePicker.valueProperty().bindBidirectional(viewModel.readDate());
        statusComboBox.valueProperty().bindBidirectional(viewModel.status());
    }

    private Callback<DatePicker, DateCell> createCellFactory() {
        return new Callback<>() {
            @Override
            public DateCell call(final DatePicker datePicker) {
                return new DateCell() {
                    @Override
                    public void updateItem(LocalDate item, boolean empty) {
                        super.updateItem(item, empty);
                        setDisable(item != null && item.isAfter(LocalDate.now()));
                    }
                };
            }
        };
    }
}
