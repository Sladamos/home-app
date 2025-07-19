package com.sladamos.book.app.modify.validation;

import javafx.scene.control.Label;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

@NoArgsConstructor
@Component
public class LabelsDisabler {

    public void disableLabel(Label label) {
        label.textProperty().unbind();
        label.setVisible(false);
        label.setManaged(false);
    }
}