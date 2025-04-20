package com.se.aiconomy.client.controller.signup;

import com.se.aiconomy.client.Application.StyleClassFixer;
import com.se.aiconomy.client.controller.BaseController;
import com.se.aiconomy.client.controller.signup.signupFlows.SignupFlowController1;
import com.se.aiconomy.client.controller.signup.signupFlows.SignupFlowController2;
import com.se.aiconomy.client.controller.signup.signupFlows.SignupFlowController3;
import com.se.aiconomy.client.controller.signup.signupTips.SignupTipController1;
import com.se.aiconomy.client.controller.signup.signupTips.SignupTipController2;
import com.se.aiconomy.client.controller.signup.signupTips.SignupTipController3;
import com.se.aiconomy.server.model.entity.User;
import javafx.animation.FadeTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.layout.HBox;
import javafx.util.Duration;

import java.io.IOException;
import java.util.Objects;

public class SignupController extends BaseController {

    private final User userData = new User();
    @FXML
    private HBox flowHBox;  // to load signupFlow*.fxml
    @FXML
    private HBox tipHBox;   // to load signupTip*.fxml

    public void initialize() {
        loadSignupFlow("signupFlow1.fxml");
        loadSignupTip("signupTip1.fxml");
    }


    public void loadSignupFlow(String fxmlFileName) {
        FadeTransition fadeOut = new FadeTransition(Duration.millis(100), flowHBox);  // Change from tipHBox to flowHBox
        fadeOut.setFromValue(1.0);
        fadeOut.setToValue(0.0);
        fadeOut.setOnFinished(event -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/signup/signupFlows/" + fxmlFileName));
                // Using controller factory to inject parent controller
                loader.setControllerFactory(clazz -> {
                    if (clazz.equals(SignupFlowController1.class)) {
                        SignupFlowController1 controller = new SignupFlowController1();
                        controller.setParentController(this);  // Set parent controller via Lombok's generated setter
                        controller.setUserData(userData);
                        return controller;
                    }
                    if (clazz.equals(SignupFlowController2.class)) {
                        SignupFlowController2 controller = new SignupFlowController2();
                        controller.setParentController(this);
                        controller.setUserData(userData);
                        return controller;
                    }
                    if (clazz.equals(SignupFlowController3.class)) {
                        SignupFlowController3 controller = new SignupFlowController3();
                        controller.setParentController(this);
                        controller.setUserData(userData);
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

                // Apply fade-in effect after loading the new content
                FadeTransition fadeIn = new FadeTransition(Duration.millis(100), flowHBox);  // Change from tipHBox to flowHBox
                fadeIn.setFromValue(0.0);
                fadeIn.setToValue(1.0);
                fadeIn.play();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        fadeOut.play();
    }


    public void loadSignupTip(String fxmlFileName) {
        FadeTransition fadeOut = new FadeTransition(Duration.millis(100), tipHBox);  // Apply fade-out to tipHBox
        fadeOut.setFromValue(1.0);
        fadeOut.setToValue(0.0);
        fadeOut.setOnFinished(event -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/signup/signupTips/" + fxmlFileName));

                // Using controller factory to inject parent controller
                loader.setControllerFactory(clazz -> {
                    if (clazz.equals(SignupTipController1.class)) {
                        SignupTipController1 controller = new SignupTipController1();
                        controller.setParentController(this);  // Set parent controller via Lombok's generated setter
                        return controller;
                    }
                    if (clazz.equals(SignupTipController2.class)) {
                        SignupTipController2 controller = new SignupTipController2();
                        controller.setParentController(this);
                        return controller;
                    }
                    if (clazz.equals(SignupTipController3.class)) {
                        SignupTipController3 controller = new SignupTipController3();
                        controller.setParentController(this);
                        return controller;
                    }
                    try {
                        return clazz.getDeclaredConstructor().newInstance();
                    } catch (Exception e) {
                        e.printStackTrace();
                        return null;
                    }
                });

                Parent tipContent = loader.load();
                tipHBox.getChildren().clear();
                tipHBox.getChildren().add(tipContent);

                // Apply fade-in effect after loading the new content
                FadeTransition fadeIn = new FadeTransition(Duration.millis(100), tipHBox);  // Apply fade-in to tipHBox
                fadeIn.setFromValue(0.0);
                fadeIn.setToValue(1.0);
                fadeIn.play();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        fadeOut.play();
    }

}

