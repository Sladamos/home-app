package com.sladamos.book.app.items.controller;

import com.sladamos.app.util.messages.BindingsCreator;
import com.sladamos.book.app.items.event.OnAddBookClicked;
import com.sladamos.book.app.items.BookItemsSortOption;
import com.sladamos.book.app.items.viewmodel.BookItemsActiveState;
import com.sladamos.book.app.items.viewmodel.BookItemsViewModel;
import com.sladamos.app.util.ui.ListCellFactory;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
@RequiredArgsConstructor
public class BooksItemsHeaderActionsController {

    @FXML
    private Button addBookButton;

    @FXML
    private TextField searchField;

    @FXML
    private ComboBox<BookItemsSortOption> sortComboBox;

    private final BookItemsActiveState activeState;
    private final BindingsCreator bindingsCreator;
    private final ApplicationEventPublisher applicationEventPublisher;
    private final ListCellFactory listCellFactory;

    @FXML
    public void initialize() {
        BookItemsViewModel viewModel = activeState.getActive();
        addBookButton.textProperty().bind(bindingsCreator.createBinding("books.items.addBook"));
        searchField.promptTextProperty().bind(bindingsCreator.createBinding("books.items.searchField"));
        searchField.textProperty().bindBidirectional(viewModel.getSearchQuery());
        sortComboBox.setItems(FXCollections.observableArrayList(BookItemsSortOption.values()));
        sortComboBox.setCellFactory(cb -> listCellFactory.createListCell(BookItemsSortOption::getTranslationKey));
        sortComboBox.setButtonCell(listCellFactory.createListCell(BookItemsSortOption::getTranslationKey));
        sortComboBox.valueProperty().bindBidirectional(viewModel.getSortOption());
    }

    @FXML
    private void onAddBookClicked() {
        applicationEventPublisher.publishEvent(new OnAddBookClicked());
    }
}
