package com.sladamos.book.app.items.controller;

import com.sladamos.app.util.load.ViewsLoader;
import com.sladamos.book.app.items.viewmodel.BookItemViewModel;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.layout.FlowPane;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
@RequiredArgsConstructor
public class BooksItemsGridRenderer {

    private static final String BOOK_ITEM_CARD_FXML = "/com/sladamos/book/app/items/BookItemCard.fxml";

    private final Map<UUID, Node> cardsByBookId = new LinkedHashMap<>();
    private final ViewsLoader viewsLoader;
    private final ObjectProvider<BookItemController> controllerProvider;

    private FlowPane container;
    private ObservableList<BookItemViewModel> items;

    public void bind(FlowPane container, ObservableList<BookItemViewModel> items) {
        this.container = container;
        this.items = items;
        this.items.addListener((ListChangeListener<BookItemViewModel>) change -> renderCards());
        renderCards();
    }

    private void renderCards() {
        pruneRemovedCards();
        container.getChildren().setAll(items.stream()
                .map(this::getOrCreateCard)
                .toList());
    }

    private Node getOrCreateCard(BookItemViewModel viewModel) {
        return cardsByBookId.computeIfAbsent(viewModel.getId().get(), key -> createCard(viewModel));
    }

    private Node createCard(BookItemViewModel viewModel) {
        BookItemController controller = controllerProvider.getObject();
        controller.init(viewModel);
        return viewsLoader.loadViewWithController(getClass().getResource(BOOK_ITEM_CARD_FXML), controller);
    }

    private void pruneRemovedCards() {
        Set<UUID> visibleIds = items.stream()
                .map(viewModel -> viewModel.getId().get())
                .collect(Collectors.toSet());
        cardsByBookId.keySet().removeIf(bookId -> !visibleIds.contains(bookId));
    }
}
