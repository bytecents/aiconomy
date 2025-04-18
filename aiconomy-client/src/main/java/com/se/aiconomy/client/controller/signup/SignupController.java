package com.se.aiconomy.client.controller.signup;

import com.se.aiconomy.client.controller.signup.signupFlows.SignupFlowController1;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.layout.HBox;

import java.io.IOException;
import java.util.Objects;

public class SignupController {

    @FXML
    private HBox flowHBox;  // to load signupFlow*.fxml

    @FXML
    private HBox tipHBox;   // to load signupTip*.fxml

    public void initialize() {
        loadSignupFlow("signupFlow1.fxml");
        loadSignupTip("signupTip1.fxml");
    }

    public void loadSignupFlow(String fxmlFileName) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/signup/signupFlows/" + fxmlFileName));
            // Using controller factory to inject parent controller
            loader.setControllerFactory(clazz -> {
                if (clazz.equals(SignupFlowController1.class)) {
                    SignupFlowController1 controller = new SignupFlowController1();
                    controller.setParentController(this);  // Set parent controller via Lombok's generated setter
                    return controller;
                }
                try {
                    return clazz.getDeclaredConstructor().newInstance();
                } catch (Exception e) {
                    e.printStackTrace();
                    return null;
                }
            });
            Node flowContent = loader.load();
            flowHBox.getChildren().clear();
            flowHBox.getChildren().add(flowContent);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void loadSignupTip(String fxmlFileName) {
        try {
            Parent tipContent = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/fxml/signup/signupTips/" + fxmlFileName)));
            tipHBox.getChildren().clear();
            tipHBox.getChildren().add(tipContent);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

