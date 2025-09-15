package com.sladamos.book.app.items;

import com.sladamos.app.util.components.ComponentsGenerator;
import com.sladamos.app.util.messages.BindingsCreator;
import com.sladamos.app.util.messages.TemporaryMessagesFactory;
import com.sladamos.book.Book;
import com.sladamos.book.BookNotFoundException;
import com.sladamos.book.BookService;
import com.sladamos.book.BookValidationException;
import com.sladamos.book.app.add.OnAddBookClicked;
import com.sladamos.book.app.add.OnBookCreated;
import com.sladamos.book.app.edit.OnBookEdited;
import com.sladamos.book.app.util.ListCellFactory;
import jakarta.annotation.PostConstruct;
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
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
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
    private VBox noBooksFound;

    @FXML
    private Label noBooksFoundLabel;

    @FXML
    private Button addBookButton;

    @FXML
    private TextField searchField;

    @FXML
    private ComboBox<BooksItemsSortOption> sortComboBox;

    private final Map<UUID, Node> bookNodes = new HashMap<>();

    private final BookService bookService;

    private final BooksLoader booksLoader;

    private final BooksItemsViewModel viewModel;

    private final BindingsCreator bindingsCreator;

    private final ComponentsGenerator componentsGenerator;

    private final BookItemControllerFactory bookItemControllerFactory;

    private final ApplicationEventPublisher applicationEventPublisher;

    private final TemporaryMessagesFactory temporaryMessagesFactory;

    private final ListCellFactory listCellFactory;

    @PostConstruct
    public void postConstruct() {
        viewModel.getSortedBooks().addListener(this::handleChanges);
    }

    @FXML
    public void initialize() {
        loadBooks();
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

    @EventListener(OnBookDuplicated.class)
    public void onBookDuplicated(OnBookDuplicated event) {
        Book book = event.book();
        log.info("Duplicating book in items: [id: {}, title: {}]", book.getId(), book.getTitle());
        Book bookToDuplicate = prepareBookToDuplicate(book);
        log.info("Trying to duplicate book with new title: [id: {}, title: {}]", bookToDuplicate.getId(), bookToDuplicate.getTitle());
        try {
            bookService.createBook(bookToDuplicate);
            viewModel.addBook(bookToDuplicate);
        } catch (BookValidationException e) {
            log.error("Unable to duplicate book: [reason: {}]", e.getReason());
            temporaryMessagesFactory.showError(bindingsCreator.getMessage("books.items.duplicateBookError"));
        }
    }

    private Book prepareBookToDuplicate(Book book) {
        long booksCount = viewModel.getSortedBooks().stream().filter(e -> e.getIsbn().get().equals(book.getIsbn())).count();
        String baseTitle = book.getTitle().replaceAll(" \\(\\d+\\)$", "");
        String newTitle = String.format("%s (%d)", baseTitle, booksCount);
        LocalDateTime now = LocalDateTime.now();
        return book.toBuilder()
                .id(UUID.randomUUID())
                .title(newTitle)
                .creationDate(now)
                .modificationDate(now)
                .build();
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
        if (viewModel.areBooksNotLoaded()) {
            booksLoader.loadBooks();
        } else {
            log.info("Books already loaded, adding them to items");
            viewModel.getSortedBooks().forEach(this::addItem);
        }
    }

    private void handleChanges(Change<? extends BookItemViewModel> change) {
        while (change.next()) {
            if (change.wasPermutated()) {
                bookNodes.values().forEach(e -> booksContainer.getChildren().remove(e));
                bookNodes.clear();
                viewModel.getSortedBooks().forEach(this::addItem);
            }
            if (change.wasRemoved()) {
                change.getRemoved().forEach(this::removeItem);
            }
            if (change.wasAdded()) {
                change.getAddedSubList().forEach(this::addItem);
            }
        }
    }

    private void removeItem(BookItemViewModel bookItemViewModel) {
        Node component = bookNodes.remove(bookItemViewModel.getBook().getId());
        booksContainer.getChildren().remove(component);
    }

    private void addItem(BookItemViewModel itemVM) {
        Node component = componentsGenerator.addComponentAtBeginning(bookItemControllerFactory.createController(itemVM), booksContainer, getClass().getResource("BooksItem.fxml"));
        bookNodes.put(itemVM.getId().get(), component);
    }
}
