package com.sladamos.app.util;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import lombok.Getter;
import org.springframework.stereotype.Component;

import java.util.Locale;

@Component
@Getter
public class LocaleProvider {

    private final ObjectProperty<Locale> localeProperty = new SimpleObjectProperty<>(Locale.of("en"));

    public Locale getLocale() {
        return localeProperty.get();
    }

    public void setLocale(Locale locale) {
        this.localeProperty.set(locale);
    }
}
