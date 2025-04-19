package com.se.aiconomy.client.controller.ai;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class UserMessageController {
    @FXML private Label contentLabel;

    public void setContent(String content) {
        contentLabel.setText(content);
    }
}
