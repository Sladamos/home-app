package com.sladamos.app.util;

import javafx.scene.image.Image;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class IconFactory {

    public Image createIcon() {
        return new Image(Objects.requireNonNull(IconFactory.class.getResourceAsStream("icon.jpg")));
    }
}