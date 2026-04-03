package com.sladamos.book.app.modify;

import com.sladamos.app.util.components.ComponentsGenerator;
import com.sladamos.app.util.messages.BindingsCreator;
import com.sladamos.book.BookService;
import com.sladamos.book.app.modify.component.cover.SelectCoverController;
import com.sladamos.book.app.modify.component.rating.SelectRatingController;
import com.sladamos.book.app.modify.component.status.SelectStatusController;
import com.sladamos.book.app.modify.mode.AddBookMode;
import com.sladamos.book.app.modify.validation.ModifyBookValidationHandler;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.util.WaitForAsyncUtils;

import java.lang.reflect.Field;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith({ApplicationExtension.class, MockitoExtension.class})
class ModifyBookControllerTest {

    @Mock
    private BindingsCreator bindingsCreator;

    @Mock
    private ComponentsGenerator componentsGenerator;

    @Mock
    private SelectCoverController selectCoverController;

    @Mock
    private SelectRatingController selectRatingController;

    @Mock
    private SelectStatusController selectStatusController;

    @Mock
    private ModifyBookValidationHandler validationHandler;

    @Mock
    private ApplicationEventPublisher applicationEventPublisher;

    @Mock
    private BookService bookService;

    private ModifyBookController controller;
    private ModifyBookViewModel viewModel;

    @BeforeEach
    void setUp() throws Exception {
        viewModel = new ModifyBookViewModel();
        controller = new ModifyBookController(
                applicationEventPublisher,
                viewModel,
                new AddBookMode(),
                bookService,
                bindingsCreator,
                componentsGenerator,
                selectCoverController,
                selectRatingController,
                selectStatusController,
                validationHandler
        );
        when(bindingsCreator.createBinding(anyString()))
                .thenAnswer(invocation -> Bindings.createStringBinding(() -> invocation.getArgument(0)));
        when(bindingsCreator.getMessage(anyString()))
                .thenAnswer(invocation -> invocation.getArgument(0));
        when(selectStatusController.getBorrowedByValidationLabel()).thenReturn(new Label());
        doAnswer(invocation -> {
            Object componentController = invocation.getArgument(0);
            Pane wrapper = invocation.getArgument(1);
            java.net.URL resource = invocation.getArgument(2);

            FXMLLoader loader = new FXMLLoader(resource);
            loader.setControllerFactory(clazz -> componentController);
            Node loaded = loader.load();
            wrapper.getChildren().add(loaded);
            return loaded;
        }).when(componentsGenerator).addComponentAtEnd(any(), any(Pane.class), any());
        injectFxmlFields();
    }

    @Test
    void shouldInitializeBindingsAndChildComponents() {
        Platform.runLater(controller::initialize);
        WaitForAsyncUtils.waitForFxEvents();

        verify(componentsGenerator).addComponentAtEnd(any(), org.mockito.ArgumentMatchers.eq(getField("genresPanel", Pane.class)), any());
        verify(componentsGenerator).addComponentAtEnd(any(), org.mockito.ArgumentMatchers.eq(getField("authorsPanel", Pane.class)), any());
        verify(selectCoverController).bindTo(viewModel);
        verify(selectRatingController).bindTo(any());
        verify(selectStatusController).bindTo(any());
        verify(validationHandler).initialize(any(), any(), any(), any(), any(), any(), any(), any());

        assertThat(getField("submitBookLabel", Label.class).getText()).isEqualTo("books.add.name");
        assertThat(getField("submitBookButton", Button.class).getText()).isEqualTo("books.add.name");

        Platform.runLater(() -> viewModel.getPages().set(321));
        WaitForAsyncUtils.waitForFxEvents();

        assertThat(getField("pagesField", TextField.class).getText()).isEqualTo("321");
    }

    @Test
    void shouldKeepGenresWrapperVisibleAfterInitializingRequiredGenreField() {
        Platform.runLater(controller::initialize);
        WaitForAsyncUtils.waitForFxEvents();

        HBox genresWrapper = getField("genresWrapper", HBox.class);
        assertThat(genresWrapper.isVisible()).isTrue();
        assertThat(genresWrapper.isManaged()).isTrue();

        Platform.runLater(() -> viewModel.getGenres().add("Fantasy"));
        WaitForAsyncUtils.waitForFxEvents();

        assertThat(genresWrapper.isVisible()).isTrue();
        assertThat(genresWrapper.isManaged()).isTrue();
    }

    private void injectFxmlFields() throws Exception {
        setField("formScrollPane", new ScrollPane());
        setField("titleValidationLabel", new Label());
        setField("authorsValidationLabel", new Label());
        setField("isbnValidationLabel", new Label());
        setField("genresValidationLabel", new Label());
        setField("pagesValidationLabel", new Label());
        setField("descriptionValidationLabel", new Label());
        setField("titleLabel", new Label());
        setField("authorsLabel", new Label());
        setField("publisherLabel", new Label());
        setField("genresLabel", new Label());
        setField("pagesLabel", new Label());
        setField("descriptionLabel", new Label());
        setField("submitBookLabel", new Label());
        setField("genresWrapper", new HBox());
        setField("genresPanel", new VBox());
        setField("authorsPanel", new VBox());
        setField("returnToItemsButton", new Button());
        setField("submitBookButton", new Button());
        setField("titleField", new TextField());
        setField("isbnField", new TextField());
        setField("descriptionArea", new TextArea());
        setField("publisherField", new TextField());
        setField("pagesField", new TextField());
        setField("addAuthorButton", new Button());
        setField("addGenreButton", new Button());
    }

    private void setField(String name, Object value) throws Exception {
        Field field = ModifyBookController.class.getDeclaredField(name);
        field.setAccessible(true);
        field.set(controller, value);
    }

    @SuppressWarnings("unchecked")
    private <T> T getField(String name, Class<T> type) {
        try {
            Field field = ModifyBookController.class.getDeclaredField(name);
            field.setAccessible(true);
            return (T) field.get(controller);
        } catch (ReflectiveOperationException e) {
            throw new IllegalStateException(e);
        }
    }
}
