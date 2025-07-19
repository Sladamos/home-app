package com.sladamos.book.app;

import com.sladamos.app.util.messages.BindingsCreator;
import com.sladamos.book.app.add.OnAddBookClicked;
import com.sladamos.book.app.add.OnBookCreated;
import com.sladamos.book.app.edit.EditBookControllerFactory;
import com.sladamos.book.app.edit.OnBookEdited;
import com.sladamos.book.app.edit.OnEditBookClicked;
import com.sladamos.book.app.items.BooksItemsController;
import com.sladamos.book.app.items.OnDisplayItemsClicked;
import com.sladamos.book.app.modify.ModifyBookController;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.util.Callback;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URL;

@Slf4j
@Component
@RequiredArgsConstructor
public class BooksAppController {

    private static final URL MODIFY_BOOK_COMPONENT_RESOURCE = ModifyBookController.class.getResource("ModifyBook.fxml");
    private static final URL BOOK_ITEMS_COMPONENT_RESOURCE = BooksItemsController.class.getResource("BooksItems.fxml");

    @FXML
    private Label titleLabel;

    @FXML
    private BorderPane rootPanel;

    private final ApplicationContext context;

    private final BindingsCreator bindingsCreator;

    private final EditBookControllerFactory editBookControllerFactory;

    @FXML
    public void initialize() {
        titleLabel.textProperty().bind(bindingsCreator.createBinding("books.title"));
        switchViewToBooksItems();
    }

    @EventListener(OnAddBookClicked.class)
    public void onAddBook() {
        log.info("Switching to Add Book view");
        setView(MODIFY_BOOK_COMPONENT_RESOURCE);
    }

    @EventListener(OnEditBookClicked.class)
    public void onEditBook(OnEditBookClicked event) {
        log.info("Switching to Edit Book view");
        setView(MODIFY_BOOK_COMPONENT_RESOURCE,  clazz -> {
            if (clazz == ModifyBookController.class) {
                return editBookControllerFactory.createEditController(event.book());
            }
            return context.getBean(clazz);
        });
    }

    @EventListener(OnDisplayItemsClicked.class)
    public void onDisplayItems() {
        switchViewToBooksItems();
    }

    @EventListener(OnBookCreated.class)
    public void onBookCreated() {
        switchViewToBooksItems();
    }

    @EventListener(OnBookEdited.class)
    public void onBookEdited() {
        switchViewToBooksItems();
    }

    private void switchViewToBooksItems() {
        log.info("Switching to Display Items view");
        setView(BOOK_ITEMS_COMPONENT_RESOURCE);
    }

    private void setView(URL fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(fxmlPath);
            loader.setControllerFactory(context::getBean);
            Node view = loader.load();
            rootPanel.setCenter(view);
        } catch (IOException e) {
            throw new RuntimeException("Failed to load FXML: " + fxmlPath, e);
        }
    }

    private void setView(URL fxmlPath, Callback<Class<?>, Object> controllerFactory) {
        try {
            FXMLLoader loader = new FXMLLoader(fxmlPath);
            loader.setControllerFactory(controllerFactory);
            Node view = loader.load();
            rootPanel.setCenter(view);
        } catch (IOException e) {
            throw new RuntimeException("Failed to load FXML: " + fxmlPath, e);
        }
    }
}
