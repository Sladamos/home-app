package com.sladamos.book.app.modify.validation;

import com.sladamos.app.util.message.BindingsCreator;
import com.sladamos.app.util.ui.navigation.FocusableFinder;
import jakarta.validation.ConstraintViolation;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

@Slf4j
@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
@RequiredArgsConstructor
public class FormValidationHandler {

    private final ValidationLabelStateModifier validationLabelStateModifier;
    private final FocusableFinder focusableFinder;
    private final BindingsCreator bindingsCreator;

    private final Map<String, ViolationDisplayer> violationDisplayers = new LinkedHashMap<>();

    public void registerField(String fieldName, Label validationLabel) {
        violationDisplayers.put(fieldName, new NoArgsViolationDisplayer(bindingsCreator, validationLabel));
    }

    public <T> void registerFieldWithLimit(String fieldName, Label validationLabel, T limit) {
        violationDisplayers.put(fieldName, new SingleArgViolationDisplayer<>(bindingsCreator, validationLabel, limit));
    }

    public void disableLabels() {
        validationLabelStateModifier.disableValidationLabels(violationDisplayers);
    }

    public Optional<Node> display(Set<? extends ConstraintViolation<?>> violations) {
        Optional<Label> firstLabel = validationLabelStateModifier.updateValidationLabels(violationDisplayers, violations);

        return firstLabel.flatMap(label -> {
            Pane pane = (Pane) label.getParent();
            return focusableFinder.findFirstFocusableNode(pane);
        });
    }
}