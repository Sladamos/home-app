package com.sladamos.app.util.messages;

import com.sladamos.app.util.FXWinUtil;
import com.sladamos.app.util.IconFactory;
import javafx.animation.PauseTransition;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import javafx.util.Duration;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Objects;

import static javafx.scene.control.ButtonBar.ButtonData;

@Component
@RequiredArgsConstructor
public class TemporaryMessagesFactory {

    public static final int GRAPHIC_SCALE = 2;

    @Value("${temporary.message.duration:3}")
    private double messageDurationSeconds;

    private final IconFactory iconFactory;

    private final FXWinUtil fxWinUtil;

    private final BindingsCreator bindingsCreator;

    public Alert showConfirmation(String message, Image image) {
        ImageView graphic = prepareImageToContent(image);
        Alert alert = setupAlert(message, "alert.confirmation.title", AlertType.CONFIRMATION);
        alert.getButtonTypes().setAll(
                new ButtonType(bindingsCreator.getMessage("alert.confirmation.yes"), ButtonData.OK_DONE),
                new ButtonType(bindingsCreator.getMessage("alert.confirmation.no"), ButtonData.CANCEL_CLOSE)
        );
        alert.setGraphic(graphic);
        showAlert(alert);
        return alert;
    }

    public void showError(String message) {
        Alert alert = setupAlert(message, "alert.default.title", AlertType.ERROR);
        setAutoCloseIfNecessary(alert);
        showAlert(alert);
    }

    private Alert setupAlert(String message, String titleKey, AlertType alerttype) {
        Alert alert = new Alert(alerttype, message);
        String title = bindingsCreator.getMessage(titleKey);
        alert.setTitle(title);
        alert.setHeaderText(null);
        modifyAlertLook(alert);
        return alert;
    }

    private ImageView prepareImageToContent(Image image) {
        ImageView graphic = new ImageView(image);
        graphic.setPreserveRatio(true);
        graphic.setFitHeight(image.getHeight() / GRAPHIC_SCALE);
        graphic.setFitWidth(image.getWidth() / GRAPHIC_SCALE);
        return graphic;
    }

    private void setAutoCloseIfNecessary(Alert alert) {
        if (messageDurationSeconds > 0) {
            PauseTransition delay = new PauseTransition(Duration.seconds(this.messageDurationSeconds));
            delay.setOnFinished(event -> alert.close());
            delay.play();
        }
    }

    private void modifyAlertLook(Alert alert) {
        Stage alertStage = (Stage) alert.getDialogPane().getScene().getWindow();
        alertStage.getIcons().add(iconFactory.createIcon());

        alert.getDialogPane().getStylesheets().add(
                Objects.requireNonNull(getClass().getResource("alert-theme.css")).toExternalForm()
        );
        alert.getDialogPane().getStyleClass().add("custom-alert");
    }

    private void showAlert(Alert alert) {
        Stage alertStage = (Stage) alert.getDialogPane().getScene().getWindow();
        alert.show();
        fxWinUtil.setTitleBarColor(alertStage);
    }
}
