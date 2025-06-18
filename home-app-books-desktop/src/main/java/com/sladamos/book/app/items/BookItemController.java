package com.sladamos.book.app.items;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class BookItemController {

    private final BookItemViewModel viewModel;

    @FXML
    private Label titleLabel;
    @FXML
    private Label isbnLabel;
    @FXML
    private Label descriptionLabel;
    @FXML
    private Label publisherLabel;
    @FXML
    private Label borrowedToLabel;
    @FXML
    private Label pagesLabel;
    @FXML
    private Label ratingLabel;
    @FXML
    private Label favoriteLabel;
    @FXML
    private Label statusLabel;
    @FXML
    private Label authorsLabel;
    @FXML
    private Label genresLabel;

    @FXML
    public void initialize() {
        titleLabel.textProperty().bind(viewModel.getTitle());
        isbnLabel.textProperty().bind(viewModel.getIsbn());
        descriptionLabel.textProperty().bind(viewModel.getDescription());
        publisherLabel.textProperty().bind(viewModel.getPublisher());
        borrowedToLabel.textProperty().bind(viewModel.getBorrowedTo());
        pagesLabel.textProperty().bind(viewModel.getPages().asString());
        ratingLabel.textProperty().bind(viewModel.getRating().asString());
        favoriteLabel.textProperty().bind(viewModel.getFavorite().asString());
        statusLabel.textProperty().bind(viewModel.getStatus());
        authorsLabel.textProperty().bind(viewModel.getAuthors());
        genresLabel.textProperty().bind(viewModel.getGenres());
    }
}
