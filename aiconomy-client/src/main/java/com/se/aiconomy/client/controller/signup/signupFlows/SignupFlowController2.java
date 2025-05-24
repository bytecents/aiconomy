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

/**
 * Controller for the second step of the signup flow.
 * Handles user input for personal information, validation, and navigation between steps.
 */
public class SignupFlowController2 extends BaseController {
    /** Continue button to proceed to the next signup step. */
    @FXML
    public Button continueButton;

    /** Button to go back to the previous signup step. */
    @FXML
    public Button goBackButton;

    /** Hyperlink to switch to the login page. */
    @FXML
    public Hyperlink loginEntry;

    /** Text field for user's first name input. */
    @FXML
    public TextField firstNameField;

    /** Text field for user's last name input. */
    @FXML
    public TextField lastNameField;

    /** Text field for user's phone number input. */
    @FXML
    public TextField phoneNumberField;

    /** Date picker for user's date of birth. */
    @FXML
    public DatePicker dateOfBirthPicker;

    /** Choice box for selecting user's preferred currency. */
    @FXML
    public ChoiceBox<String> currencyChoiceBox;

    /** Reference to the parent signup controller. */
    @Setter
    private SignupController parentController;

    /** User data object for storing signup information. */
    @Setter
    private User userData;

    /**
     * Initializes the controller, sets up default values and populates fields if userData is present.
     */
    public void initialize() {
        dateOfBirthPicker.setEditable(false);
        dateOfBirthPicker.setValue(LocalDate.parse("2001-01-01"));
        currencyChoiceBox.setValue("USD - US Dollar");
        if (userData.getFirstName() != null) firstNameField.setText(userData.getFirstName());
        if (userData.getLastName() != null) lastNameField.setText(userData.getLastName());
        if (userData.getPhone() != null) phoneNumberField.setText(userData.getPhone());
        if (userData.getBirthDate() != null) dateOfBirthPicker.setValue(userData.getBirthDate());
        if (userData.getCurrency() != null) currencyChoiceBox.setValue(userData.getCurrency());
    }

    /**
     * Opens the date picker for the user to select their date of birth.
     */
    public void openDatePicker() {
        dateOfBirthPicker.show();
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
     * Handles the action to go back to the first signup step.
     * Saves the current input data and loads the previous step.
     */
    @FXML
    public void goBackToSignupFlow1() {
        saveData();
        if (parentController != null) {
            parentController.loadSignupFlow("signupFlow1.fxml");
            parentController.loadSignupTip("signupTip1.fxml");
        } else {
            System.out.println("Parent controller is null");
        }
    }

    /**
     * Handles the continue button action, validates input and navigates to the next signup step.
     */
    @FXML
    public void switchToNextStep() {
        if (parentController != null) {
            if (!checkInput(firstNameField.getText(), lastNameField.getText())) {
                return;
            }
            saveData();
            parentController.loadSignupFlow("signupFlow3.fxml");
            parentController.loadSignupTip("signupTip3.fxml");
        } else {
            System.out.println("Parent controller is null");
        }
    }

    /**
     * Saves the current input data to the userData object.
     */
    private void saveData() {
        if (firstNameField.getText() != null) userData.setFirstName(firstNameField.getText().trim());
        if (lastNameField.getText() != null) userData.setLastName(lastNameField.getText().trim());
        if (phoneNumberField.getText() != null) userData.setPhone(phoneNumberField.getText().trim());
        if (dateOfBirthPicker.getValue() != null) userData.setBirthDate(dateOfBirthPicker.getValue());
        if (currencyChoiceBox.getValue() != null) userData.setCurrency(currencyChoiceBox.getValue());
    }

    /**
     * Validates the user input for first name and last name fields.
     * @param firstName the first name input
     * @param lastName the last name input
     * @return true if input is valid, false otherwise
     */
    private boolean checkInput(String firstName, String lastName) {
        if (firstName == null || lastName == null || firstName.trim().isEmpty() || lastName.trim().isEmpty()) {
            CustomDialog.show("Invalid Name", "First name or last name is empty", "error", "Fill them");
            return false;
        }
        return true;
    }
}