package com.sladamos.app.util.messages;

import com.sladamos.app.util.FXWinUtil;
import com.sladamos.app.util.IconFactory;
import javafx.animation.PauseTransition;
import javafx.scene.control.Alert;
import javafx.stage.Stage;
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

    private final IconFactory iconFactory;

    private final FXWinUtil fxWinUtil;

    public void showInformation(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        setupAlert(message, alert);
    }

    public void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        setupAlert(message, alert);
    }

    private void setupAlert(String message, Alert alert) {
        alert.setTitle("Alert");
        alert.setHeaderText(null);
        alert.setContentText(message);
        Stage alertStage = (Stage) alert.getDialogPane().getScene().getWindow();
        alertStage.getIcons().add(iconFactory.createIcon());

        alert.getDialogPane().getStylesheets().add(
            Objects.requireNonNull(getClass().getResource("alert-theme.css")).toExternalForm()
        );
        alert.getDialogPane().getStyleClass().add("custom-alert");

        alert.show();
        fxWinUtil.setTitleBarColor(alertStage);

        if (messageDurationSeconds > 0) {
            PauseTransition delay = new PauseTransition(Duration.seconds(this.messageDurationSeconds));
            delay.setOnFinished(event -> alert.close());
            delay.play();
        }
    }
}
