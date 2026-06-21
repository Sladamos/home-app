package com.sladamos.book.app.items.controller;

import com.sladamos.app.util.message.BindingsCreator;
import com.sladamos.book.app.items.viewmodel.BookItemsViewModel;
import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Region;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
@RequiredArgsConstructor
public class BooksItemsController {

    @FXML
    private ScrollPane booksScrollPane;

    @FXML
    private FlowPane booksFlowPane;

    @FXML
    private Region noBooksFound;

    @FXML
    private Label noBooksFoundLabel;

    private final BookItemsViewModel viewModel;

    private final BindingsCreator bindingsCreator;

    private final ObjectProvider<BooksItemsGridRenderer> gridRendererProvider;

    private BooksItemsGridRenderer gridRenderer;

    @FXML
    public void initialize() {
        gridRenderer = gridRendererProvider.getObject();
        bindUiComponents();
    }

    private void bindUiComponents() {
        noBooksFoundLabel.textProperty().bind(bindingsCreator.createBinding("books.items.noBooksFound"));
        noBooksFound.visibleProperty().bind(Bindings.isEmpty(viewModel.getSortedBooks()));
        noBooksFound.managedProperty().bind(noBooksFound.visibleProperty());
        booksScrollPane.visibleProperty().bind(noBooksFound.visibleProperty().not());
        booksScrollPane.managedProperty().bind(booksScrollPane.visibleProperty());
        booksFlowPane.prefWrapLengthProperty().bind(Bindings.createDoubleBinding(
                () -> Math.max(340.0, booksScrollPane.getViewportBounds().getWidth() - 24.0),
                booksScrollPane.viewportBoundsProperty()
        ));
        booksFlowPane.prefWidthProperty().bind(Bindings.createDoubleBinding(
                () -> Math.max(340.0, booksScrollPane.getViewportBounds().getWidth() - 24.0),
                booksScrollPane.viewportBoundsProperty()
        ));
        gridRenderer.bind(booksFlowPane, viewModel.getSortedBooks());
    }
}
