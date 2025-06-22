package com.sladamos.app;

import javafx.fxml.FXML;
import javafx.geometry.Rectangle2D;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.stage.Screen;
import javafx.stage.Stage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
@RequiredArgsConstructor
public class AppTitleBarController {

    @FXML private ImageView iconImageView;
    @FXML private Button closeButton;
    @FXML private Button minimizeButton;
    @FXML private Button maximizeButton;

    private double xOffset = 0;
    private double yOffset = 0;
    private boolean maximized = false;
    private double prevWidth, prevHeight, prevX, prevY;

    @FXML
    public void initialize() {
        iconImageView.setImage(new Image(Objects.requireNonNull(getClass().getResourceAsStream("icon.png"))));

        HBox hbox = (HBox) iconImageView.getParent();
        addDraggingEvents(hbox);

        minimizeButton.setOnAction(e -> getStage().setIconified(true));
        maximizeButton.setOnAction(e -> toggleMaximize());
        closeButton.setOnAction(e -> getStage().close());
    }

    private Stage getStage() {
        return (Stage) iconImageView.getScene().getWindow();
    }

    private Rectangle2D getCurrentScreenBounds(Stage stage) {
        return Screen.getScreensForRectangle(stage.getX(), stage.getY(), stage.getWidth(), stage.getHeight())
                .getFirst()
                .getVisualBounds();
    }


    private void addDraggingEvents(HBox titleBar) {
        titleBar.addEventFilter(MouseEvent.MOUSE_PRESSED, this::saveWindowPosition);

        titleBar.addEventFilter(MouseEvent.MOUSE_DRAGGED, e -> {
            Stage stage = getStage();
            if (!maximized) {
                stage.setX(e.getScreenX() - xOffset);
                stage.setY(e.getScreenY() - yOffset);
            } else {
                Rectangle2D bounds = getCurrentScreenBounds(stage);
                double percent = e.getScreenX() / bounds.getWidth();
                double offset = 15;
                prevX = e.getScreenX() - prevWidth * percent;
                prevY = e.getScreenY() - offset;
                xOffset = e.getScreenX() - prevX;
                yOffset = e.getScreenY() - prevY;
                minimize(stage);
            }
        });

        titleBar.addEventFilter(MouseEvent.MOUSE_CLICKED, e -> {
            if (e.getClickCount() == 2) {
                toggleMaximize();
            }
        });
    }

    private void saveWindowPosition(MouseEvent e) {
        xOffset = e.getSceneX();
        yOffset = e.getSceneY();
    }

    private void toggleMaximize() {
        Stage stage = getStage();
        if (!maximized) {
            maximize(stage);
        } else {
            minimize(stage);
        }
    }

    private void maximize(Stage stage) {
        prevWidth = stage.getWidth();
        prevHeight = stage.getHeight();
        prevX = stage.getX();
        prevY = stage.getY();

        Rectangle2D bounds = getCurrentScreenBounds(stage);
        stage.setX(bounds.getMinX());
        stage.setY(bounds.getMinY());
        stage.setWidth(bounds.getWidth());
        stage.setHeight(bounds.getHeight());
        maximized = true;
        maximizeButton.setText("🗗");
    }

    private void minimize(Stage stage) {
        stage.setX(prevX);
        stage.setY(prevY);
        stage.setWidth(prevWidth);
        stage.setHeight(prevHeight);
        maximized = false;
        maximizeButton.setText("🗖");
    }
}
