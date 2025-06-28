package com.sladamos.book.app.util;

import javafx.scene.image.Image;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.util.Objects;

@Component
@NoArgsConstructor
public class CoverImageProvider {

    public Image getImageCover(byte[] imageCover) {
        if (imageCover != null) {
            return new Image(new ByteArrayInputStream(imageCover));
        } else {
            return new Image(Objects.requireNonNull(getClass().getResourceAsStream("/default-cover.jpg")));
        }
    }
}
