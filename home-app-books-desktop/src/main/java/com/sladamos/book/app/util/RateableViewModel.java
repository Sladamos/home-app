package com.sladamos.book.app.util;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;

public interface RateableViewModel {
    IntegerProperty getRating();
    BooleanProperty getFavorite();
}
