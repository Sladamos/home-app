package com.sladamos.book.app.common;

import javafx.scene.image.Image;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.util.Objects;

@Slf4j
@Component
@NoArgsConstructor
public class CoverImageProvider {

    private static final String DEFAULT_COVER_PATH = "/default-cover.jpg";

    private Image cachedDefaultCover;

    public Image getImageCover(byte[] imageCover) {
        if (imageCover == null || imageCover.length == 0) {
            return getDefaultCover();
        }
        try {
            return new Image(new ByteArrayInputStream(imageCover));
        } catch (Exception e) {
            log.error("Failed to load cover image from bytes", e);
            return getDefaultCover();
        }
    }

    private Image getDefaultCover() {
        if (cachedDefaultCover == null) {
            cachedDefaultCover = new Image(Objects.requireNonNull(getClass().getResourceAsStream(DEFAULT_COVER_PATH)));
        }
        return cachedDefaultCover;
    }
}
