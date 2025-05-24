package com.se.aiconomy.client.controller.ai;

import com.se.aiconomy.client.common.MyMarkdownView;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Controller for displaying AI messages in the chat panel.
 */
public class AiMessageController implements Initializable {

    /**
     * The root HBox container for the AI message.
     */
    @FXML
    private HBox root;

    /**
     * The markdown view to display the AI message content.
     */
    @FXML
    private MyMarkdownView contentView;

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
     * Sets the content of the AI message in the markdown view.
     *
     * @param content the markdown string to display
     */
    public void setContent(String content) {
        contentView.setMdString(content);
    }
}