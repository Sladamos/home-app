package com.sladamos.app.util.components;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.layout.Pane;
import org.springframework.stereotype.Component;

import java.net.URL;

@Component
public class ComponentsGenerator {

    public Node addComponentAtBeginning(Object controller, Pane wrapper, URL resource) {
        try {
            FXMLLoader loader = new FXMLLoader(resource);
            loader.setControllerFactory(param -> controller);
            Node itemRoot = loader.load();
            wrapper.getChildren().addFirst(itemRoot);
            return itemRoot;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public Node addComponentAtEnd(Object controller, Pane wrapper, URL resource) {
        try {
            FXMLLoader loader = new FXMLLoader(resource);
            loader.setControllerFactory(param -> controller);
            Node itemRoot = loader.load();
            wrapper.getChildren().add(itemRoot);
            return itemRoot;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
