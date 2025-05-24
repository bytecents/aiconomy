package com.se.aiconomy.client.controller.signup.signupFlows;

import com.se.aiconomy.client.Application.StyleClassFixer;
import com.se.aiconomy.client.common.CustomDialog;
import com.se.aiconomy.client.controller.signup.SignupController;
import com.se.aiconomy.server.model.entity.User;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import lombok.Setter;  // Import Lombok's @Setter annotation

import java.io.IOException;
import java.util.Objects;
import java.util.function.UnaryOperator;

/**
 * Controller for the first step of the signup flow.
 * Handles user input for email and password, validation, and navigation to the next step.
 */
public class SignupFlowController1 {

    /** Hyperlink to switch to the login page. */
    @FXML
    public Hyperlink loginEntry;

    /** Button to continue to the next signup step. */
    @FXML
    public Button continueButton;

    /** Text field for user email input. */
    @FXML
    public TextField emailField;

    /** Text field for displaying password in plain text. */
    @FXML
    public TextField passwordTextField;

    /** Password field for entering password securely. */
    @FXML
    public PasswordField passwordField;

    /** Password field for confirming password. */
    @FXML
    public PasswordField confirmedPasswordField;

    /** Checkbox for agreeing to the policy. */
    @FXML
    public CheckBox isAgreePolicy;

    /** Flag to indicate whether the password is visible. */
    private boolean showPassword = false;

    /** User data object for storing signup information. */
    @Setter
    private User userData;

    /** Reference to the parent signup controller. */
    @Setter
    private SignupController parentController;

    /**
     * Initializes the controller, sets up input filters and default values.
     */
    public void initialize() {
        UnaryOperator<TextFormatter.Change> filter = change -> {
            String passwordRegex = "[A-Za-z0-9@#\\$%&\\-_.!~]*";
            if (change.getText().matches(passwordRegex)) {
                return change;
            }
            return null;
        };
        emailField.setTextFormatter(new TextFormatter<>(filter));
        confirmedPasswordField.setTextFormatter(new TextFormatter<>(filter));
        passwordField.setTextFormatter(new TextFormatter<>(filter));
        passwordTextField.setTextFormatter(new TextFormatter<>(filter));
        passwordTextField.setVisible(false);
        if (userData.getEmail() != null) {
            emailField.setText(userData.getEmail());
        }
        if (userData.getPassword() != null) {
            passwordField.setText(userData.getPassword());
            confirmedPasswordField.setText(userData.getPassword());
        }
        if (!isAgreePolicy.isSelected()) {
            isAgreePolicy.setSelected(true);
        }
    }

    /**
     * Handles the continue button action, validates input and navigates to the next signup step.
     * @param event the action event
     * @throws IOException if loading the next FXML fails
     */
    @FXML
    public void switchToNextStep(ActionEvent event) throws IOException {
        if (parentController != null) {
            String userInputEmail = emailField.getText();
            String userInputPassword = passwordField.getText();
            String userConfirmPassword = confirmedPasswordField.getText();

            if (!checkInput(userInputEmail, userInputPassword, userConfirmPassword)) {
                return;
            }
            userData.setEmail(userInputEmail.trim());
            userData.setPassword(userInputPassword.trim());

            parentController.loadSignupFlow("signupFlow2.fxml");
            parentController.loadSignupTip("signupTip2.fxml");
        } else {
            System.out.println("Error: Parent controller is null");
        }
    }

    /**
     * Validates the user input for email and password fields.
     * @param userInputEmail the email input
     * @param userInputPassword the password input
     * @param userConfirmPassword the confirmed password input
     * @return true if input is valid, false otherwise
     */
    private boolean checkInput(String userInputEmail, String userInputPassword, String userConfirmPassword) {
        if (userInputEmail == null || userInputEmail.trim().isEmpty() || userInputPassword == null || userInputPassword.trim().isEmpty()) {
            CustomDialog.show("Invalid Field", "Email or password is empty", "error", "Fill them");
            return false;
        }

        String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
        if (!userInputEmail.matches(emailRegex)) {
            CustomDialog.show("Invalid Field", "Invalid email address", "error", "Reset");
            return false;
        }

        if (userInputPassword.length() < 6) {
            CustomDialog.show("Invalid Length", "Password length should be at least 6", "error", "Reset");
            return false;
        }

        if (!userInputPassword.equals(userConfirmPassword)) {
            CustomDialog.show("Invalid Password", "Passwords are inconsistent", "error", "Try again");
            return false;
        }
        if (!isAgreePolicy.isSelected()) {
            CustomDialog.show("Agreement", "You must agree our policies", "error", "Got it");
            return false;
        }
        return true;
    }

    /**
     * Switches the scene to the login page.
     * @param event the action event
     * @throws IOException if loading the login FXML fails
     */
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

    /**
     * Handles the password visibility toggle.
     * @param mouseEvent the mouse event
     */
    @FXML
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