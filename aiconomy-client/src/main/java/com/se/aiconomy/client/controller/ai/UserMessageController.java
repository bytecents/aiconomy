package com.se.aiconomy.client.controller.ai;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Controller for displaying user messages in the chat panel.
 */
public class UserMessageController implements Initializable {

    /**
     * The root HBox container for the user message.
     */
    @FXML
    private HBox root;

    /**
     * The label to display the user message content.
     */
    @FXML
    private Label contentLabel;

    /**
     * Initializes the controller and sets up the root container.
     *
     * @param url the location used to resolve relative paths for the root object, or null if unknown
     * @param resourceBundle the resources used to localize the root object, or null if not localized
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        root.setMinHeight(Region.USE_PREF_SIZE);
    }

    /**
     * Sets the content of the user message in the label.
     *
     * @param content the string to display as the user message
     */
    public void setContent(String content) {
        contentLabel.setText(content);
    }
}