package com.se.aiconomy.client.controller;

import com.se.aiconomy.client.Application.StyleClassFixer;
import javafx.animation.FadeTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.Objects;

public class LoginController {
    public Button loginButton;
    public Hyperlink signUpEntry;

    @FXML
    public void login(ActionEvent event) throws IOException {
        switchToMain(event);
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
}
