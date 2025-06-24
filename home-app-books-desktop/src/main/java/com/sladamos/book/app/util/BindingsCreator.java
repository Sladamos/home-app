package com.sladamos.book.app.util;

import javafx.beans.binding.Bindings;
import javafx.beans.binding.StringBinding;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.StringProperty;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.text.MessageFormat;
import java.util.ResourceBundle;

@Component
@RequiredArgsConstructor
public class BindingsCreator {

    private final LocaleProvider localeProvider;

    public StringBinding createBindingWithKey(String messageKey, IntegerProperty property) {
        return Bindings.createStringBinding(
                () -> {
                    ResourceBundle bundle = ResourceBundle.getBundle("messages", localeProvider.getLocale());
                    String pattern = bundle.getString(messageKey);
                    return MessageFormat.format(pattern, property.get());
                },
                localeProvider.getLocaleProperty(),
                property
        );
    }

    public StringBinding createBindingWithKey(String messageKey, StringProperty property) {
        return Bindings.createStringBinding(
                () -> {
                    String pattern = getMessage(messageKey);
                    return MessageFormat.format(pattern, property.get());
                },
                localeProvider.getLocaleProperty(),
                property
        );
    }

    public StringBinding createBinding(String messageKey) {
        return Bindings.createStringBinding(
                () -> getMessage(messageKey),
                localeProvider.getLocaleProperty()
        );
    }

    private String getMessage(String messageKey) {
        return ResourceBundle.getBundle("messages", localeProvider.getLocale()).getString(messageKey);
    }
}