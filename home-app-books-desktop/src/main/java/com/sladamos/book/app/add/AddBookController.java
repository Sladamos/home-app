package com.sladamos.book.app.add;

import com.sladamos.book.app.items.OnDisplayItemsClicked;
import com.sladamos.book.app.util.BindingsCreator;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class AddBookController {

    @FXML
    private Button returnToItemsButton;

    private final ApplicationEventPublisher applicationEventPublisher;

    private final BindingsCreator bindingsCreator;

    @FXML
    public void initialize() {
        returnToItemsButton.textProperty().bind(bindingsCreator.createBinding("books.add.returnToBooks"));

    }

    @FXML
    private void onReturnButtonClicked() {
        applicationEventPublisher.publishEvent(new OnDisplayItemsClicked());
    }


}
