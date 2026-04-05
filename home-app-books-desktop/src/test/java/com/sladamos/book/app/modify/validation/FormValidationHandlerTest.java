package com.sladamos.book.app.modify.validation;

import com.sladamos.app.util.message.BindingsCreator;
import com.sladamos.app.util.ui.navigation.FocusableFinder;
import javafx.scene.Node;
import javafx.scene.control.Label;
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
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FormValidationHandlerTest {

    @Mock
    private ValidationLabelStateModifier validationLabelStateModifier;

    @Mock
    private FocusableFinder focusableFinder;

    @Mock
    private BindingsCreator bindingsCreator;

    private FormValidationHandler handler;

    @BeforeEach
    void setUp() {
        handler = new FormValidationHandler(validationLabelStateModifier, focusableFinder, bindingsCreator);
    }

    @Test
    void shouldDisableAllRegisteredLabels() {
        Label titleLabel = new Label();
        Label descLabel = new Label();

        handler.registerField("title", titleLabel);
        handler.registerFieldWithLimit("description", descLabel, 300);
        handler.disableLabels();

        ArgumentCaptor<Map<String, ViolationDisplayer>> captor = ArgumentCaptor.forClass(Map.class);
        verify(validationLabelStateModifier).disableValidationLabels(captor.capture());

        assertThat(captor.getValue()).containsKeys("title", "description");
    }

    @Test
    void shouldReturnFirstInvalidFocusableNode() {
        TextField invalidField = new TextField();
        Label invalidLabel = new Label();
        VBox wrapper = new VBox(invalidField, invalidLabel);

        when(validationLabelStateModifier.updateValidationLabels(any(), any()))
                .thenReturn(Optional.of(invalidLabel));
        when(focusableFinder.findFirstFocusableNode(wrapper))
                .thenReturn(Optional.of(invalidField));

        handler.registerField("testField", invalidLabel);
        Optional<Node> result = handler.display(Collections.emptySet());

        assertThat(result).isPresent().contains(invalidField);
    }
}