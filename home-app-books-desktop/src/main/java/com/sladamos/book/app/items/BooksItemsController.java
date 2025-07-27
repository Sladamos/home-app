package com.sladamos.book.app.items;

import com.sladamos.app.util.components.ComponentsGenerator;
import com.sladamos.app.util.messages.BindingsCreator;
import com.sladamos.app.util.messages.TemporaryMessagesFactory;
import com.sladamos.book.Book;
import com.sladamos.book.BookNotFoundException;
import com.sladamos.book.BookService;
import com.sladamos.book.app.add.OnAddBookClicked;
import com.sladamos.book.app.add.OnBookCreated;
import com.sladamos.book.app.edit.OnBookEdited;
import jakarta.annotation.PostConstruct;
import javafx.collections.ListChangeListener.Change;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class BooksItemsController {

    @FXML
    private VBox booksContainer;

    @FXML
    private Button addBookButton;

    @FXML
    private TextField searchField;

    private final Map<UUID, Node> bookNodes = new HashMap<>();

    private final BookService bookService;

    private final BooksLoader booksLoader;

    private final BooksItemsViewModel viewModel;

    private final BindingsCreator bindingsCreator;

    private final ComponentsGenerator componentsGenerator;

    private final BookItemControllerFactory bookItemControllerFactory;

    private final ApplicationEventPublisher applicationEventPublisher;

    private final TemporaryMessagesFactory temporaryMessagesFactory;

    @PostConstruct
    public void postConstruct() {
        viewModel.getFilteredBooks().addListener(this::handleChanges);
    }

    @FXML
    public void initialize() {
        loadBooks();
        addBookButton.textProperty().bind(bindingsCreator.createBinding("books.items.addBook"));
        searchField.promptTextProperty().bind(bindingsCreator.createBinding("books.items.searchField"));
        searchField.textProperty().bindBidirectional(viewModel.getSearchQuery());
    }

    @FXML
    private void onAddBookClicked() {
        log.info("Add book button clicked");
        applicationEventPublisher.publishEvent(new OnAddBookClicked());
    }

    @EventListener(OnBookCreated.class)
    @Order(1)
    public void onBookCreated(OnBookCreated event) {
        Book book = event.book();
        log.info("Adding new book to items: [id: {}, title: {}]", book.getId(), book.getTitle());
        viewModel.addBook(book);
        viewModel.getSearchQuery().setValue("");
    }

    @EventListener(OnBookEdited.class)
    @Order(1)
    public void onBookEdited(OnBookEdited event) {
        Book book = event.book();
        log.info("Updating book in items: [id: {}, title: {}]", book.getId(), book.getTitle());
        viewModel.updateBook(book);
    }

    @EventListener(OnBookDeleted.class)
    public void onBookDeleted(OnBookDeleted event) {
        log.info("Deleting book from items: [id: {}, title: {}]", event.bookId(), event.bookTitle());
        try {
            bookService.deleteBook(event.bookId());
            viewModel.deleteBook(event.bookId());
        } catch (BookNotFoundException e) {
            log.error("Unable to delete not existing book: [id: {}, title: {}]", event.bookId(), event.bookTitle());
            temporaryMessagesFactory.showError(bindingsCreator.getMessage("books.items.deleteBookError"));
        }
    }

    private void loadBooks() {
        if (viewModel.getBooks().isEmpty()) {
            booksLoader.loadBooks();
        } else {
            log.info("Books already loaded, adding them to items");
            viewModel.getFilteredBooks().forEach(this::addItem);
        }
    }

    private void handleChanges(Change<? extends BookItemViewModel> change) {
        while (change.next()) {
            if (change.wasRemoved()) {
                change.getRemoved().forEach(this::removeItem);
            }
            if (change.wasAdded()) {
                change.getAddedSubList().forEach(this::addItem);
            }
        }
    }

    private void removeItem(BookItemViewModel bookItemViewModel) {
        var component = bookNodes.remove(bookItemViewModel.getBook().getId());
        booksContainer.getChildren().remove(component);
    }

    private void addItem(BookItemViewModel itemVM) {
        var component = componentsGenerator.addComponentAtBeginning(bookItemControllerFactory.createController(itemVM), booksContainer, getClass().getResource("BooksItem.fxml"));
        bookNodes.put(itemVM.getId().get(), component);
    }
}
