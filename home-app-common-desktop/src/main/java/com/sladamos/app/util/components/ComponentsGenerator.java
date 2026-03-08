package com.sladamos.app.util.components;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.layout.Pane;
import org.springframework.stereotype.Component;

import java.net.URL;

@Component
public class ComponentsGenerator {

    public Node addComponentAtBeginning(Object controller, Pane wrapper, URL resource) {
        Node itemRoot = loadAndAttach(controller, wrapper, resource);
        wrapper.getChildren().addFirst(itemRoot);
        return itemRoot;
    }

    public Node addComponentAtEnd(Object controller, Pane wrapper, URL resource) {
        Node itemRoot = loadAndAttach(controller, wrapper, resource);
        wrapper.getChildren().add(itemRoot);
        return itemRoot;
    }

    private Node loadAndAttach(Object controller, Pane wrapper, URL resource) {
        try {
            FXMLLoader loader = new FXMLLoader(resource);
            loader.setControllerFactory(param -> controller);
            return loader.load();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}