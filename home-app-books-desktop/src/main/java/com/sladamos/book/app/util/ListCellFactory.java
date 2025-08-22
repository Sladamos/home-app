package com.sladamos.book.app.util;

import com.sladamos.app.util.messages.BindingsCreator;
import javafx.scene.control.ListCell;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.function.Function;

@Component
@RequiredArgsConstructor
public class ListCellFactory {

    private final BindingsCreator bindingsCreator;

    public <T> ListCell<T> createListCell(Function<T, String> keySupplier) {
        return new ListCell<>() {
            @Override
            protected void updateItem(T item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText("");
                } else {
                    String key = keySupplier.apply(item);
                    String message = bindingsCreator.getMessage(key);
                    setText(message);
                }
            }
        };
    }
}
