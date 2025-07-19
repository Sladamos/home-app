package com.sladamos.book.app.modify.components;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;

public record SelectRatingViewModel(IntegerProperty rating, BooleanProperty favorite) {
}
