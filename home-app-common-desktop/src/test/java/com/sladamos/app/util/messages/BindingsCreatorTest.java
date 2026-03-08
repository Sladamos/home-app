package com.sladamos.app.util.messages;

import com.sladamos.app.util.LocaleProvider;
import javafx.beans.binding.StringBinding;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.context.support.ResourceBundleMessageSource;

import java.util.Locale;

import static org.assertj.core.api.Assertions.assertThat;

class BindingsCreatorTest {

    private LocaleProvider localeProvider;
    private BindingsCreator bindingsCreator;

    @BeforeEach
    void setUp() {
        localeProvider = new LocaleProvider();
        localeProvider.setLocale(Locale.of("en"));

        ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
        messageSource.setBasenames("messages", "common-messages");
        messageSource.setDefaultEncoding("UTF-8");

        bindingsCreator = new BindingsCreator(messageSource, localeProvider);
    }

    @Test
    void shouldReturnFallbackWhenKeyIsMissing() {
        StringBinding binding = bindingsCreator.createBinding("nonexistent.key");

        assertThat(binding.get()).isEqualTo("[nonexistent.key]");
    }

    @Test
    void shouldReactToIntegerPropertyChange() {
        SimpleIntegerProperty property = new SimpleIntegerProperty(5);
        StringBinding binding = bindingsCreator.createBindingWithKey("test.greeting", property);

        assertThat(binding.get()).isEqualTo("Hello 5");

        property.set(10);

        assertThat(binding.get()).isEqualTo("Hello 10");
    }

    @Test
    void shouldReactToStringPropertyChange() {
        SimpleStringProperty property = new SimpleStringProperty("John");
        StringBinding binding = bindingsCreator.createBindingWithKey("test.greeting", property);

        assertThat(binding.get()).isEqualTo("Hello John");

        property.set("Anna");

        assertThat(binding.get()).isEqualTo("Hello Anna");
    }

    @Test
    void shouldReactToLocaleChange() {
        StringBinding binding = bindingsCreator.createBinding("test.greeting");

        assertThat(binding.get()).isEqualTo("Hello {0}");

        localeProvider.setLocale(Locale.of("pl"));

        assertThat(binding.get()).isEqualTo("Cześć {0}");
    }

    @Test
    void shouldCreateBindingWithStaticArgument() {
        ObservableValue<String> binding = bindingsCreator.createBindingWithArg("test.greeting", "StaticArg");

        assertThat(binding.getValue()).isEqualTo("Hello StaticArg");
    }
}