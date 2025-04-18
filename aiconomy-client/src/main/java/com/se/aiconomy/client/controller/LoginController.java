package com.se.aiconomy.client.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.io.IOException;

public class LoginController {
    public Button loginButton;

    @FXML
    public void login(ActionEvent event) throws IOException {
        goToMain(event);
    }

    @FXML
    public void goToMain(ActionEvent event) throws IOException {

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/main.fxml"));
        Parent root = loader.load();

        // Get the current window, and load main page
        Stage stage = (Stage) loginButton.getScene().getWindow();
        Scene scene = new Scene(root, 1200, 820);
        stage.setScene(scene);
        stage.show();
    }
}
