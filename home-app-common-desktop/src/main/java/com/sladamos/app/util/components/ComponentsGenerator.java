package com.sladamos.app.util.components;

import com.sladamos.app.util.load.ViewsLoader;
import javafx.scene.Node;
import javafx.scene.layout.Pane;
import org.springframework.stereotype.Component;

import java.net.URL;

@Component
public class ComponentsGenerator {

    private final ViewsLoader viewsLoader;

    public ComponentsGenerator(ViewsLoader viewsLoader) {
        this.viewsLoader = viewsLoader;
    }

    public Node addComponentAtEnd(Object controller, Pane wrapper, URL resource) {
        Node itemRoot = viewsLoader.loadView(resource, param -> controller);
        wrapper.getChildren().add(itemRoot);
        return itemRoot;
    }
}