package com.sladamos.book.app.modify.validation;

import com.sladamos.app.util.component.FocusableFinder;
import com.sladamos.app.util.component.NodeScroller;
import com.sladamos.book.model.Book;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ModifyBookValidationHandlerTest {

    @Mock
    private ValidationsOperator validationsOperator;

    @Mock
    private ViolationDisplayerFactory violationDisplayerFactory;

    @Mock
    private FocusableFinder focusableFinder;

    @Mock
    private NodeScroller nodeScroller;

    private ModifyBookValidationHandler handler;

    private ScrollPane formScrollPane;
    private Label titleValidationLabel;
    private Label authorsValidationLabel;
    private Label isbnValidationLabel;
    private Label genresValidationLabel;
    private Label pagesValidationLabel;
    private Label descriptionValidationLabel;
    private Label borrowedByValidationLabel;

    @BeforeEach
    void setUp() {
        handler = new ModifyBookValidationHandler(validationsOperator, violationDisplayerFactory, focusableFinder, nodeScroller);
        formScrollPane = new ScrollPane();
        titleValidationLabel = new Label();
        authorsValidationLabel = new Label();
        isbnValidationLabel = new Label();
        genresValidationLabel = new Label();
        pagesValidationLabel = new Label();
        descriptionValidationLabel = new Label();
        borrowedByValidationLabel = new Label();

        stubDisplayer(titleValidationLabel);
        stubDisplayer(authorsValidationLabel);
        stubDisplayer(isbnValidationLabel);
        stubDisplayer(genresValidationLabel);
        stubDisplayer(pagesValidationLabel);
        stubDisplayer(borrowedByValidationLabel);
        lenient().when(violationDisplayerFactory.createSingleArgViolationsDisplayer(any(), any()))
                .thenAnswer(invocation -> new SingleArgViolationDisplayer<>(null, invocation.getArgument(1), invocation.getArgument(0)));
    }

    @Test
    void shouldConfigureAndClearAllValidationDisplayers() {
        handler.initialize(
                formScrollPane,
                titleValidationLabel,
                authorsValidationLabel,
                isbnValidationLabel,
                genresValidationLabel,
                pagesValidationLabel,
                descriptionValidationLabel,
                borrowedByValidationLabel
        );

        ArgumentCaptor<Map<String, ViolationDisplayer>> captor = ArgumentCaptor.forClass(Map.class);
        verify(validationsOperator).disableValidationLabels(captor.capture());

        assertThat(captor.getValue()).containsKeys("title", "authors", "isbn", "genres", "pages", "description", "borrowedBy");
    }

    @Test
    void shouldFocusAndScrollToFirstInvalidField() {
        TextField invalidField = new TextField();
        Label invalidLabel = new Label();
        VBox wrapper = new VBox(invalidField, invalidLabel);
        ScrollPane scrollPane = new ScrollPane(wrapper);

        stubDisplayer(invalidLabel);

        handler.initialize(
                scrollPane,
                invalidLabel,
                authorsValidationLabel,
                isbnValidationLabel,
                genresValidationLabel,
                pagesValidationLabel,
                descriptionValidationLabel,
                borrowedByValidationLabel
        );

        when(validationsOperator.updateValidationLabels(any(), any()))
                .thenReturn(Optional.of(invalidLabel));
        when(focusableFinder.findFirstFocusableNode(wrapper))
                .thenReturn(Optional.of(invalidField));

        handler.display(Collections.<jakarta.validation.ConstraintViolation<Book>>emptySet());

        verify(nodeScroller).scrollToNode(scrollPane, invalidField);
    }

    private void stubDisplayer(Label label) {
        lenient().when(violationDisplayerFactory.createNoArgsViolationsDisplayer(label))
                .thenReturn(new NoArgsViolationDisplayer(null, label));
    }
}
