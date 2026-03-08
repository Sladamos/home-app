package com.sladamos.app.util.components;

import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.CompletableFuture;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(ApplicationExtension.class)
class ComponentsGeneratorTest {

    private ComponentsGenerator componentsGenerator;
    private Pane wrapper;
    private Path tempFxmlPath;
    private URL fxmlUrl;

    @Start
    private void start(Stage stage) {
        componentsGenerator = new ComponentsGenerator();
        wrapper = new VBox();
        wrapper.getChildren().add(new Label("Existing Child"));
    }

    @BeforeEach
    void setUp() throws IOException {
        tempFxmlPath = Files.createTempFile("testComponent", ".fxml");
        String fxmlContent = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><javafx.scene.control.Label xmlns=\"http://javafx.com/javafx\" text=\"InjectedNode\"/>";
        Files.writeString(tempFxmlPath, fxmlContent);
        fxmlUrl = tempFxmlPath.toUri().toURL();
    }

    @AfterEach
    void tearDown() throws IOException {
        Files.deleteIfExists(tempFxmlPath);
    }

    @Test
    void shouldAddComponentAtBeginning() {
        Object dummyController = new Object();

        Node result = CompletableFuture.supplyAsync(() ->
                        componentsGenerator.addComponentAtBeginning(dummyController, wrapper, fxmlUrl),
                Platform::runLater
        ).join();

        assertThat(result).isInstanceOf(Label.class);
        assertThat(((Label) result).getText()).isEqualTo("InjectedNode");
        assertThat(wrapper.getChildren()).hasSize(2);
        assertThat(wrapper.getChildren().getFirst()).isEqualTo(result);
    }

    @Test
    void shouldAddComponentAtEnd() {
        Object dummyController = new Object();

        Node result = CompletableFuture.supplyAsync(() ->
                        componentsGenerator.addComponentAtEnd(dummyController, wrapper, fxmlUrl),
                Platform::runLater
        ).join();

        assertThat(result).isInstanceOf(Label.class);
        assertThat(((Label) result).getText()).isEqualTo("InjectedNode");
        assertThat(wrapper.getChildren()).hasSize(2);
        assertThat(wrapper.getChildren().getLast()).isEqualTo(result);
    }
}