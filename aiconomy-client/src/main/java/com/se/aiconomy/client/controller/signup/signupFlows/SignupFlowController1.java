package com.se.aiconomy.client.controller.signup.signupFlows;

import com.se.aiconomy.client.Application.StyleClassFixer;
import com.se.aiconomy.client.controller.signup.SignupController;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.stage.Stage;
import lombok.Setter;  // Import Lombok's @Setter annotation

import java.io.IOException;
import java.util.Objects;

public class SignupFlowController1 {

    @FXML
    public Hyperlink loginEntry;
    @FXML
    public Button continueButton;

    @Setter  // Lombok generates the setter method for parentController
    private SignupController parentController;  // Automatically generates a setter

    @FXML
    public void switchToNextStep(ActionEvent event) throws IOException {
        if (parentController != null) {
            parentController.loadSignupFlow("signupFlow2.fxml"); // switch to signupFlow2.fxml
            parentController.loadSignupTip("signupTip2.fxml"); // switch to signupTip2.fxml
        } else {
            System.out.println("Parent controller is null");
        }
    }

    @FXML
    public void switchToLogin(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/fxml/login.fxml")));

        Stage stage = (Stage) loginEntry.getScene().getWindow();
        Scene currentScene = stage.getScene();

        double currentWidth = currentScene.getWidth();
        double currentHeight = currentScene.getHeight();

        Scene scene = new Scene(root, currentWidth, currentHeight);
        stage.setScene(scene);
        stage.show();
    }
}
