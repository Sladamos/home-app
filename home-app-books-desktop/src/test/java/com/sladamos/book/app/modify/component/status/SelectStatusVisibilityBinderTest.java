package com.sladamos.book.app.modify.component.status;

import com.sladamos.book.model.BookStatus;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.HBox;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.util.WaitForAsyncUtils;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(ApplicationExtension.class)
class SelectStatusVisibilityBinderTest {

    private final SelectStatusVisibilityBinder binder = new SelectStatusVisibilityBinder();

    @Test
    void shouldShowBorrowedByOnlyForBorrowedStatus() {
        ComboBox<BookStatus> comboBox = new ComboBox<>(FXCollections.observableArrayList(BookStatus.values()));
        HBox borrowedByBox = new HBox();
        HBox readDateBox = new HBox();

        Platform.runLater(() -> {
            binder.bind(comboBox, borrowedByBox, readDateBox);
            comboBox.setValue(BookStatus.BORROWED);
        });
        WaitForAsyncUtils.waitForFxEvents();

        assertThat(borrowedByBox.isVisible()).isTrue();
        assertThat(readDateBox.isVisible()).isFalse();
    }

    @Test
    void shouldShowReadDateOnlyForFinishedReadingStatus() {
        ComboBox<BookStatus> comboBox = new ComboBox<>(FXCollections.observableArrayList(BookStatus.values()));
        HBox borrowedByBox = new HBox();
        HBox readDateBox = new HBox();

        Platform.runLater(() -> {
            binder.bind(comboBox, borrowedByBox, readDateBox);
            comboBox.setValue(BookStatus.FINISHED_READING);
        });
        WaitForAsyncUtils.waitForFxEvents();

        assertThat(borrowedByBox.isVisible()).isFalse();
        assertThat(readDateBox.isVisible()).isTrue();
    }
}
