package com.sladamos.book.app.modify.component.status;

import javafx.scene.control.DateCell;
import javafx.scene.control.DatePicker;
import javafx.util.Callback;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class FutureDateCellFactory {

    public Callback<DatePicker, DateCell> create() {
        return new Callback<>() {
            @Override
            public DateCell call(DatePicker datePicker) {
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
