package com.sladamos.book.app.common;

import javafx.collections.ObservableList;

public record MultipleFieldsViewModel(ObservableList<String> fields, Integer minimalNumberOfFields) {
}
