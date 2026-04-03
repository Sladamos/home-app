package com.sladamos.book.app.items.controller;

import com.sladamos.app.util.messages.BindingsCreator;
import com.sladamos.book.app.items.event.OnAddBookClicked;
import com.sladamos.book.app.items.BooksItemsSortOption;
import com.sladamos.book.app.items.viewmodel.BooksItemsViewModel;
import com.sladamos.app.util.ui.ListCellFactory;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BooksItemsHeaderActionsController {

    @FXML
    private Button addBookButton;

    @FXML
    private TextField searchField;

    @FXML
    private ComboBox<BooksItemsSortOption> sortComboBox;

    private final BooksItemsViewModel viewModel;
    private final BindingsCreator bindingsCreator;
    private final ApplicationEventPublisher applicationEventPublisher;
    private final ListCellFactory listCellFactory;

    @FXML
    public void initialize() {
        addBookButton.textProperty().bind(bindingsCreator.createBinding("books.items.addBook"));
        searchField.promptTextProperty().bind(bindingsCreator.createBinding("books.items.searchField"));
        searchField.textProperty().bindBidirectional(viewModel.getSearchQuery());
        sortComboBox.setItems(FXCollections.observableArrayList(BooksItemsSortOption.values()));
        sortComboBox.setCellFactory(cb -> listCellFactory.createListCell(BooksItemsSortOption::getTranslationKey));
        sortComboBox.setButtonCell(listCellFactory.createListCell(BooksItemsSortOption::getTranslationKey));
        sortComboBox.valueProperty().bindBidirectional(viewModel.getSortOption());
    }

    @FXML
    private void onAddBookClicked() {
        applicationEventPublisher.publishEvent(new OnAddBookClicked());
    }
}
