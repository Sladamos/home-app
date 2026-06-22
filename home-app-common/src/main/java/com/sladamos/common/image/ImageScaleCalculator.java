package com.sladamos.common.image;

import org.springframework.stereotype.Component;

@Component
public class ImageScaleCalculator {

    public double calculateScale(int originalWidth, int originalHeight, int maxCoverWidth, int maxCoverHeight) {
        double widthRatio = (double) maxCoverWidth / originalWidth;
        double heightRatio = (double) maxCoverHeight / originalHeight;
        return Math.min(widthRatio, heightRatio);
    }
}
