package com.se.aiconomy.client.controller.ai;

import com.se.aiconomy.client.common.MyMarkdownView;
import javafx.fxml.FXML;

public class AiMessageController {

    @FXML private MyMarkdownView contentView;

    public void setContent(String content) {
        contentView.setMdString(content);
    }
}
