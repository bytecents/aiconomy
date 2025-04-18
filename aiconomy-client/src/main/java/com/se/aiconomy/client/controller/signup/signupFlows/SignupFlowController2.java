package com.se.aiconomy.client.controller.signup.signupFlows;

import com.se.aiconomy.client.controller.signup.SignupController;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Hyperlink;
import javafx.stage.Stage;
import lombok.Setter;

import java.io.IOException;
import java.util.Objects;

public class SignupFlowController2 {
    @FXML
    public Button continueButton;
    @FXML
    public Button goBackButton;
    @FXML
    public Hyperlink loginEntry;
    @FXML
    public ChoiceBox<String> currencyChoiceBox;
    @Setter  // Lombok generates the setter method for parentController
    private SignupController parentController;  // Automatically generates a setter

    public void initialize() {
        // Optionally set the default value
        currencyChoiceBox.setValue("USD - US Dollar");
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

    @FXML
    public void goBackToSignupFlow1(){
        if (parentController != null) {
            parentController.loadSignupFlow("signupFlow1.fxml"); // switch to signupFlow2.fxml
            parentController.loadSignupTip("signupTip1.fxml"); // switch to signupTip2.fxml
        } else {
            System.out.println("Parent controller is null");
        }
    }

    @FXML
    public void switchToNextStep(){
        if (parentController != null) {
            parentController.loadSignupFlow("signupFlow3.fxml"); // switch to signupFlow2.fxml
            parentController.loadSignupTip("signupTip3.fxml"); // switch to signupTip2.fxml
        } else {
            System.out.println("Parent controller is null");
        }
    }
}
