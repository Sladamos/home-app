package com.sladamos.app.util.ui;

import javafx.application.Platform;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.Label;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class LabelTextClamp {

    private static final String ELLIPSIS = "...";

    public void bindTwoLineClamp(Label label, ObservableValue<String> source) {
        Runnable updater = () -> updateLabel(label, source.getValue());
        source.addListener((obs, oldValue, newValue) -> updater.run());
        label.widthProperty().addListener((obs, oldValue, newValue) -> updater.run());
        label.fontProperty().addListener((obs, oldValue, newValue) -> updater.run());
        label.sceneProperty().addListener((obs, oldValue, newValue) -> Platform.runLater(updater));
        Platform.runLater(updater);
    }

    private void updateLabel(Label label, String text) {
        double availableWidth = label.getWidth() > 0 ? label.getWidth() : label.prefWidth(-1);
        if (availableWidth <= 0) {
            label.setText(Objects.requireNonNullElse(text, ""));
            return;
        }

        label.setText(clampToTwoLines(text, availableWidth, label.getFont()));
    }

    private String clampToTwoLines(String text, double availableWidth, Font font) {
        String normalizedText = normalize(text);
        if (fitsWithinTwoLines(normalizedText, availableWidth, font)) {
            return normalizedText;
        }

        int low = 0;
        int high = normalizedText.length();
        String bestCandidate = ELLIPSIS;

        while (low <= high) {
            int mid = (low + high) / 2;
            String candidate = buildCandidate(normalizedText, mid);
            if (fitsWithinTwoLines(candidate, availableWidth, font)) {
                bestCandidate = candidate;
                low = mid + 1;
            } else {
                high = mid - 1;
            }
        }

        return bestCandidate;
    }

    private boolean fitsWithinTwoLines(String text, double availableWidth, Font font) {
        Text textNode = new Text(text);
        textNode.setFont(font);
        textNode.setWrappingWidth(availableWidth);

        Text lineMeasure = new Text("Ag");
        lineMeasure.setFont(font);

        double maxHeight = lineMeasure.getLayoutBounds().getHeight() * 2.8;
        return textNode.getLayoutBounds().getHeight() <= maxHeight;
    }

    private String buildCandidate(String text, int length) {
        String candidate = text.substring(0, Math.max(0, Math.min(length, text.length()))).trim();
        return candidate.isEmpty() ? ELLIPSIS : candidate + ELLIPSIS;
    }

    private String normalize(String text) {
        if (text == null || text.isBlank()) {
            return "";
        }
        return text.replace('\n', ' ').replaceAll("\\s+", " ").trim();
    }
}
