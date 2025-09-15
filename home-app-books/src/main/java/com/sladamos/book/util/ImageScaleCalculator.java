package com.sladamos.book.util;

import org.springframework.stereotype.Component;

import static com.sladamos.book.Book.MAX_COVER_HEIGHT;
import static com.sladamos.book.Book.MAX_COVER_WIDTH;

@Component
public class ImageScaleCalculator {

    public double calculateScale(int originalWidth, int originalHeight) {
        double widthRatio = (double) MAX_COVER_WIDTH / originalWidth;
        double heightRatio = (double) MAX_COVER_HEIGHT / originalHeight;
        return Math.min(widthRatio, heightRatio);
    }
}
