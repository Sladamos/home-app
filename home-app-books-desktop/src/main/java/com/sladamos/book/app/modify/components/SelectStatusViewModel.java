package com.sladamos.book.app.modify.components;

import com.sladamos.book.model.BookStatus;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.StringProperty;

import java.time.LocalDate;

public record SelectStatusViewModel(StringProperty borrowedBy,
                                    ObjectProperty<LocalDate> readDate,
                                    ObjectProperty<BookStatus> status) {
}
