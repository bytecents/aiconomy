package com.se.aiconomy.client.controller;

import com.se.aiconomy.client.Application.StyleClassFixer;
import com.se.aiconomy.client.common.CustomDialog;
import com.se.aiconomy.server.handler.UserRequestHandler;
import com.se.aiconomy.server.model.dto.user.request.UserLoginRequest;
import com.se.aiconomy.server.model.dto.user.request.UserRegisterRequest;
import com.se.aiconomy.server.model.entity.User;
import com.se.aiconomy.server.service.UserService;
import com.se.aiconomy.server.service.impl.UserServiceImpl;
import com.se.aiconomy.server.storage.service.impl.JSONStorageServiceImpl;
import javafx.animation.FadeTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.Objects;
import java.util.function.UnaryOperator;

public class LoginController {
    @FXML public Button loginButton;
    @FXML public Hyperlink signUpEntry;
    @FXML public TextField emailField;
    @FXML public PasswordField passwordField;
    @FXML public TextField passwordTextField;
    private UserRequestHandler userRequestHandler;

    private boolean showPassword = false;

    @FXML public void initialize() {
        passwordTextField.setVisible(false);
        UnaryOperator<TextFormatter.Change> filter = change -> {
            // not allow " "
            if (change.getText().contains(" ")) {
                return null;
            }
            return change;
        };
        emailField.setTextFormatter(new TextFormatter<>(filter));
        passwordField.setTextFormatter(new TextFormatter<>(filter));
        passwordTextField.setTextFormatter(new TextFormatter<>(filter));

//        UserRegisterRequest registerRequest = new UserRegisterRequest();
//        registerRequest.setEmail("test@example.com");
//        registerRequest.setPassword("test");
//        registerRequest.setFirstName("test");
//        registerRequest.setLastName("example");
//        try{
//            userRequestHandler.handleRegisterRequest(registerRequest);
//        } catch (Exception e){
//            e.printStackTrace();
//        }
    }

    @FXML
    public void login(ActionEvent event) throws IOException {
        String userInputEmail = emailField.getText();
        String userInputPassword;
        if (showPassword)
            userInputPassword = passwordTextField.getText();
        else
            userInputPassword = passwordField.getText();

        try {
            this.userRequestHandler = new UserRequestHandler(new UserServiceImpl(JSONStorageServiceImpl.getInstance()));
            UserLoginRequest userLoginRequest = new UserLoginRequest();
            userLoginRequest.setEmail(userInputEmail);
            userLoginRequest.setPassword(userInputPassword);
            userRequestHandler.handleLoginRequest(userLoginRequest);

            switchToMain(event);
            CustomDialog.show("Success", "Login successfully", "success", "OK");

        } catch (RuntimeException rte) {
            System.out.println(rte.getMessage());
            CustomDialog.show("Error", rte.getMessage(), "error", "Try Again");
        }
    }

    public void switchToSignup(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/fxml/signup/signup.fxml")));
        Stage stage = (Stage) signUpEntry.getScene().getWindow();
        Scene currentScene = stage.getScene();

        // Use the scene width and height, which excludes window decorations
        double currentWidth = currentScene.getWidth();
        double currentHeight = currentScene.getHeight();

        Scene scene = new Scene(root, currentWidth, currentHeight);
        stage.setScene(scene);
        stage.show();
    }

    @FXML
    public void switchToMain(ActionEvent event) throws IOException {

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/main.fxml"));
        Parent root = loader.load();
        StyleClassFixer.fixStyleClasses(root);

        // Get the current window, and load main page
        Stage stage = (Stage) loginButton.getScene().getWindow();
        Scene currentScene = stage.getScene();

        // Use the scene width and height, which excludes window decorations
        double currentWidth = currentScene.getWidth();
        double currentHeight = currentScene.getHeight();

        Scene scene = new Scene(root, currentWidth, currentHeight);
        stage.setScene(scene);
        stage.show();
//        FadeTransition fadeOut = getFadeTransition(stage, scene);
//        fadeOut.play();

    }

    private static @NotNull FadeTransition getFadeTransition(Stage stage, Scene scene) {
        FadeTransition fadeOut = new FadeTransition(Duration.seconds(0.2), stage.getScene().getRoot());
        fadeOut.setFromValue(1.0); // opacity = 1
        fadeOut.setToValue(0.0);   // opacity = 0
        fadeOut.setOnFinished(event1 -> {
            // after fade-out
            stage.setScene(scene);
            stage.show();

            // create fade-in animation
            FadeTransition fadeIn = new FadeTransition(Duration.seconds(1), scene.getRoot());
            fadeIn.setFromValue(0.0);  // opacity = 0
            fadeIn.setToValue(1.0);    // opacity = 1
            fadeIn.play();             // play the fade-in animation
        });
        return fadeOut;
    }

    public void handlePasswordVisibility(MouseEvent mouseEvent) {
        this.showPassword = !this.showPassword;
        String userInputPassword;
        if (showPassword)
            userInputPassword = passwordField.getText();
        else
            userInputPassword = passwordTextField.getText();
        passwordField.setVisible(!showPassword);
        passwordField.setText(userInputPassword);
        passwordTextField.setVisible(showPassword);
        passwordTextField.setText(userInputPassword);
    }
}
