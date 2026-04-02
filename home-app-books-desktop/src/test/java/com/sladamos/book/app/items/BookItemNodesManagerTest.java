package com.sladamos.book.app.items;

import javafx.scene.Node;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

@ExtendWith(ApplicationExtension.class)
class BookItemNodesManagerTest {

    private VBox container;
    private BookItemNodesManager manager;

    @Start
    private void start(Stage stage) {
        container = new VBox();
        manager = new BookItemNodesManager(container);
    }

    @Test
    void shouldRegisterNodeWithoutAddingToContainer() {
        UUID id = UUID.randomUUID();
        Node node = mock(Node.class);

        manager.register(id, node);

        assertThat(container.getChildren()).isEmpty();
        assertThat(manager.contains(id)).isTrue();
    }

    @Test
    void shouldUnregisterNode() {
        UUID id = UUID.randomUUID();
        Node node = mock(Node.class);
        manager.register(id, node);

        manager.unregister(id);

        assertThat(manager.contains(id)).isFalse();
    }

    @Test
    void shouldReturnFalseForUnregisteredId() {
        assertThat(manager.contains(UUID.randomUUID())).isFalse();
    }

    @Test
    void shouldShowAllNodesInOrder() {
        UUID id1 = UUID.randomUUID();
        UUID id2 = UUID.randomUUID();
        UUID id3 = UUID.randomUUID();
        Node node1 = mock(Node.class);
        Node node2 = mock(Node.class);
        Node node3 = mock(Node.class);
        manager.register(id1, node1);
        manager.register(id2, node2);
        manager.register(id3, node3);

        manager.showAll(List.of(id1, id2, id3));

        assertThat(container.getChildren()).containsExactly(node1, node2, node3);
    }

    @Test
    void shouldShowSubsetOfNodes() {
        UUID id1 = UUID.randomUUID();
        UUID id2 = UUID.randomUUID();
        UUID id3 = UUID.randomUUID();
        Node node1 = mock(Node.class);
        Node node2 = mock(Node.class);
        Node node3 = mock(Node.class);
        manager.register(id1, node1);
        manager.register(id2, node2);
        manager.register(id3, node3);

        manager.showAll(List.of(id1, id3));

        assertThat(container.getChildren()).containsExactly(node1, node3);
    }

    @Test
    void shouldReorderNodesOnShowAll() {
        UUID id1 = UUID.randomUUID();
        UUID id2 = UUID.randomUUID();
        Node node1 = mock(Node.class);
        Node node2 = mock(Node.class);
        manager.register(id1, node1);
        manager.register(id2, node2);
        manager.showAll(List.of(id1, id2));

        manager.showAll(List.of(id2, id1));

        assertThat(container.getChildren()).containsExactly(node2, node1);
    }

    @Test
    void shouldIgnoreMissingIdsInShowAll() {
        UUID id1 = UUID.randomUUID();
        UUID id2 = UUID.randomUUID();
        Node node1 = mock(Node.class);
        manager.register(id1, node1);

        manager.showAll(List.of(id1, id2));

        assertThat(container.getChildren()).containsExactly(node1);
    }

    @Test
    void shouldShowEmptyListClearContainer() {
        UUID id = UUID.randomUUID();
        Node node = mock(Node.class);
        manager.register(id, node);
        manager.showAll(List.of(id));

        manager.showAll(List.of());

        assertThat(container.getChildren()).isEmpty();
    }
}
