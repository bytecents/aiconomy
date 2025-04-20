package com.se.aiconomy.client.Application;

import javafx.scene.Node;
import javafx.scene.Parent;

public class StyleClassFixer {
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
//                System.out.printf("[StyleClassFixer] Fixed node: %s\n", root.getClass().getSimpleName());
//                System.out.printf("  Original: \"%s\"\n", original);
//                System.out.printf("  Fixed   : %s\n", styleClasses);
            }
        }

        if (root instanceof Parent parent) {
            for (Node child : parent.getChildrenUnmodifiable()) {
                fixStyleClasses(child);
            }
        }
    }
}
