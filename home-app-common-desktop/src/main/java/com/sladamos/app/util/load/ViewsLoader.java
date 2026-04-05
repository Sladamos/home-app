package com.sladamos.app.util.load;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.util.Callback;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URL;

@Slf4j
@Component
@RequiredArgsConstructor
public class ViewsLoader {

    private final ApplicationContext context;

    public Node loadView(URL url) {
        return executeLoad(url, context::getBean);
    }

    public Node loadViewWithController(URL url, Object controllerInstance) {
        return executeLoad(url, clazz -> {
            if (clazz.isInstance(controllerInstance)) {
                return controllerInstance;
            }
            return context.getBean(clazz);
        });
    }

    private Node executeLoad(URL url, Callback<Class<?>, Object> controllerFactory) {
        log.info("Loading view from url: [url: {}]", url);
        try {
            FXMLLoader loader = new FXMLLoader(url);
            loader.setControllerFactory(controllerFactory);
            return loader.load();
        } catch (IOException e) {
            throw new RuntimeException("Failed to load FXML: " + url, e);
        }
    }
}
