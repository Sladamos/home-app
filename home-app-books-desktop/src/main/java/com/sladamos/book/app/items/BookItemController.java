package com.sladamos.book.app.items;

import com.sladamos.book.BookStatus;
import com.sladamos.book.app.LocaleProvider;
import com.sladamos.book.app.BindingsCreator;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.StringBinding;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import lombok.RequiredArgsConstructor;

import java.text.MessageFormat;
import java.util.ResourceBundle;

@RequiredArgsConstructor
public class BookItemController {

    private final BookItemViewModel viewModel;

    private final LocaleProvider localeProvider;

    private final BindingsCreator bindingsCreator;

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
    public void initialize() {
        titleLabel.textProperty().bind(viewModel.getTitle());
        descriptionLabel.textProperty().bind(viewModel.getDescription());
        coverImageView.imageProperty().bind(viewModel.getCoverImage());

        publisherLabel.textProperty().bind(bindingsCreator.createBindingWithKey("books.items.publisher", viewModel.getPublisher()));
        pagesLabel.textProperty().bind(bindingsCreator.createBindingWithKey("books.items.pages", viewModel.getPages()));
        authorsLabel.textProperty().bind(bindingsCreator.createBindingWithKey("books.items.authors", viewModel.getAuthors()));
        genresLabel.textProperty().bind(bindingsCreator.createBindingWithKey("books.items.genres", viewModel.getGenres()));
        statusLabel.textProperty().bind(createStatusBinding());
    }

    private StringBinding createStatusBinding() {
        return Bindings.createStringBinding(
                () -> {
                    ResourceBundle bundle = ResourceBundle.getBundle("messages", localeProvider.getLocale());
                    String key = getStatusMessageKey(viewModel.getStatus().get());
                    if ("books.items.status.lent".equals(key)) {
                        String pattern = bundle.getString(key);
                        return MessageFormat.format(pattern, viewModel.getLentTo().get());
                    } else {
                        return bundle.getString(key);
                    }
                },
                localeProvider.getLocaleProperty(),
                viewModel.getStatus(),
                viewModel.getLentTo()
        );
    }

    private String getStatusMessageKey(BookStatus status) {
        return switch (status) {
            case ON_SHELF -> "books.items.status.onShelf";
            case WANT_TO_READ -> "books.items.status.wantToRead";
            case CURRENTLY_READING -> "books.items.status.currentlyReading";
            case FINISHED_READING -> "books.items.status.finishedReading";
            case BORROWED -> "books.items.status.lent";
        };
    }
}
