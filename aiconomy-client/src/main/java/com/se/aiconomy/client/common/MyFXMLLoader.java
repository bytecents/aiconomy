package com.se.aiconomy.client.common;

import com.se.aiconomy.client.Application.StyleClassFixer;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;

public class MyFXMLLoader extends FXMLLoader {
    public MyFXMLLoader(String fxml) {
        super(MyFXMLLoader.class.getResource(fxml));
    }

    public <T> T load() {
        try {
            T parent = super.load();
            StyleClassFixer.fixStyleClasses((Node) parent);
            return parent;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public <T> T getController() {
        return super.getController();
    }
}
