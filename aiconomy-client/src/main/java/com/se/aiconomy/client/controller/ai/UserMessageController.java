package com.se.aiconomy.client.controller.ai;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;

import java.net.URL;
import java.util.ResourceBundle;

public class UserMessageController implements Initializable {
    @FXML private HBox root;
    @FXML private Label contentLabel;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        root.setMinHeight(Region.USE_PREF_SIZE);
    }


    public void setContent(String content) {
        contentLabel.setText(content);
    }
}
