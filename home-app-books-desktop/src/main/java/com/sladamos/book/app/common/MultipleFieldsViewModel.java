package com.sladamos.book.app.common;

import javafx.collections.ObservableList;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class MultipleFieldsViewModel {

    private final ObservableList<String> fields;

    private final Integer minimalNumberOfFields;
}
