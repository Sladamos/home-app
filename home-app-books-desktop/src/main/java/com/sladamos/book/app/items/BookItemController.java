package com.sladamos.book.app.items;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class BookItemController {

    private final BookItemViewModel viewModel;

    @FXML
    private Label titleLabel;
    @FXML
    private Label isbnLabel;
    @FXML
    private Text descriptionText;
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
    private ImageView coverImageView;

    @FXML
    public void initialize() {
        titleLabel.textProperty().bind(viewModel.getTitle());
        isbnLabel.textProperty().bind(viewModel.getIsbn());
        descriptionText.textProperty().bind(viewModel.getDescription());
        publisherLabel.textProperty().bind(viewModel.getPublisher());
        borrowedToLabel.textProperty().bind(viewModel.getBorrowedTo());
        pagesLabel.textProperty().bind(viewModel.getPages().asString());
        ratingLabel.textProperty().bind(viewModel.getRating().asString());
        favoriteLabel.textProperty().bind(viewModel.getFavorite().asString());
        statusLabel.textProperty().bind(viewModel.getStatus());
        authorsLabel.textProperty().bind(viewModel.getAuthors());
        genresLabel.textProperty().bind(viewModel.getGenres());
        coverImageView.imageProperty().bind(viewModel.getCoverImage());
    }
}
