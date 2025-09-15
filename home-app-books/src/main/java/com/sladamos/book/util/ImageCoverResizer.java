package com.sladamos.book.util;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

@Component
@Slf4j
@RequiredArgsConstructor
public class ImageCoverResizer {

    private final ImageScaleCalculator imageScaleCalculator;

    public byte[] resizeImage(byte[] originalBytes) throws IOException {
        BufferedImage originalImage = ImageIO.read(new ByteArrayInputStream(originalBytes));
        if (originalImage == null) {
            log.error("Image could not be resized");
            return originalBytes;
        }

        int originalWidth = originalImage.getWidth();
        int originalHeight = originalImage.getHeight();

        double scale = imageScaleCalculator.calculateScale(originalWidth, originalHeight);

        log.info("Scaling image with: [scale: {}]", scale);

        int newWidth = (int) (originalWidth * scale);
        int newHeight = (int) (originalHeight * scale);

        BufferedImage resized = createResizedImage(newWidth, newHeight, originalImage);

        return saveToJpg(resized);
    }

    private BufferedImage createResizedImage(int newWidth, int newHeight, BufferedImage originalImage) {
        BufferedImage resized = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = resized.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2d.drawImage(originalImage, 0, 0, newWidth, newHeight, null);
        g2d.dispose();
        return resized;
    }

    private byte[] saveToJpg(BufferedImage resized) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ImageIO.write(resized, "jpg", outputStream);
        return outputStream.toByteArray();
    }
}
