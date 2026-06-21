package com.sladamos.app.util.ui;

import com.sladamos.app.util.ui.LabelTextClamp;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;
import org.testfx.util.WaitForAsyncUtils;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(ApplicationExtension.class)
class LabelTextClampTest {

    private final LabelTextClamp clamp = new LabelTextClamp();
    private final SimpleStringProperty text = new SimpleStringProperty();
    private Label label;

    @Start
    private void start(Stage stage) {
        label = new Label();
        label.setWrapText(true);
        label.setMinWidth(220);
        label.setPrefWidth(220);
        label.setMaxWidth(220);
        label.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");
        stage.setScene(new Scene(new StackPane(label), 260, 140));
        stage.show();
    }

    @Test
    void shouldKeepTwoLineTextWithoutEllipsis() {
        Platform.runLater(() -> {
            clamp.bindTwoLineClamp(label, text);
            text.set("Krotki tytul ksiazki");
        });
        WaitForAsyncUtils.waitForFxEvents();

        assertThat(label.getText()).doesNotEndWith("...");
        assertThat(label.getText()).isEqualTo("Krotki tytul ksiazki");
    }

    @Test
    void shouldAddEllipsisForTooLongText() {
        Platform.runLater(() -> {
            clamp.bindTwoLineClamp(label, text);
            text.set("To jest bardzo dlugi tytul ksiazki ktory powinien przekroczyc dwie linie i zostac obciety wielokropkiem na samym koncu drugiej linii");
        });
        WaitForAsyncUtils.waitForFxEvents();

        assertThat(label.getText()).endsWith("...");
    }
}
