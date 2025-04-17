package com.se.aiconomy.client.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.layout.HBox;

import java.awt.*;
import java.io.IOException;
import java.util.Objects;

public class SignupController {
    @FXML private HBox flowHBox;  // to load signupFlow*.fxml

    @FXML private HBox tipHBox;   // to load signupTip*.fxml

    public void initialize() {
        try {
            // 加载并将 signupFlow1.fxml 内容添加到 flowHBox
            Node flowContent = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/fxml/signup/signupFlows/signupFlow1.fxml")));
            flowHBox.getChildren().add(flowContent);

            // 加载并将 signupTip1.fxml 内容添加到 tipHBox
            Parent tipContent = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/fxml/signup/signupTips/signupTip1.fxml")));
            tipHBox.getChildren().add(tipContent);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
