package com.sladamos.book.app.modify.component.status;

import com.sladamos.book.model.BookStatus;
import javafx.beans.binding.Bindings;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.HBox;
import org.springframework.stereotype.Component;

@Component
public class SelectStatusVisibilityBinder {

    public void bind(ComboBox<BookStatus> statusComboBox, HBox borrowedByBox, HBox readDateBox) {
        borrowedByBox.visibleProperty().bind(Bindings.createBooleanBinding(
                () -> statusComboBox.getValue() == BookStatus.BORROWED,
                statusComboBox.valueProperty()));
        borrowedByBox.managedProperty().bind(borrowedByBox.visibleProperty());

        readDateBox.visibleProperty().bind(Bindings.createBooleanBinding(
                () -> statusComboBox.getValue() == BookStatus.FINISHED_READING,
                statusComboBox.valueProperty()));
        readDateBox.managedProperty().bind(readDateBox.visibleProperty());
    }
}
