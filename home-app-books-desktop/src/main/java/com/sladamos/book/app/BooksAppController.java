package com.sladamos.book.app;

import com.sladamos.app.util.load.ViewsLoader;
import com.sladamos.app.util.messages.BindingsCreator;
import com.sladamos.book.app.items.event.OnAddBookClicked;
import com.sladamos.book.app.modify.event.OnBookCreated;
import com.sladamos.book.app.modify.event.OnBookEdited;
import com.sladamos.book.app.items.event.OnEditBookClicked;
import com.sladamos.book.app.items.event.OnDisplayItemsClicked;
import com.sladamos.book.app.modify.ModifyBookControllerFactory;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.util.function.Function;

@Slf4j
@Component
@RequiredArgsConstructor
public class BooksAppController {

    private static final String MODIFY_BOOK_FXML = "/com/sladamos/book/app/modify/screen/ModifyBook.fxml";
    private static final String BOOK_ITEMS_FXML = "/com/sladamos/book/app/items/BooksItems.fxml";
    private static final String BOOK_ITEMS_HEADER_ACTIONS_FXML = "/com/sladamos/book/app/items/BooksItemsHeaderActions.fxml";

    @FXML
    private Label titleLabel;

    @FXML
    private BorderPane rootPanel;

    @FXML
    private StackPane headerActionsContainer;

    private final BindingsCreator bindingsCreator;
    private final ModifyBookControllerFactory modifyBookControllerFactory;
    private final ViewsLoader viewsLoader;

    @FXML
    public void initialize() {
        titleLabel.textProperty().bind(bindingsCreator.createBinding("books.title"));
        onDisplayBooks();
    }

    @EventListener(OnAddBookClicked.class)
    public void onAddBook() {
        log.info("Switching to Add Book view");
        clearHeaderActions();
        loadView(MODIFY_BOOK_FXML, url -> viewsLoader.loadView(url, modifyBookControllerFactory.forAdd()));
    }

    @EventListener(OnEditBookClicked.class)
    public void onEditBook(OnEditBookClicked event) {
        log.info("Switching to Edit Book view");
        clearHeaderActions();
        loadView(MODIFY_BOOK_FXML, url -> viewsLoader.loadView(url, modifyBookControllerFactory.forEdit(event.book())));
    }

    @EventListener({OnDisplayItemsClicked.class, OnBookCreated.class, OnBookEdited.class})
    public void onDisplayBooks() {
        log.info("Switching to Display Items view");
        showHeaderActions(BOOK_ITEMS_HEADER_ACTIONS_FXML);
        loadView(BOOK_ITEMS_FXML, viewsLoader::loadView);
    }

    private void loadView(String fxmlPath, Function<URL, Node> nodeLoader) {
        URL url = getClass().getResource(fxmlPath);
        Node view = nodeLoader.apply(url);
        rootPanel.setCenter(view);
    }

    private void showHeaderActions(String fxmlPath) {
        URL url = getClass().getResource(fxmlPath);
        Node actionsView = viewsLoader.loadView(url);
        headerActionsContainer.getChildren().setAll(actionsView);
    }

    private void clearHeaderActions() {
        headerActionsContainer.getChildren().clear();
    }
}
