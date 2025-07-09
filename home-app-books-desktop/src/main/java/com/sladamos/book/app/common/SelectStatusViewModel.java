package com.sladamos.book.app.common;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.StringProperty;

import java.time.LocalDate;

public record SelectStatusViewModel(StringProperty borrowedBy, ObjectProperty<LocalDate> readDate) {
}
