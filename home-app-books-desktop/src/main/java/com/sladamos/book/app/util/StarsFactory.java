package com.sladamos.book.app.util;

import com.sladamos.book.Book;
import com.sladamos.book.app.RateableViewModel;
import javafx.beans.binding.Bindings;
import javafx.scene.control.Label;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@NoArgsConstructor
public class StarsFactory {

    public List<Label> createStars(RateableViewModel viewModel) {
        List<Label> stars = new ArrayList<>();
        for (int i = Book.MIN_RATING; i < Book.MAX_RATING; i++) {
            Label star = new Label();
            setStarText(star, viewModel, i);
            setStarColor(star, viewModel);
            stars.add(star);
        }
        return stars;
    }

    private void setStarColor(Label star, RateableViewModel viewModel) {
        star.styleProperty().bind(Bindings.createStringBinding(() -> {
            String color = viewModel.getFavorite().get() ? "#00DCF4" : "#FFD700";
            return String.format("-fx-font-size: 24; -fx-text-fill: %s;", color);
        }, viewModel.getFavorite()));
    }

    private void setStarText(Label star, RateableViewModel viewModel, int starIndex) {
        star.textProperty().bind(Bindings.createStringBinding(() -> {
            int rating = viewModel.getRating().get();
            return starIndex < rating ? "★" : "☆";
        }, viewModel.getRating()));
    }
}
