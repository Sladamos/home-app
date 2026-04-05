package com.sladamos.book.app.modify.component.status;

import com.sladamos.app.util.message.BindingsCreator;
import com.sladamos.app.util.ui.ListCellFactory;
import com.sladamos.book.app.common.StatusMessageKeyProvider;
import com.sladamos.book.model.BookStatus;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
@RequiredArgsConstructor
public class SelectStatusController {

    @FXML @Getter private Label borrowedByValidationLabel;
    @FXML private Label readDateLabel;
    @FXML private Label borrowedByLabel;
    @FXML private Label selectStatusLabel;
    @FXML private DatePicker readDatePicker;
    @FXML private TextField borrowedByField;
    @FXML private ComboBox<BookStatus> statusComboBox;
    @FXML private HBox borrowedByBox;
    @FXML private HBox readDateBox;

    private final BindingsCreator bindingsCreator;
    private final ListCellFactory listCellFactory;
    private final StatusMessageKeyProvider statusMessageKeyProvider;
    private final FutureDateCellFactory futureDateCellFactory;

    @FXML
    public void initialize() {
        readDateLabel.textProperty().bind(bindingsCreator.createBinding("books.selectStatus.readDate"));
        borrowedByLabel.textProperty().bind(bindingsCreator.createBinding("books.selectStatus.borrowedBy"));
        selectStatusLabel.textProperty().bind(bindingsCreator.createBinding("books.selectStatus.label"));
        readDatePicker.setDayCellFactory(futureDateCellFactory.create());

        initializeStatusComboBox();
        bindVisibility();
    }

    private void initializeStatusComboBox() {
        statusComboBox.setItems(FXCollections.observableArrayList(BookStatus.values()));
        statusComboBox.setCellFactory(cb -> listCellFactory.createListCell(statusMessageKeyProvider::getAddStatusMessageKey));
        statusComboBox.setButtonCell(listCellFactory.createListCell(statusMessageKeyProvider::getAddStatusMessageKey));
    }

    private void bindVisibility() {
        borrowedByBox.visibleProperty().bind(Bindings.createBooleanBinding(
                () -> statusComboBox.getValue() == BookStatus.BORROWED,
                statusComboBox.valueProperty()));
        borrowedByBox.managedProperty().bind(borrowedByBox.visibleProperty());

        readDateBox.visibleProperty().bind(Bindings.createBooleanBinding(
                () -> statusComboBox.getValue() == BookStatus.FINISHED_READING,
                statusComboBox.valueProperty()));
        readDateBox.managedProperty().bind(readDateBox.visibleProperty());
    }

    public void bindTo(SelectStatusViewModel viewModel) {
        borrowedByField.textProperty().bindBidirectional(viewModel.borrowedBy());
        readDatePicker.valueProperty().bindBidirectional(viewModel.readDate());
        statusComboBox.valueProperty().bindBidirectional(viewModel.status());
    }
}