package com.sladamos.book.app.util;

import javafx.animation.PauseTransition;
import javafx.scene.control.Alert;
import javafx.util.Duration;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
@RequiredArgsConstructor
public class TemporaryMessagesFactory {

    @Value("${temporary.message.duration:3}")
    private double messageDurationSeconds;

    public void showInformation(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        setupAlert(message, alert);
    }

    public void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        setupAlert(message, alert);
    }

    private void setupAlert(String message, Alert alert) {
        alert.setTitle("");
        alert.setHeaderText(null);
        alert.setContentText(message);

        alert.getDialogPane().getStylesheets().add(
            Objects.requireNonNull(getClass().getResource("alert-theme.css")).toExternalForm()
        );
        alert.getDialogPane().getStyleClass().add("custom-alert");

        alert.show();

        if (messageDurationSeconds > 0) {
            PauseTransition delay = new PauseTransition(Duration.seconds(this.messageDurationSeconds));
            delay.setOnFinished(event -> alert.close());
            delay.play();
        }
    }
}
