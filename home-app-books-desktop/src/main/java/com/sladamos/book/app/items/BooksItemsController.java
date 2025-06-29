package com.sladamos.book.app.items;

import com.sladamos.app.util.BindingsCreator;
import com.sladamos.app.util.ComponentsGenerator;
import com.sladamos.book.app.add.OnAddBookClicked;
import javafx.collections.ListChangeListener.Change;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class BooksItemsController {

    @FXML
    private VBox booksContainer;

    @FXML
    private Button addBookButton;

    private final BooksItemsViewModel viewModel;

    private final BindingsCreator bindingsCreator;

    private final ComponentsGenerator componentsGenerator;

    private final BookItemControllerFactory bookItemControllerFactory;

    private final ApplicationEventPublisher applicationEventPublisher;

    @FXML
    public void initialize() {
        viewModel.getBooks().addListener(this::handleChanges);
        loadBooks();

        addBookButton.textProperty().bind(bindingsCreator.createBinding("books.items.addBook"));
    }

    private void loadBooks() {
        if (viewModel.getBooks().isEmpty()) {
            viewModel.loadBooks();
        } else {
            viewModel.getBooks().forEach(this::addItem);
        }
    }

    @FXML
    private void onAddBookClicked() {
        log.info("Add book button clicked");
        applicationEventPublisher.publishEvent(new OnAddBookClicked());
    }

    private void handleChanges(Change<? extends BookItemViewModel> change) {
        while (change.next()) {
            if (change.wasAdded()) {
                change.getAddedSubList().forEach(this::addItem);
            }
        }
    }

    private void addItem(BookItemViewModel itemVM) {
        componentsGenerator.addComponent(bookItemControllerFactory.createController(itemVM), booksContainer, getClass().getResource("BooksItem.fxml"));
    }
}
