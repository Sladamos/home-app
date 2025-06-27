package com.sladamos.book.app.common;

import com.sladamos.book.app.util.BindingsCreator;
import com.sladamos.book.app.util.ImageCoverProvider;
import com.sladamos.book.app.util.TemporaryMessagesFactory;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

@Slf4j
@Component
@RequiredArgsConstructor
public class SelectCoverController {

    @FXML
    private Button selectCoverButton;

    @FXML
    private Button removeCoverButton;

    @FXML
    private ImageView coverPreview;

    private final ImageCoverProvider imageCoverProvider;

    private final BindingsCreator bindingsCreator;

    private final TemporaryMessagesFactory temporaryMessagesFactory;

    private SelectCoverViewModel viewModel;

    @FXML
    public void initialize() {
        selectCoverButton.textProperty().bind(bindingsCreator.createBinding("books.add.selectCover"));
        removeCoverButton.textProperty().bind(bindingsCreator.createBinding("books.add.removeCover"));
        removeCoverButton.setVisible(false);
    }

    public void bindTo(SelectCoverViewModel viewModel) {
        this.viewModel = viewModel;

        updateCoverPreview(viewModel.getCoverImage().get());
        this.viewModel.getCoverImage().addListener((obs, oldVal, newVal) -> updateCoverPreview(newVal));
    }

    @FXML
    private void onSelectCoverClicked() {
        log.info("Select cover button clicked");
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle(bindingsCreator.getMessage("books.selectCover.fileChooser.title"));
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter(bindingsCreator.getMessage("books.selectCover.fileChooser.images"), "*.png", "*.jpg", "*.jpeg", "*.gif")
        );
        File file = fileChooser.showOpenDialog(selectCoverButton.getScene().getWindow());
        if (file != null) {
            try {
                log.info("Selected cover file: [file: {}]", file.getAbsolutePath());
                byte[] imageBytes = Files.readAllBytes(file.toPath());
                viewModel.getCoverImage().set(imageBytes);
            } catch (IOException e) {
                log.error("Error reading cover image file: {}", file.getAbsolutePath(), e);
                temporaryMessagesFactory.showError(bindingsCreator.getMessage("books.selectCover.fileReadError"));
            }
        } else {
            log.info("No file selected in cover file chooser");
        }
    }

    @FXML
    private void onRemoveCoverClicked() {
        log.info("Remove cover button clicked");
        viewModel.getCoverImage().set(null);
    }

    private void updateCoverPreview(byte[] imageBytes) {
        if (imageCoverProvider != null) {
            coverPreview.setImage(imageCoverProvider.getImageCover(imageBytes));
        }
        removeCoverButton.setVisible(imageBytes != null && imageBytes.length > 0);
    }
}
