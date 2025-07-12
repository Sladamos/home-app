package com.sladamos.book.app.util;

import javafx.scene.Node;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class FocusableFinder {

    public Optional<Node> findFirstFocusableNode(Pane pane) {
        var visibleChildren = pane.getChildren().stream().filter(Node::isVisible).toList();

        for (Node child : visibleChildren) {
            if (child instanceof TextField || child instanceof TextArea) {
                return Optional.of(child);
            } else if (child instanceof Pane childPane) {
                return findFirstFocusableNode(childPane);
            }
        }
        return Optional.empty();
    }
}