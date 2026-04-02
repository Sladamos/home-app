package com.sladamos.book.app;

import com.sladamos.app.util.load.ViewsLoader;
import com.sladamos.app.util.messages.BindingsCreator;
import com.sladamos.book.app.add.OnAddBookClicked;
import com.sladamos.book.app.add.OnBookCreated;
import com.sladamos.book.app.edit.EditBookControllerFactory;
import com.sladamos.book.app.edit.OnBookEdited;
import com.sladamos.book.app.edit.OnEditBookClicked;
import com.sladamos.book.app.items.event.OnDisplayItemsClicked;
import com.sladamos.book.app.modify.ModifyBookController;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.util.Callback;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.util.Objects;
import java.util.function.Function;

@Slf4j
@Component
@RequiredArgsConstructor
public class BooksAppController {

    private static final String MODIFY_BOOK_FXML = "/com/sladamos/book/app/modify/ModifyBook.fxml";
    private static final String BOOK_ITEMS_FXML = "/com/sladamos/book/app/items/BooksItems.fxml";

    @FXML
    private Label titleLabel;

    @FXML
    private BorderPane rootPanel;

    private final ApplicationContext context;
    private final BindingsCreator bindingsCreator;
    private final EditBookControllerFactory editBookControllerFactory;
    private final ViewsLoader viewsLoader;

    @FXML
    public void initialize() {
        titleLabel.textProperty().bind(bindingsCreator.createBinding("books.title"));
        onDisplayBooks();
    }

    @EventListener(OnAddBookClicked.class)
    public void onAddBook() {
        log.info("Switching to Add Book view");
        loadView(MODIFY_BOOK_FXML, viewsLoader::loadView);
    }

    @EventListener(OnEditBookClicked.class)
    public void onEditBook(OnEditBookClicked event) {
        log.info("Switching to Edit Book view");
        Function<URL, Node> nodeLoader = url -> viewsLoader.loadView(url, determineEditBookController(event));
        loadView(MODIFY_BOOK_FXML, nodeLoader);
    }

    @EventListener({OnDisplayItemsClicked.class, OnBookCreated.class, OnBookEdited.class})
    public void onDisplayBooks() {
        log.info("Switching to Display Items view");
        loadView(BOOK_ITEMS_FXML, viewsLoader::loadView);
    }

    private Callback<Class<?>, Object> determineEditBookController(OnEditBookClicked event) {
        return clazz -> {
            if (Objects.equals(clazz, ModifyBookController.class)) {
                return editBookControllerFactory.createEditController(event.book());
            }
            return context.getBean(clazz);
        };
    }

    private void loadView(String fxmlPath, Function<URL, Node> nodeLoader) {
        URL url = getClass().getResource(fxmlPath);
        Node view = nodeLoader.apply(url);
        rootPanel.setCenter(view);
    }
}