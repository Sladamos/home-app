package com.sladamos.app.util.component;

import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class FocusableFinderTest {

    private final FocusableFinder focusableFinder = new FocusableFinder();

    @BeforeAll
    static void initJFX() {
        try {
            Platform.startup(() -> {});
        } catch (IllegalStateException ignored) {
        }
    }

    @Test
    void shouldFindTextFieldAtRootLevel() {
        Pane root = new VBox();
        TextField textField = new TextField();
        root.getChildren().addAll(new Label("Test"), textField, new Button("Click"));

        Optional<Node> result = focusableFinder.findFirstFocusableNode(root);

        assertThat(result).isPresent().contains(textField);
    }

    @Test
    void shouldFindTextAreaNestedDeeply() {
        Pane root = new VBox();
        Pane childPane = new VBox();
        Pane grandChildPane = new VBox();

        TextArea textArea = new TextArea();
        grandChildPane.getChildren().add(textArea);
        childPane.getChildren().addAll(new Label("Nested"), grandChildPane);
        root.getChildren().addAll(new Button("Top"), childPane);

        Optional<Node> result = focusableFinder.findFirstFocusableNode(root);

        assertThat(result).isPresent().contains(textArea);
    }

    @Test
    void shouldIgnoreInvisibleNodes() {
        Pane root = new VBox();
        TextField invisibleField = new TextField();
        invisibleField.setVisible(false);
        TextField visibleField = new TextField();

        root.getChildren().addAll(invisibleField, visibleField);

        Optional<Node> result = focusableFinder.findFirstFocusableNode(root);

        assertThat(result).isPresent().contains(visibleField);
    }

    @Test
    void shouldReturnEmptyWhenNoFocusableNodesExist() {
        Pane root = new VBox();
        root.getChildren().addAll(new Label("Just a label"), new Button("Just a button"));

        Optional<Node> result = focusableFinder.findFirstFocusableNode(root);

        assertThat(result).isEmpty();
    }
}