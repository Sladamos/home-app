package com.sladamos.book.app.items;

import com.sladamos.book.app.util.BindingsCreator;
import com.sladamos.book.app.util.LocaleProvider;
import com.sladamos.book.app.util.StarsFactory;
import com.sladamos.book.app.util.StatusMessageKeyProvider;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.StringBinding;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import lombok.RequiredArgsConstructor;

import java.text.MessageFormat;
import java.util.ResourceBundle;

@RequiredArgsConstructor
public class BookItemController {

    private final BookItemViewModel viewModel;

    private final LocaleProvider localeProvider;

    private final BindingsCreator bindingsCreator;

    private final StatusMessageKeyProvider statusMessageKeyProvider;

    private final StarsFactory starsFactory;

    @FXML
    private Label titleLabel;
    @FXML
    private Label descriptionLabel;
    @FXML
    private Label publisherLabel;
    @FXML
    private Label pagesLabel;
    @FXML
    private Label statusLabel;
    @FXML
    private Label authorsLabel;
    @FXML
    private Label genresLabel;
    @FXML
    private ImageView coverImageView;
    @FXML
    private HBox ratingStars;

    @FXML
    public void initialize() {
        titleLabel.textProperty().bind(viewModel.getTitle());
        descriptionLabel.textProperty().bind(viewModel.getDescription());
        coverImageView.imageProperty().bind(viewModel.getCoverImage());

        publisherLabel.textProperty().bind(bindingsCreator.createBindingWithKey("books.items.publisher", viewModel.getPublisher()));
        pagesLabel.textProperty().bind(bindingsCreator.createBindingWithKey("books.items.pages", viewModel.getPages()));
        authorsLabel.textProperty().bind(bindingsCreator.createBindingWithKey("books.items.authors", viewModel.getAuthors()));
        genresLabel.textProperty().bind(bindingsCreator.createBindingWithKey("books.items.genres", viewModel.getGenres()));
        statusLabel.textProperty().bind(createStatusBinding());
        ratingStars.getChildren().clear();
        ratingStars.getChildren().addAll(starsFactory.createStars(viewModel));
    }

    private StringBinding createStatusBinding() {
        return Bindings.createStringBinding(
                () -> {
                    ResourceBundle bundle = ResourceBundle.getBundle("messages", localeProvider.getLocale());
                    String key = statusMessageKeyProvider.getStatusMessageKey(viewModel.getStatus().get());
                    if ("books.items.status.borrowed".equals(key)) {
                        String pattern = bundle.getString(key);
                        return MessageFormat.format(pattern, viewModel.getBorrowedBy().get());
                    } else {
                        return bundle.getString(key);
                    }
                },
                localeProvider.getLocaleProperty(),
                viewModel.getStatus(),
                viewModel.getBorrowedBy()
        );
    }
}
