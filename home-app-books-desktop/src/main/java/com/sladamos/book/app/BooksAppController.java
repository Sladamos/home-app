package com.sladamos.book.app;

import com.sladamos.book.app.add.OnAddBookClicked;
import com.sladamos.book.app.add.OnBookCreated;
import com.sladamos.book.app.items.OnDisplayItemsClicked;
import com.sladamos.app.util.BindingsCreator;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class BooksAppController {

    @FXML
    private Label titleLabel;

    @FXML
    private BorderPane rootPanel;

    private final ApplicationContext context;

    private final BindingsCreator bindingsCreator;

    @FXML
    public void initialize() {
        titleLabel.textProperty().bind(bindingsCreator.createBinding("books.title"));
        //setView("items/BooksItems.fxml");
        setView("add/AddBook.fxml");
    }

    @EventListener
    public void onAddBook(OnAddBookClicked event) {
        log.info("Switching to Add Book view");
        setView("add/AddBook.fxml");
    }

    @EventListener
    public void onDisplayItems(OnDisplayItemsClicked event) {
        switchViewToBooksItems();
    }

    @EventListener
    public void onBookCreated(OnBookCreated event) {
        switchViewToBooksItems();
    }

    private void switchViewToBooksItems() {
        log.info("Switching to Display Items view");
        setView("items/BooksItems.fxml");
    }


    private void setView(String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            loader.setControllerFactory(context::getBean);
            Node view = loader.load();
            rootPanel.setCenter(view);
        } catch (IOException e) {
            throw new RuntimeException("Failed to load FXML: " + fxmlPath, e);
        }
    }
}
