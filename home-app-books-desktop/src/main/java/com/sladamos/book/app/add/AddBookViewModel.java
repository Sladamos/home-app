package com.sladamos.book.app.add;

import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import lombok.Getter;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
@Getter
public class AddBookViewModel {
    private final StringProperty title = new SimpleStringProperty();
    private final StringProperty author = new SimpleStringProperty();
    private final StringProperty isbn = new SimpleStringProperty();
    private final StringProperty description = new SimpleStringProperty();
    private final StringProperty publisher = new SimpleStringProperty();
    private final StringProperty borrowedBy = new SimpleStringProperty();
    private final IntegerProperty pages = new SimpleIntegerProperty();
    private final ObjectProperty<Integer> rating = new SimpleObjectProperty<>();
    private final BooleanProperty favorite = new SimpleBooleanProperty();
    private final ObjectProperty<LocalDate> readDate = new SimpleObjectProperty<>();
    private final ObjectProperty<byte[]> coverImage = new SimpleObjectProperty<>();
    private final ObservableList<String> authors = FXCollections.observableArrayList();
    private final ObservableList<String> genres = FXCollections.observableArrayList();
}
