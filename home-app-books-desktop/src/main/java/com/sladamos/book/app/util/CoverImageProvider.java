package com.sladamos.book.app.util;

import javafx.scene.image.Image;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Objects;

@Slf4j
@Component
@NoArgsConstructor
public class CoverImageProvider {

    public Image getImageCover(byte[] imageCover) {
        if (imageCover != null && imageCover.length > 0) {
            try {
                ByteArrayInputStream bais = new ByteArrayInputStream(imageCover);
                BufferedImage bufferedImage = ImageIO.read(bais);
                if (bufferedImage != null) {
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    ImageIO.write(bufferedImage, "png", baos);
                    byte[] pngBytes = baos.toByteArray();
                    return new Image(new ByteArrayInputStream(pngBytes));
                } else {
                    log.error("Could not decode image bytes. Unsupported format or corrupted data.");
                }
            } catch (IOException e) {
                log.error("Error converting image bytes for JavaFX preview", e);
            }
        }
        // Fallback w przypadku braku okładki lub błędu podczas parsowania
        return new Image(Objects.requireNonNull(getClass().getResourceAsStream("/default-cover.jpg")));
    }
}