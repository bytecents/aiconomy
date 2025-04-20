package com.se.aiconomy.client.controller.ai;

import com.se.aiconomy.client.common.MyMarkdownView;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;

import java.net.URL;
import java.util.ResourceBundle;

public class AiMessageController implements Initializable {
    @FXML private HBox root;
    @FXML private MyMarkdownView contentView;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        root.setMinHeight(Region.USE_PREF_SIZE);
    }

    public void setContent(String content) {
        contentView.setMdString(content);
    }
}
