package com.se.aiconomy.client.controller.signup.signupFlows;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public class signupFlowController1 {
    public Hyperlink loginEntry;
    public Button continueButton;

    public void switchToSignup(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/fxml/login.fxml")));
        Stage stage = (Stage) loginEntry.getScene().getWindow();
        Scene currentScene = stage.getScene();

        // Use the scene width and height, which excludes window decorations
        double currentWidth = currentScene.getWidth();
        double currentHeight = currentScene.getHeight();

        Scene scene = new Scene(root, currentWidth, currentHeight);
        stage.setScene(scene);
        stage.show();
    }

    public void switchToNextStep(ActionEvent event) throws IOException {
//        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/fxml/signup/signupFlows/signupFlow2.fxml")));
//        Stage stage = (Stage) loginEntry.getScene().getWindow();
//        double currentWidth = stage.getWidth();
//        double currentHeight = stage.getHeight();
//        Scene scene = new Scene(root, currentWidth, currentHeight);
//        stage.setScene(scene);
//        stage.show();
    }
}
