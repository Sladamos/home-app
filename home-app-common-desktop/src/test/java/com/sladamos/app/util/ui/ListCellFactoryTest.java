package com.sladamos.app.util.ui;

import com.sladamos.app.util.messages.BindingsCreator;
import com.sladamos.app.util.ui.ListCellFactory;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.StringBinding;
import javafx.scene.Scene;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.testfx.api.FxRobot;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;
import org.testfx.util.WaitForAsyncUtils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith({ApplicationExtension.class, MockitoExtension.class})
class ListCellFactoryTest {

    @Mock
    private BindingsCreator bindingsCreator;

    @InjectMocks
    private ListCellFactory factory;

    private ListView<String> listView;

    @Start
    private void start(Stage stage) {
        listView = new ListView<>();
        listView.setCellFactory(lv -> factory.createListCell(item -> "key." + item));
        stage.setScene(new Scene(listView, 200, 200));
        stage.show();
    }

    @Test
    void shouldSetEmptyTextWhenItemIsNull(FxRobot robot) {
        Platform.runLater(() -> listView.getItems().add(null));
        WaitForAsyncUtils.waitForFxEvents();

        ListCell<String> cell = getFirstCell(robot);

        assertThat(cell.getText()).isEmpty();
        assertThat(cell.textProperty().isBound()).isFalse();
    }

    @Test
    void shouldBindTextWhenItemIsPresent(FxRobot robot) {
        StringBinding dummyBinding = Bindings.createStringBinding(() -> "Translated");
        when(bindingsCreator.createBinding("key.test")).thenReturn(dummyBinding);

        Platform.runLater(() -> listView.getItems().add("test"));
        WaitForAsyncUtils.waitForFxEvents();

        ListCell<String> cell = getFirstCell(robot);
        assertThat(cell.getText()).isEqualTo("Translated");
        assertThat(cell.textProperty().isBound()).isTrue();
    }

    @Test
    void shouldUnbindWhenItemChangesToEmpty(FxRobot robot) {
        StringBinding dummyBinding = Bindings.createStringBinding(() -> "Translated");
        when(bindingsCreator.createBinding("key.test")).thenReturn(dummyBinding);

        Platform.runLater(() -> listView.getItems().add("test"));
        WaitForAsyncUtils.waitForFxEvents();

        Platform.runLater(() -> listView.getItems().clear());
        WaitForAsyncUtils.waitForFxEvents();

        ListCell<String> cell = getFirstCell(robot);
        assertThat(cell.getText()).isEmpty();
        assertThat(cell.textProperty().isBound()).isFalse();
    }

    @SuppressWarnings("unchecked")
    private ListCell<String> getFirstCell(FxRobot robot) {
        return robot.lookup(".list-cell").queryAll().stream()
                .map(node -> (ListCell<String>) node)
                .findFirst()
                .orElseThrow();
    }
}
