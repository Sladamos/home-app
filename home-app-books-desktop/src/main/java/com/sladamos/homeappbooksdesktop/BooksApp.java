package com.sladamos.homeappbooksdesktop;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.io.IOException;

public class BooksApp extends Application {

    private AnnotationConfigApplicationContext context;

    @Override
    public void init() {
        context = new AnnotationConfigApplicationContext(BooksAppConfig.class);
    }

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("BooksModule.fxml"));
        loader.setControllerFactory(context::getBean);
        Scene scene = new Scene(loader.load());
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}