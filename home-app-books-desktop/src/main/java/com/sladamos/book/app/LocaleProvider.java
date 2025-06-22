package com.sladamos.book.app;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import lombok.Getter;
import org.springframework.stereotype.Component;

import java.util.Locale;

@Component
@Getter
public class LocaleProvider {

    private final ObjectProperty<Locale> localeProperty = new SimpleObjectProperty<>(Locale.of("pl"));

    public Locale getLocale() {
        return localeProperty.get();
    }
}
