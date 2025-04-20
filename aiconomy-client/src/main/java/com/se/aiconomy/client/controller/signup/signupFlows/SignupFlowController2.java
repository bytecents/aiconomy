package com.se.aiconomy.client.controller.signup.signupFlows;

import com.se.aiconomy.client.common.CustomDialog;
import com.se.aiconomy.client.controller.BaseController;
import com.se.aiconomy.client.controller.signup.SignupController;
import com.se.aiconomy.server.model.entity.User;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import lombok.Setter;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Objects;

public class SignupFlowController2 extends BaseController {
    @FXML
    public Button continueButton;

    @FXML
    public Button goBackButton;

    @FXML
    public Hyperlink loginEntry;

    @FXML
    public TextField firstNameField;

    @FXML
    public TextField lastNameField;

    @FXML
    public TextField phoneNumberField;


    @FXML
    public DatePicker dateOfBirthPicker;

    @FXML
    public ChoiceBox<String> currencyChoiceBox;

    @Setter  // Lombok generates the setter method for parentController
    private SignupController parentController;  // Automatically generates a setter
    @Setter
    private User userData;

    public void initialize() {
        // Optionally set the default value
        dateOfBirthPicker.setEditable(false);
        dateOfBirthPicker.setValue(LocalDate.parse("2001-01-01"));
        currencyChoiceBox.setValue("USD - US Dollar");
        // System.out.println("User: " + userData.getEmail() + " " + userData.getPassword());
        if (userData.getFirstName() != null) firstNameField.setText(userData.getFirstName());
        if (userData.getLastName() != null) lastNameField.setText(userData.getLastName());
        if (userData.getPhone() != null) phoneNumberField.setText(userData.getPhone());
        if (userData.getBirthDate() != null) dateOfBirthPicker.setValue(userData.getBirthDate());
        if (userData.getCurrency() != null) currencyChoiceBox.setValue(userData.getCurrency());
    }

    public void openDatePicker() {
        dateOfBirthPicker.show();
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
    public void goBackToSignupFlow1() {

        saveData();

        if (parentController != null) {
            parentController.loadSignupFlow("signupFlow1.fxml"); // switch to signupFlow2.fxml
            parentController.loadSignupTip("signupTip1.fxml"); // switch to signupTip2.fxml
        } else {
            System.out.println("Parent controller is null");
        }
    }

    @FXML
    public void switchToNextStep() {
        if (parentController != null) {
            if (!checkInput(firstNameField.getText(), lastNameField.getText())) {
                return;
            }
            saveData();
            parentController.loadSignupFlow("signupFlow3.fxml"); // switch to signupFlow2.fxml
            parentController.loadSignupTip("signupTip3.fxml"); // switch to signupTip2.fxml
        } else {
            System.out.println("Parent controller is null");
        }
    }

    private void saveData() {
        if (firstNameField.getText() != null) userData.setFirstName(firstNameField.getText().trim());
        if (lastNameField.getText() != null) userData.setLastName(lastNameField.getText().trim());
        if (phoneNumberField.getText() != null) userData.setPhone(phoneNumberField.getText().trim());
        if (dateOfBirthPicker.getValue() != null) userData.setBirthDate(dateOfBirthPicker.getValue());
        if (currencyChoiceBox.getValue() != null) userData.setCurrency(currencyChoiceBox.getValue());
    }

    private boolean checkInput(String firstName, String lastName) {
        if (firstName == null || lastName == null || firstName.trim().isEmpty() || lastName.trim().isEmpty()) {
            CustomDialog.show("Invalid Name", "First name or last name is empty", "error", "Fill them");
            return false;
        }
        return true;
    }
}
