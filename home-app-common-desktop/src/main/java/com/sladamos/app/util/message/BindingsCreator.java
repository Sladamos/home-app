package com.sladamos.app.util.message;

import com.sladamos.app.util.LocaleProvider;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.StringBinding;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableValue;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;
import org.springframework.stereotype.Component;

import java.text.MessageFormat;

@Slf4j
@Component
@RequiredArgsConstructor
public class BindingsCreator {

    private final MessageSource messageSource;
    private final LocaleProvider localeProvider;

    public StringBinding createBindingWithKey(String messageKey, IntegerProperty property) {
        return Bindings.createStringBinding(
                () -> formatMessage(messageKey, property.get()),
                localeProvider.getLocaleProperty(),
                property
        );
    }

    public StringBinding createBindingWithKey(String messageKey, StringProperty property) {
        return Bindings.createStringBinding(
                () -> formatMessage(messageKey, property.get()),
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

    public <T> ObservableValue<String> createBindingWithArg(String messageKey, T messageArg) {
        return Bindings.createStringBinding(
                () -> formatMessage(messageKey, messageArg),
                localeProvider.getLocaleProperty()
        );
    }

    public String getMessage(String messageKey, Object... args) {
        try {
            return messageSource.getMessage(messageKey, args, localeProvider.getLocale());
        } catch (NoSuchMessageException e) {
            log.warn("Missing translation for key: [key: {}]", messageKey);
            return "[" + messageKey + "]";
        }
    }

    private String formatMessage(String messageKey, Object arg) {
        return MessageFormat.format(getMessage(messageKey), arg);
    }
}