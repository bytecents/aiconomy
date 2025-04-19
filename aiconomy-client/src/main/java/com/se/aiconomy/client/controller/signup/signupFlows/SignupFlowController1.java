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

public class SignupFlowController1 {

    @FXML
    public Hyperlink loginEntry;
    @FXML
    public Button continueButton;

    @FXML
    public TextField emailField;

    @FXML
    public TextField passwordTextField;

    @FXML
    public PasswordField passwordField;

    @FXML
    public PasswordField confirmedPasswordField;

    @FXML
    public CheckBox isAgreePolicy;

    private boolean showPassword = false;

    @Setter
    private User userData;

    @Setter  // Lombok generates the setter method for parentController
    private SignupController parentController;  // Automatically generates a setter

    public void initialize(){
        UnaryOperator<TextFormatter.Change> filter = change -> {
            String passwordRegex = "[A-Za-z0-9@#\\$%&\\-_.!]*";
            // limitations
            if (change.getText().matches(passwordRegex)) {
                return change;  // 允许符合条件的输入
            }
            return null;
        };
        emailField.setTextFormatter(new TextFormatter<>(filter));
        confirmedPasswordField.setTextFormatter(new TextFormatter<>(filter));
        passwordField.setTextFormatter(new TextFormatter<>(filter));
        passwordTextField.setTextFormatter(new TextFormatter<>(filter));
        passwordTextField.setVisible(false);
        if (userData.getEmail() != null){
            emailField.setText(userData.getEmail());
        }
        if (userData.getPassword() != null){
            passwordField.setText(userData.getPassword());
            confirmedPasswordField.setText(userData.getPassword());
        }
        if (!isAgreePolicy.isSelected()){
            isAgreePolicy.setSelected(true);
        }
    }



    @FXML
    public void switchToNextStep(ActionEvent event) throws IOException {
        if (parentController != null) {
            String userInputEmail = emailField.getText();
            String userInputPassword = passwordField.getText();
            String userConfirmPassword = confirmedPasswordField.getText();

            if (!checkInput(userInputEmail, userInputPassword, userConfirmPassword)){
                return;
            }
            userData.setEmail(userInputEmail.trim());
            userData.setPassword(userInputPassword.trim());

            parentController.loadSignupFlow("signupFlow2.fxml"); // switch to signupFlow2.fxml
            parentController.loadSignupTip("signupTip2.fxml"); // switch to signupTip2.fxml
        } else {
            System.out.println("Error: Parent controller is null");
        }
    }

    private boolean checkInput(String userInputEmail, String userInputPassword, String userConfirmPassword){
        if (userInputEmail == null || userInputEmail.trim().isEmpty() || userInputPassword == null || userInputPassword.trim().isEmpty()) {
            CustomDialog.show("Invalid Field", "Email or password is empty", "error", "Fill them");
            return false;
        }

        String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"; // regular expression for email format
        if (!userInputEmail.matches(emailRegex)) {
            CustomDialog.show("Invalid Field", "Invalid email address", "error", "Reset");
            return false;
        }

        if (userInputPassword.length() < 6) {
            CustomDialog.show("Invalid Length", "Password length should be at least 6", "error", "Reset");
            return false;
        }

        if (!userInputPassword.equals(userConfirmPassword)){
            CustomDialog.show("Invalid Password", "Passwords are inconsistent", "error", "Try again");
//                System.out.println("Passwords are inconsistent");
            return false;
        }
        if (!isAgreePolicy.isSelected()){
            CustomDialog.show("Agreement", "You must agree our policies", "error", "Got it");
            return false;
        }
        return true;
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
