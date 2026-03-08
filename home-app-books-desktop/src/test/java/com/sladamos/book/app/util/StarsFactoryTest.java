package com.sladamos.book.app.util;

import com.sladamos.book.model.Book;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.css.PseudoClass;
import javafx.scene.control.Label;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.testfx.framework.junit5.ApplicationExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith({ApplicationExtension.class, MockitoExtension.class})
class StarsFactoryTest {

    private static final PseudoClass FAVORITE_PSEUDO_CLASS = PseudoClass.getPseudoClass("favorite");

    private StarsFactory starsFactory;

    @Mock
    private RateableViewModel viewModel;

    private IntegerProperty ratingProperty;
    private BooleanProperty favoriteProperty;

    @BeforeEach
    void setUp() {
        starsFactory = new StarsFactory();
        ratingProperty = new SimpleIntegerProperty(0);
        favoriteProperty = new SimpleBooleanProperty(false);

        when(viewModel.getRating()).thenReturn(ratingProperty);
        when(viewModel.getFavorite()).thenReturn(favoriteProperty);
    }

    @Test
    void shouldAssignRatingStarStyleClassToAllStars() {
        List<Label> stars = starsFactory.createStars(viewModel);

        assertThat(stars).allMatch(star -> star.getStyleClass().contains("rating-star"));
    }

    @Test
    void shouldNotHaveFavoritePseudoClassInitially() {
        List<Label> stars = starsFactory.createStars(viewModel);

        assertThat(stars.getFirst().getPseudoClassStates()).doesNotContain(FAVORITE_PSEUDO_CLASS);
    }

    @Test
    void shouldAddFavoritePseudoClassWhenFavoriteIsTrue() {
        List<Label> stars = starsFactory.createStars(viewModel);

        favoriteProperty.set(true);

        assertThat(stars.getFirst().getPseudoClassStates()).contains(FAVORITE_PSEUDO_CLASS);
    }

    @Test
    void shouldRemoveFavoritePseudoClassWhenFavoriteIsChangedToFalse() {
        List<Label> stars = starsFactory.createStars(viewModel);
        favoriteProperty.set(true);

        favoriteProperty.set(false);

        assertThat(stars.getFirst().getPseudoClassStates()).doesNotContain(FAVORITE_PSEUDO_CLASS);
    }

    @Test
    void shouldSetAllStarsToFilledWhenRatingIsMax() {
        List<Label> stars = starsFactory.createStars(viewModel);

        ratingProperty.set(Book.MAX_RATING);

        assertThat(stars).allMatch(star -> star.getText().equals("★"));
    }

    @Test
    void shouldSetAllStarsToEmptyWhenRatingIsBelowMin() {
        List<Label> stars = starsFactory.createStars(viewModel);

        ratingProperty.set(Book.MIN_RATING - 1);

        assertThat(stars).allMatch(star -> star.getText().equals("☆"));
    }
}