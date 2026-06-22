package com.sladamos.common.image;

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

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ImageCoverResizerTest {

    @Mock
    private ImageScaleCalculator imageScaleCalculator;

    @InjectMocks
    private ImageCoverResizer imageCoverResizer;

    public static final int MAX_COVER_WIDTH = 200;

    public static final int MAX_COVER_HEIGHT = 300;

    @Test
    void shouldReturnOriginalImageWhenUnableToReadImage() throws IOException {
        byte[] originalImage = new byte[]{0, 1, 2, 3, 4, 5};
        ImageParameters imageParameters = createImageParameters(originalImage, MAX_COVER_WIDTH, MAX_COVER_HEIGHT);

        byte[] resizedImage = imageCoverResizer.resizeImage(imageParameters);

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
            ImageParameters imageParameters = createImageParameters(originalBytes, MAX_COVER_WIDTH, MAX_COVER_HEIGHT);

            when(imageScaleCalculator.calculateScale(originalWidth, originalHeight, MAX_COVER_WIDTH, MAX_COVER_HEIGHT)).thenReturn(scale);

            byte[] resizedBytes = imageCoverResizer.resizeImage(imageParameters);
            BufferedImage resized = ImageIO.read(new ByteArrayInputStream(resizedBytes));

            assertAll(
                    () -> assertThat(resized.getWidth()).isEqualTo(expectedWidth),
                    () -> assertThat(resized.getHeight()).isEqualTo(MAX_COVER_HEIGHT)
            );
        }
    }

    private ImageParameters createImageParameters(byte[] originalImage, int width, int height) {
        return new ImageParameters(originalImage, width, height);
    }

}