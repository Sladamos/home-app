package com.sladamos.book.app.modify.components;

import javafx.collections.ObservableList;

public record MultipleFieldsViewModel(ObservableList<String> fields, Integer minimalNumberOfFields) {
}
