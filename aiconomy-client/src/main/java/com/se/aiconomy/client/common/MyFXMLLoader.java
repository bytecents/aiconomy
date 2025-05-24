package com.se.aiconomy.client.common;

import com.se.aiconomy.client.Application.StyleClassFixer;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;

/**
 * Custom FXML loader that automatically fixes style classes after loading.
 * <p>
 * This loader extends {@link FXMLLoader} and applies {@link StyleClassFixer#fixStyleClasses(Node)}
 * to the root node after loading the FXML, ensuring that any style classes containing spaces
 * are split into separate style classes.
 * </p>
 */
public class MyFXMLLoader extends FXMLLoader {

    /**
     * Constructs a new MyFXMLLoader for the specified FXML resource.
     *
     * @param fxml the path to the FXML file, relative to the classpath
     */
    public MyFXMLLoader(String fxml) {
        super(MyFXMLLoader.class.getResource(fxml));
    }

    /**
     * Loads the FXML file and applies style class fixes to the root node.
     *
     * @param <T> the type of the root node
     * @return the root node of the loaded FXML, or {@code null} if loading fails
     */
    @Override
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

    /**
     * Returns the controller associated with the loaded FXML.
     *
     * @param <T> the type of the controller
     * @return the controller instance
     */
    @Override
    public <T> T getController() {
        return super.getController();
    }
}