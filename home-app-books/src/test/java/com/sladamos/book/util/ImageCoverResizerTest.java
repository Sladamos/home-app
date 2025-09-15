package com.sladamos.book.util;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

import static com.sladamos.book.Book.MAX_COVER_HEIGHT;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ImageCoverResizerTest {

    @Mock
    private ImageScaleCalculator imageScaleCalculator;

    @InjectMocks
    private ImageCoverResizer imageCoverResizer;

    @Test
    void shouldReturnOriginalImageWhenUnableToReadImage() throws IOException {
        byte[] originalImage = new byte[]{0, 1, 2, 3, 4, 5};
        byte[] resizedImage = imageCoverResizer.resizeImage(originalImage);
        assertThat(resizedImage).isSameAs(originalImage);
    }

    @Test
    void shouldCreateResizedImage() throws IOException {
        try (InputStream in = getClass().getResourceAsStream("/example_cover.png");) {
            byte[] originalBytes = Objects.requireNonNull(in).readAllBytes();
            int originalHeight = 740;
            int originalWidth = 1080;
            double scale = (double) MAX_COVER_HEIGHT / originalHeight;
            int expectedWidth = (int) (originalWidth * scale);

            when(imageScaleCalculator.calculateScale(originalWidth, originalHeight)).thenReturn(scale);

            byte[] resizedBytes = imageCoverResizer.resizeImage(originalBytes);
            BufferedImage resized = ImageIO.read(new ByteArrayInputStream(resizedBytes));

            assertAll(
                    () -> assertThat(resized.getWidth()).isEqualTo(expectedWidth),
                    () -> assertThat(resized.getHeight()).isEqualTo(MAX_COVER_HEIGHT)
            );
        }
    }

}