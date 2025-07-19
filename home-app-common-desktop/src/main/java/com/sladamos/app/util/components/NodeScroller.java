package com.sladamos.app.util.components;

import javafx.application.Platform;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.control.ScrollPane;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@NoArgsConstructor
public class NodeScroller {

    public void scrollToNode(ScrollPane scrollPane, Node node) {
        Bounds contentBounds = node.localToScene(node.getBoundsInLocal());
        Bounds paneBoundsInLocal = scrollPane.getContent().getBoundsInLocal();
        Bounds scrollBounds = scrollPane.getContent().localToScene(paneBoundsInLocal);
        double y = contentBounds.getMinY() - scrollBounds.getMinY();
        double height = paneBoundsInLocal.getHeight();
        scrollPane.setVvalue(y / height);
    }

    public void scheduleScrollingToNode(ScrollPane scrollPane, Node node) {
        Platform.runLater(() -> scrollToNode(scrollPane, node));
    }
}