package com.sladamos.book.app.items;

import com.sladamos.book.app.items.viewmodel.RateableViewModel;
import com.sladamos.book.model.BookEntity;
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
class StarFactoryTest {

    private static final PseudoClass FAVORITE_PSEUDO_CLASS = PseudoClass.getPseudoClass("favorite");

    private StarFactory starFactory;

    @Mock
    private RateableViewModel viewModel;

    private IntegerProperty ratingProperty;
    private BooleanProperty favoriteProperty;

    @BeforeEach
    void setUp() {
        starFactory = new StarFactory();
        ratingProperty = new SimpleIntegerProperty(0);
        favoriteProperty = new SimpleBooleanProperty(false);

        when(viewModel.getRating()).thenReturn(ratingProperty);
        when(viewModel.getFavorite()).thenReturn(favoriteProperty);
    }

    @Test
    void shouldAssignRatingStarStyleClassToAllStars() {
        List<Label> stars = starFactory.createStars(viewModel);

        assertThat(stars).allMatch(star -> star.getStyleClass().contains("rating-star"));
    }

    @Test
    void shouldNotHaveFavoritePseudoClassInitially() {
        List<Label> stars = starFactory.createStars(viewModel);

        assertThat(stars.getFirst().getPseudoClassStates()).doesNotContain(FAVORITE_PSEUDO_CLASS);
    }

    @Test
    void shouldAddFavoritePseudoClassWhenFavoriteIsTrue() {
        List<Label> stars = starFactory.createStars(viewModel);

        favoriteProperty.set(true);

        assertThat(stars.getFirst().getPseudoClassStates()).contains(FAVORITE_PSEUDO_CLASS);
    }

    @Test
    void shouldRemoveFavoritePseudoClassWhenFavoriteIsChangedToFalse() {
        List<Label> stars = starFactory.createStars(viewModel);
        favoriteProperty.set(true);

        favoriteProperty.set(false);

        assertThat(stars.getFirst().getPseudoClassStates()).doesNotContain(FAVORITE_PSEUDO_CLASS);
    }

    @Test
    void shouldSetAllStarsToFilledWhenRatingIsMax() {
        List<Label> stars = starFactory.createStars(viewModel);

        ratingProperty.set(BookEntity.MAX_RATING);

        assertThat(stars).allMatch(star -> star.getText().equals("★"));
    }

    @Test
    void shouldSetAllStarsToEmptyWhenRatingIsBelowMin() {
        List<Label> stars = starFactory.createStars(viewModel);

        ratingProperty.set(BookEntity.MIN_RATING - 1);

        assertThat(stars).allMatch(star -> star.getText().equals("☆"));
    }
}
