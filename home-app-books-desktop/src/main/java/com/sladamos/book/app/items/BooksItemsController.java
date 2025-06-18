package com.sladamos.book.app.items;

import javafx.collections.ListChangeListener.Change;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.layout.VBox;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BooksItemsController {

    @FXML
    private VBox booksContainer;

    private final BooksItemsViewModel viewModel;

    @FXML
    public void initialize() {
        viewModel.getBooks().addListener(this::handleChanges);
        viewModel.loadBooks();
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
            loader.setControllerFactory(param -> new BookItemController(itemVM));
            Node itemRoot = loader.load();
            booksContainer.getChildren().add(itemRoot);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
