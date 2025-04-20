package com.se.aiconomy.client.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;

public class DashboardController extends BaseController {
    @FXML
    Button quickAddButton;

    @FXML
    public void quickAdd() {
        System.out.println("Test user info in quick add:" + userInfo);
    }
}
