package com.sladamos.app.util.messages;

import com.sladamos.app.util.FXWinUtil;
import com.sladamos.app.util.IconFactory;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;
import javafx.stage.Stage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;

import java.lang.reflect.Field;
import java.util.concurrent.CompletableFuture;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith({ApplicationExtension.class, MockitoExtension.class})
class TemporaryMessagesFactoryTest {

    @Mock
    private IconFactory iconFactory;

    @Mock
    private FXWinUtil fxWinUtil;

    @Mock
    private BindingsCreator bindingsCreator;

    @InjectMocks
    private TemporaryMessagesFactory factory;

    @Start
    private void start(Stage stage) {
    }

    @BeforeEach
    void setUp() throws Exception {
        Field durationField = TemporaryMessagesFactory.class.getDeclaredField("messageDurationSeconds");
        durationField.setAccessible(true);
        durationField.set(factory, 0.0);

        when(iconFactory.createIcon()).thenReturn(new WritableImage(1, 1));
    }

    @Test
    void shouldShowConfirmationAlertWithCorrectProperties() {
        when(bindingsCreator.getMessage("alert.confirmation.title")).thenReturn("Confirm Title");
        when(bindingsCreator.getMessage("alert.confirmation.yes")).thenReturn("Yes Button");
        when(bindingsCreator.getMessage("alert.confirmation.no")).thenReturn("No Button");
        Image dummyImage = new WritableImage(100, 100);

        Alert alert = CompletableFuture.supplyAsync(() ->
                        factory.showConfirmation("Do you want to proceed?", dummyImage),
                Platform::runLater
        ).join();

        assertThat(alert.getTitle()).isEqualTo("Confirm Title");
        assertThat(alert.getContentText()).isEqualTo("Do you want to proceed?");
        assertThat(alert.getButtonTypes())
                .extracting(ButtonType::getText)
                .containsExactlyInAnyOrder("Yes Button", "No Button");
        verify(fxWinUtil).setTitleBarColor(any(Stage.class));
    }

    @Test
    void shouldShowErrorAlertWithCorrectProperties() {
        when(bindingsCreator.getMessage("alert.default.title")).thenReturn("Error Title");

        CompletableFuture.runAsync(() ->
                        factory.showError("Something went wrong"),
                Platform::runLater
        ).join();

        verify(fxWinUtil).setTitleBarColor(any(Stage.class));
    }
}