package com.sladamos.book.app;

import com.sladamos.app.util.FXWinUtil;
import com.sladamos.app.util.IconFactory;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.core.env.PropertiesPropertySource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.support.PropertiesLoaderUtils;

import java.io.IOException;
import java.util.Arrays;
import java.util.Properties;

@Slf4j
public class BooksApp extends Application {

    private AnnotationConfigApplicationContext context;

    @Override
    public void init() {
        context = new AnnotationConfigApplicationContext();
        String[] activeProfiles = context.getEnvironment().getActiveProfiles();
        if (activeProfiles.length == 0) {
            loadProfile("local");
        } else  {
            Arrays.stream(activeProfiles).forEach(this::loadProfile);
        }

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

    private void loadProfile(String profile) {
        try {
            ClassPathResource resource = new ClassPathResource("application-" + profile + ".properties");
            if (resource.exists()) {
                Properties props = PropertiesLoaderUtils.loadProperties(resource);
                context.getEnvironment().getPropertySources()
                        .addLast(new PropertiesPropertySource("config-" + profile, props));
                log.info("Loaded profile : {}", profile);
            } else {
                log.error("No properties found for profile: {}", profile);
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to load properties for profile: " + profile, e);
        }
    }
}