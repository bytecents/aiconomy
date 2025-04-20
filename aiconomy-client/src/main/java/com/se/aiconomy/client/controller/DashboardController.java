package com.se.aiconomy.client.controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

public class DashboardController extends BaseController {
    @FXML
    public Label welcomeTextField;
    @FXML
    Button quickAddButton;

    @FXML
    void initialize() {
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
        welcomeTextField.setText("Welcome back, " + userInfo.getFirstName() + "!");
    }

    @FXML
    public void quickAdd() {
        System.out.println("Test user info in quick add:" + userInfo);
    }
}