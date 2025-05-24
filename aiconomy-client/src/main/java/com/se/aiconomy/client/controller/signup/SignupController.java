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

/**
 * Controller for the signup process.
 * Handles loading and switching between signup flow and tip views.
 */
public class SignupController extends BaseController {

    /**
     * User data for the signup process.
     */
    private final User userData = new User();

    /**
     * HBox for loading signup flow FXMLs.
     */
    @FXML
    private HBox flowHBox;

    /**
     * HBox for loading signup tip FXMLs.
     */
    @FXML
    private HBox tipHBox;

    /**
     * Initializes the controller by loading the first signup flow and tip views.
     */
    public void initialize() {
        loadSignupFlow("signupFlow1.fxml");
        loadSignupTip("signupTip1.fxml");
    }

    /**
     * Loads a signup flow FXML into the flowHBox with fade transition.
     *
     * @param fxmlFileName the FXML file name to load (e.g., "signupFlow1.fxml")
     */
    public void loadSignupFlow(String fxmlFileName) {
        FadeTransition fadeOut = new FadeTransition(Duration.millis(100), flowHBox);
        fadeOut.setFromValue(1.0);
        fadeOut.setToValue(0.0);
        fadeOut.setOnFinished(event -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/signup/signupFlows/" + fxmlFileName));
                // Use controller factory to inject parent controller and user data
                loader.setControllerFactory(clazz -> {
                    if (clazz.equals(SignupFlowController1.class)) {
                        SignupFlowController1 controller = new SignupFlowController1();
                        controller.setParentController(this);
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

                // Fade in after loading new content
                FadeTransition fadeIn = new FadeTransition(Duration.millis(100), flowHBox);
                fadeIn.setFromValue(0.0);
                fadeIn.setToValue(1.0);
                fadeIn.play();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        fadeOut.play();
    }

    /**
     * Loads a signup tip FXML into the tipHBox with fade transition.
     *
     * @param fxmlFileName the FXML file name to load (e.g., "signupTip1.fxml")
     */
    public void loadSignupTip(String fxmlFileName) {
        FadeTransition fadeOut = new FadeTransition(Duration.millis(100), tipHBox);
        fadeOut.setFromValue(1.0);
        fadeOut.setToValue(0.0);
        fadeOut.setOnFinished(event -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/signup/signupTips/" + fxmlFileName));
                // Use controller factory to inject parent controller
                loader.setControllerFactory(clazz -> {
                    if (clazz.equals(SignupTipController1.class)) {
                        SignupTipController1 controller = new SignupTipController1();
                        controller.setParentController(this);
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

                // Fade in after loading new content
                FadeTransition fadeIn = new FadeTransition(Duration.millis(100), tipHBox);
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