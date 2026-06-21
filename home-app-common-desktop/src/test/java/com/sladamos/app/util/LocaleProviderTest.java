package com.sladamos.app.util;

import org.junit.jupiter.api.Test;

import java.util.Locale;

import static org.assertj.core.api.Assertions.assertThat;

class LocaleProviderTest {

    private final LocaleProvider localeProvider = new LocaleProvider();

    @Test
    void shouldInitializeWithEnglishLocale() {
        assertThat(localeProvider.getLocale()).isEqualTo(Locale.of("en"));
    }

    @Test
    void shouldUpdateLocalePropertyCorrectly() {
        localeProvider.setLocale(Locale.of("pl"));

        assertThat(localeProvider.getLocale()).isEqualTo(Locale.of("pl"));
        assertThat(localeProvider.getLocaleProperty().get()).isEqualTo(Locale.of("pl"));
    }
}