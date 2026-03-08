package com.sladamos.book.app.util;

import javafx.application.Platform;
import javafx.scene.image.Image;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CoverImageProviderTest {

    private final CoverImageProvider provider = new CoverImageProvider();

    @BeforeAll
    static void initJFX() {
        try {
            Platform.startup(() -> {});
        } catch (IllegalStateException ignored) {
        }
    }

    @Test
    void shouldReturnDefaultCoverWhenBytesAreNull() {
        Image image = provider.getImageCover(null);

        assertThat(image).isNotNull();
        assertThat(image.isError()).isFalse();
    }

    @Test
    void shouldReturnDefaultCoverWhenBytesAreEmpty() {
        Image image = provider.getImageCover(new byte[0]);

        assertThat(image).isNotNull();
        assertThat(image.isError()).isFalse();
    }

    @Test
    void shouldReturnDefaultCoverWhenBytesAreCorrupted() {
        Image image = provider.getImageCover(new byte[]{1, 2, 3});

        assertThat(image).isNotNull();
        assertThat(image.isError()).isTrue();
    }
}