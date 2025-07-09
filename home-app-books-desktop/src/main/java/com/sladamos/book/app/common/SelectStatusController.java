package com.sladamos.book.app.common;

import com.sladamos.app.util.BindingsCreator;
import javafx.fxml.FXML;
import javafx.scene.control.DateCell;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
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
    private Label borrowedToLabel;

    @FXML
    private DatePicker readDatePicker;

    @FXML
    private TextField borrowedByField;

    private final BindingsCreator bindingsCreator;

    @FXML
    public void initialize() {
        readDateLabel.textProperty().bind(bindingsCreator.createBinding("books.selectStatus.readDate"));
        borrowedToLabel.textProperty().bind(bindingsCreator.createBinding("books.selectStatus.borrowedTo"));

        readDatePicker.setDayCellFactory(createCellFactory());
    }

    public void bindTo(SelectStatusViewModel viewModel) {
        borrowedByField.textProperty().bindBidirectional(viewModel.borrowedBy());
        readDatePicker.valueProperty().bindBidirectional(viewModel.readDate());
        if (viewModel.readDate().get() == null) {
            readDatePicker.setValue(LocalDate.now());
        }
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