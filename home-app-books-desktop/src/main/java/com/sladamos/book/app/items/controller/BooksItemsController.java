package com.sladamos.book.app.items.controller;

import com.sladamos.app.util.load.ViewsLoader;
import com.sladamos.app.util.messages.BindingsCreator;
import com.sladamos.book.app.add.OnAddBookClicked;
import com.sladamos.book.app.items.BookItemNodesManager;
import com.sladamos.book.app.items.BooksItemsSortOption;
import com.sladamos.book.app.items.viewmodel.BookItemViewModel;
import com.sladamos.book.app.items.viewmodel.BooksItemsViewModel;
import com.sladamos.book.app.util.ListCellFactory;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener.Change;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class BooksItemsController {

    private static final String BOOK_ITEM_FXML = "/com/sladamos/book/app/items/BooksItem.fxml";

    @FXML
    private VBox booksContainer;

    @FXML
    private VBox noBooksFound;

    @FXML
    private Label noBooksFoundLabel;

    @FXML
    private Button addBookButton;

    @FXML
    private TextField searchField;

    @FXML
    private ComboBox<BooksItemsSortOption> sortComboBox;

    private final BooksItemsViewModel viewModel;

    private final BindingsCreator bindingsCreator;

    private final ViewsLoader viewsLoader;

    private final ObjectProvider<BookItemController> controllerProvider;

    private final ApplicationEventPublisher applicationEventPublisher;

    private final ListCellFactory listCellFactory;

    private BookItemNodesManager nodesManager;

    @FXML
    public void initialize() {
        nodesManager = new BookItemNodesManager(booksContainer);
        viewModel.getBooks().addListener(this::handleBooksListChanges);
        viewModel.getSortedBooks().addListener(this::handleVisibleBooksChanges);
        loadBooks();
        bindUiComponents();
    }

    @FXML
    private void onAddBookClicked() {
        log.info("Add book button clicked");
        applicationEventPublisher.publishEvent(new OnAddBookClicked());
    }

    private void bindUiComponents() {
        addBookButton.textProperty().bind(bindingsCreator.createBinding("books.items.addBook"));
        searchField.promptTextProperty().bind(bindingsCreator.createBinding("books.items.searchField"));
        noBooksFoundLabel.textProperty().bind(bindingsCreator.createBinding("books.items.noBooksFound"));
        searchField.textProperty().bindBidirectional(viewModel.getSearchQuery());
        noBooksFound.visibleProperty().bind(Bindings.isEmpty(viewModel.getSortedBooks()));
        noBooksFound.managedProperty().bind(noBooksFound.visibleProperty());
        sortComboBox.setItems(FXCollections.observableArrayList(BooksItemsSortOption.values()));
        sortComboBox.setCellFactory(cb -> listCellFactory.createListCell(BooksItemsSortOption::getTranslationKey));
        sortComboBox.setButtonCell(listCellFactory.createListCell(BooksItemsSortOption::getTranslationKey));
        sortComboBox.valueProperty().bindBidirectional(viewModel.getSortOption());
    }

    private void loadBooks() {
        if (viewModel.areBooksNotLoaded()) {
            viewModel.loadBooks();
        } else {
            log.info("Books already loaded, registering existing nodes");
            viewModel.getBooks().forEach(this::registerNode);
            refreshVisibleBooks();
        }
    }

    private void handleBooksListChanges(Change<? extends BookItemViewModel> change) {
        while (change.next()) {
            if (change.wasRemoved()) {
                change.getRemoved().forEach(vm -> nodesManager.unregister(vm.getId().get()));
            }
        }
    }

    private void handleVisibleBooksChanges(Change<? extends BookItemViewModel> change) {
        while (change.next()) {
            if (change.wasAdded()) {
                change.getAddedSubList().forEach(this::registerNode);
            }
        }
        refreshVisibleBooks();
    }

    private void refreshVisibleBooks() {
        List<UUID> visibleIds = viewModel.getSortedBooks().stream().map(vm -> vm.getId().get()).toList();
        nodesManager.showAll(visibleIds);
    }

    private void registerNode(BookItemViewModel itemVM) {
        if (nodesManager.contains(itemVM.getId().get())) {
            return;
        }
        BookItemController controller = controllerProvider.getObject();
        controller.init(itemVM);
        Node component = viewsLoader.loadView(getClass().getResource(BOOK_ITEM_FXML), param -> controller);
        nodesManager.register(itemVM.getId().get(), component);
    }
}
