package com.sladamos.book.app.modify.components;

import com.sladamos.app.util.messages.BindingsCreator;
import com.sladamos.app.util.messages.TemporaryMessagesFactory;
import com.sladamos.book.app.util.CoverImageProvider;
import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
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

    private final CoverImageProvider coverImageProvider;

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

        coverPreview.imageProperty().bind(Bindings.createObjectBinding(
                () -> getImage(viewModel),
                viewModel.getCoverImage()
        ));
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

    private Image getImage(SelectCoverViewModel viewModel) {
        byte[] imageBytes = viewModel.getCoverImage().get();
        removeCoverButton.setVisible(imageBytes != null && imageBytes.length > 0);
        selectCoverButton.requestFocus();
        return coverImageProvider.getImageCover(imageBytes);
    }
}
