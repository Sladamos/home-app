package com.sladamos.book.app.items;

import javafx.scene.Node;
import javafx.scene.layout.Pane;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RequiredArgsConstructor
public class BookItemNodesManager {

    private final Pane container;
    private final Map<UUID, Node> nodes = new HashMap<>();

    public void register(UUID id, Node node) {
        nodes.put(id, node);
    }

    public void unregister(UUID id) {
        nodes.remove(id);
    }

    public boolean contains(UUID id) {
        return nodes.containsKey(id);
    }

    public void showAll(List<UUID> visibleIds) {
        List<Node> visibleNodes = new ArrayList<>();
        for (UUID id : visibleIds) {
            Node node = nodes.get(id);
            if (node != null) {
                visibleNodes.add(node);
            }
        }
        container.getChildren().setAll(visibleNodes);
    }
}
