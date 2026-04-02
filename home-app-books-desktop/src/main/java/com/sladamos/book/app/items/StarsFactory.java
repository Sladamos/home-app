package com.sladamos.book.app.items;

import com.sladamos.book.app.items.viewmodel.RateableViewModel;
import com.sladamos.book.model.Book;
import javafx.beans.binding.Bindings;
import javafx.css.PseudoClass;
import javafx.scene.control.Label;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@NoArgsConstructor
public class StarsFactory {

    private static final PseudoClass FAVORITE_PSEUDO_CLASS = PseudoClass.getPseudoClass("favorite");
    private static final String RATING_STAR_CLASS = "rating-star";

    public List<Label> createStars(RateableViewModel viewModel) {
        List<Label> stars = new ArrayList<>();
        for (int i = Book.MIN_RATING; i < Book.MAX_RATING; i++) {
            Label star = new Label();
            star.getStyleClass().add(RATING_STAR_CLASS);
            setStarText(star, viewModel, i);
            setStarColor(star, viewModel);
            stars.add(star);
        }
        return stars;
    }

    private void setStarColor(Label star, RateableViewModel viewModel) {
        star.pseudoClassStateChanged(FAVORITE_PSEUDO_CLASS, viewModel.getFavorite().get());
        viewModel.getFavorite().addListener((obs, oldVal, newVal) ->
                star.pseudoClassStateChanged(FAVORITE_PSEUDO_CLASS, newVal)
        );
    }

    private void setStarText(Label star, RateableViewModel viewModel, int starIndex) {
        star.textProperty().bind(Bindings.createStringBinding(() -> {
            int rating = viewModel.getRating().get();
            return starIndex < rating ? "★" : "☆";
        }, viewModel.getRating()));
    }
}
