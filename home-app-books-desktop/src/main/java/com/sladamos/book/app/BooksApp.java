package com.sladamos.book.app;

import com.sladamos.app.util.FXWinUtil;
import com.sladamos.app.util.IconFactory;
import com.sladamos.app.util.ProfilesLoader;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.io.IOException;

@Slf4j
public class BooksApp extends Application {

    private AnnotationConfigApplicationContext context;

    @Override
    public void init() {
        context = new AnnotationConfigApplicationContext();
        ProfilesLoader profilesLoader = new ProfilesLoader();
        profilesLoader.loadProfiles(context.getEnvironment());
        context.register(BooksAppConfig.class);
        context.refresh();
    }

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("BooksApp.fxml"));
        IconFactory iconFactory = context.getBean(IconFactory.class);
        FXWinUtil fxWinUtil = context.getBean(FXWinUtil.class);
        loader.setControllerFactory(context::getBean);

        Scene scene = new Scene(loader.load());
        stage.getIcons().add(iconFactory.createIcon());
        stage.setTitle("Bookcase application");
        stage.setScene(scene);
        stage.show();
        fxWinUtil.setTitleBarColor(stage);
    }

    public static void main(String[] args) {
        launch();
    }
}