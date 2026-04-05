package com.sladamos.book.app.modify.validation;

import com.sladamos.app.util.component.FocusableFinder;
import com.sladamos.app.util.component.NodeScroller;
import com.sladamos.book.model.Book;
import jakarta.validation.ConstraintViolation;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
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

import static com.sladamos.book.model.Book.MAX_DESCRIPTION_SIZE;

@Slf4j
@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
@RequiredArgsConstructor
public class ModifyBookValidationHandler {

    private final ValidationsOperator validationsOperator;
    private final ViolationDisplayerFactory violationDisplayerFactory;
    private final FocusableFinder focusableFinder;
    private final NodeScroller nodeScroller;

    private final Map<String, ViolationDisplayer> violationDisplayers = new LinkedHashMap<>();

    private ScrollPane formScrollPane;

    public void initialize(ScrollPane formScrollPane,
                           Label titleValidationLabel,
                           Label authorsValidationLabel,
                           Label isbnValidationLabel,
                           Label genresValidationLabel,
                           Label pagesValidationLabel,
                           Label descriptionValidationLabel,
                           Label borrowedByValidationLabel) {
        this.formScrollPane = formScrollPane;
        violationDisplayers.clear();
        violationDisplayers.put("title", violationDisplayerFactory.createNoArgsViolationsDisplayer(titleValidationLabel));
        violationDisplayers.put("authors", violationDisplayerFactory.createNoArgsViolationsDisplayer(authorsValidationLabel));
        violationDisplayers.put("isbn", violationDisplayerFactory.createNoArgsViolationsDisplayer(isbnValidationLabel));
        violationDisplayers.put("genres", violationDisplayerFactory.createNoArgsViolationsDisplayer(genresValidationLabel));
        violationDisplayers.put("pages", violationDisplayerFactory.createNoArgsViolationsDisplayer(pagesValidationLabel));
        violationDisplayers.put("description", violationDisplayerFactory.createSingleArgViolationsDisplayer(MAX_DESCRIPTION_SIZE, descriptionValidationLabel));
        violationDisplayers.put("borrowedBy", violationDisplayerFactory.createNoArgsViolationsDisplayer(borrowedByValidationLabel));
        clear();
    }

    public void clear() {
        validationsOperator.disableValidationLabels(violationDisplayers);
    }

    public void display(Set<ConstraintViolation<Book>> violations) {
        Optional<Label> firstLabel = validationsOperator.updateValidationLabels(violationDisplayers, violations);
        firstLabel.ifPresent(this::focusOnFirstFieldConnectedWithLabel);
    }

    private void focusOnFirstFieldConnectedWithLabel(Label label) {
        Pane pane = (Pane) label.getParent();
        Optional<Node> field = focusableFinder.findFirstFocusableNode(pane);
        field.ifPresent(f -> {
            log.info("Focusing on field: [field: {}]", f);
            f.requestFocus();
            nodeScroller.scrollToNode(formScrollPane, f);
        });
    }
}
