package com.sladamos.book.app.modify.component.fields;

import javafx.collections.ObservableList;

public record MultipleFieldsViewModel(ObservableList<String> fields, Integer minimalNumberOfFields) {
}
