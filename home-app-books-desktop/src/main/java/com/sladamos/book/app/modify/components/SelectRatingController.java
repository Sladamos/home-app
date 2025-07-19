package com.sladamos.book.app.modify.components;

import com.sladamos.app.util.messages.BindingsCreator;
import com.sladamos.book.Book;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class SelectRatingController {

    @FXML
    private Slider ratingSlider;

    @FXML
    private CheckBox favoriteCheckBox;

    @FXML
    private Label ratingLabel;

    @FXML
    private Label favoriteLabel;

    private final BindingsCreator bindingsCreator;

    @FXML
    public void initialize() {
        ratingLabel.textProperty().bind(bindingsCreator.createBinding("books.selectRatings.title"));
        favoriteLabel.textProperty().bind(bindingsCreator.createBinding("books.selectRatings.favorite"));
    }

    public void bindTo(SelectRatingViewModel viewModel) {
        favoriteCheckBox.selectedProperty().bindBidirectional(viewModel.favorite());
        ratingSlider.setMin(Book.MIN_RATING);
        ratingSlider.setMax(Book.MAX_RATING);
        ratingSlider.valueProperty().bindBidirectional(viewModel.rating());
    }
}
