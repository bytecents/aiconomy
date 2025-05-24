package com.se.aiconomy.client.Application;

import javafx.scene.Node;
import javafx.scene.Parent;

/**
 * Utility class for fixing style classes in JavaFX nodes.
 * This class provides functionality to handle style classes that may contain spaces,
 * splitting them into separate style classes.
 */
public class StyleClassFixer {
    /**
     * Recursively fixes style classes for the given node and all its children.
     * This method splits any style class containing spaces into multiple separate style classes.
     *
     * <p>For example, if a node has a style class "button primary", it will be split into
     * two separate style classes: "button" and "primary".</p>
     *
     * @param root The root node whose style classes need to be fixed.
     *             If null, the method returns without any action.
     */
    public static void fixStyleClasses(Node root) {
        if (root == null) return;

        var styleClasses = root.getStyleClass();
        for (int i = 0; i < styleClasses.size(); i++) {
            String original = styleClasses.get(i);
            if (original.contains(" ")) {
                styleClasses.remove(i);
                String[] parts = original.trim().split("\\s+");
                styleClasses.addAll(i, java.util.List.of(parts));
                i += parts.length - 1;
            }
        }

        if (root instanceof Parent parent) {
            for (Node child : parent.getChildrenUnmodifiable()) {
                fixStyleClasses(child);
            }
        }
    }
}
