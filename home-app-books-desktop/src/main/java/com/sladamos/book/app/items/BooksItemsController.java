package com.sladamos.book.app.items;

import com.sladamos.book.app.LocaleProvider;
import com.sladamos.book.app.BindingsCreator;
import javafx.collections.ListChangeListener.Change;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BooksItemsController {

    @FXML
    private VBox booksContainer;
    @FXML
    private Label titleLabel;
    @FXML
    private Button addBookButton;

    private final BooksItemsViewModel viewModel;

    private final LocaleProvider localeProvider;

    private final BindingsCreator bindingsCreator;

    @FXML
    public void initialize() {
        viewModel.getBooks().addListener(this::handleChanges);
        viewModel.loadBooks();

        titleLabel.textProperty().bind(bindingsCreator.createBinding("books.title"));
        addBookButton.textProperty().bind(bindingsCreator.createBinding("books.addBook"));
    }

    private void handleChanges(Change<? extends BookItemViewModel> change) {
        while (change.next()) {
            if (change.wasAdded()) {
                change.getAddedSubList().forEach(this::addItem);
            }
        }
    }

    private void addItem(BookItemViewModel itemVM) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("BooksItem.fxml"));
            loader.setControllerFactory(param -> new BookItemController(itemVM, localeProvider, bindingsCreator));
            Node itemRoot = loader.load();
            booksContainer.getChildren().add(itemRoot);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
