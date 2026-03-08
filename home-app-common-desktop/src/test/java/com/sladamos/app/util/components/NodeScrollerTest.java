package com.sladamos.app.util.components;

import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;

import java.util.concurrent.CompletableFuture;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(ApplicationExtension.class)
class NodeScrollerTest {

    private ScrollPane scrollPane;
    private Label targetNode;
    private NodeScroller nodeScroller;

    @Start
    private void start(Stage stage) {
        nodeScroller = new NodeScroller();

        Pane content = new Pane();
        content.setMinHeight(2000);

        targetNode = new Label("Target");
        targetNode.setLayoutY(1500);
        content.getChildren().add(targetNode);

        scrollPane = new ScrollPane(content);
        scrollPane.setMinHeight(200);

        stage.setScene(new Scene(scrollPane, 200, 200));
        stage.show();
    }

    @Test
    void shouldScrollToSpecificNode() {
        CompletableFuture.runAsync(() ->
                        nodeScroller.scrollToNode(scrollPane, targetNode),
                javafx.application.Platform::runLater
        ).join();

        assertThat(scrollPane.getVvalue()).isGreaterThan(0.0);
    }
}