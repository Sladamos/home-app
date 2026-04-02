package com.sladamos.book.app.items.viewmodel;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;

public interface RateableViewModel {
    IntegerProperty getRating();
    BooleanProperty getFavorite();
}
