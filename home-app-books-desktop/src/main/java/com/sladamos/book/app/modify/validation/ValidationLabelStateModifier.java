package com.sladamos.book.app.modify.validation;

import jakarta.validation.ConstraintViolation;
import javafx.scene.Node;
import javafx.scene.control.Label;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;

@Slf4j
@Component
@RequiredArgsConstructor
public class ValidationLabelStateModifier {

    public Optional<Label> updateValidationLabels(Map<String, ViolationDisplayer> violationsDisplayers, Set<? extends ConstraintViolation<?>> violations) {
        log.info("Updating validation labels with violations: [size: {}]", violations.size());
        violations.forEach(this.displayViolation(violationsDisplayers));
        return violationsDisplayers.values().stream().map(ViolationDisplayer::label).filter(Node::isVisible).findFirst();
    }

    public void disableValidationLabels(Map<String, ViolationDisplayer> violationDisplayers) {
        log.info("Disabling validation labels");
        violationDisplayers.values().stream().map(ViolationDisplayer::label).forEach(this::disableLabel);
    }

    private Consumer<ConstraintViolation<?>> displayViolation(Map<String, ViolationDisplayer> violationsDisplayers) {
        return violation -> {
            ViolationDisplayer violationDisplayer = violationsDisplayers.get(violation.getPropertyPath().toString());
            violationDisplayer.displayViolation(violation);
        };
    }

    private void disableLabel(Label label) {
        label.textProperty().unbind();
        label.setVisible(false);
        label.setManaged(false);
    }
}
