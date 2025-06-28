package com.sladamos.app;

import com.sladamos.app.util.FXWinUtil;
import com.sladamos.app.util.IconFactory;
import com.sladamos.book.app.BooksAppConfig;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.io.IOException;

@RequiredArgsConstructor
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
        IconFactory iconFactory = context.getBean(IconFactory.class);
        FXWinUtil fxWinUtil = context.getBean(FXWinUtil.class);
        stage.getIcons().add(iconFactory.createIcon());
        stage.setTitle("Home app");
        stage.setScene(scene);
        stage.show();
        fxWinUtil.setTitleBarColor(stage);
    }

    public static void main(String[] args) {
        launch();
    }
}