package com.sladamos.book.app.add.validation;

import com.sladamos.book.Book;
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
public class ValidationsOperator {

    private final LabelsDisabler labelsDisabler;

    public Optional<Label> updateValidationLabels(Map<String, ViolationDisplayer> violationsDisplayers, Set<ConstraintViolation<Book>> violations) {
        log.info("Updating validation labels with violations: [size: {}]", violations.size());
        violations.forEach(this.displayViolation(violationsDisplayers));
        return violationsDisplayers.values().stream().map(ViolationDisplayer::label).filter(Node::isVisible).findFirst();
    }

    public void disableValidationLabels(Map<String, ViolationDisplayer> violationDisplayers) {
        log.info("Disabling validation labels");
        violationDisplayers.values().stream().map(ViolationDisplayer::label).forEach(labelsDisabler::disableLabel);
    }

    private Consumer<ConstraintViolation<Book>> displayViolation(Map<String, ViolationDisplayer> violationsDisplayers) {
        return violation -> {
            ViolationDisplayer violationDisplayer = violationsDisplayers.get(violation.getPropertyPath().toString());
            violationDisplayer.displayViolation(violation);
        };
    }
}
