package com.se.aiconomy.client.controller;

import javafx.application.Platform;
import javafx.fxml.FXML;

public class AccountsController extends BaseController {
    @FXML
    public void initialize() {
        if (userInfo == null) {
            Platform.runLater(() -> {
                // 延迟到事件调度线程中处理
                if (userInfo != null) {
                    init();
                }
            });
        } else {
            init();
        }
    }

    private void init() {

    }
}
