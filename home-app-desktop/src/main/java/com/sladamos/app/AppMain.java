package com.sladamos.app;

import com.sladamos.app.util.FXWinUtil;
import com.sladamos.book.app.BooksAppConfig;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.io.IOException;
import java.util.Objects;

public class AppMain extends Application {

    private AnnotationConfigApplicationContext context;

    @Override
    public void init() {
        context = new AnnotationConfigApplicationContext(BooksAppConfig.class);
    }

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("HomeApp.fxml"));
        loader.setControllerFactory(context::getBean);
        Scene scene = new Scene(loader.load());
        stage.getIcons().add(new Image(Objects.requireNonNull(getClass().getResourceAsStream("icon.ico"))));
        stage.setTitle("Home app");
        stage.setScene(scene);
        stage.show();
        FXWinUtil.setTitleBarColor(stage, 0x3A007A);
    }

    public static void main(String[] args) {
        launch();
    }
}