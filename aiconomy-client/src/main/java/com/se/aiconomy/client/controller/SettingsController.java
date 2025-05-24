package com.se.aiconomy.client.controller;

import com.se.aiconomy.client.Application.StyleClassFixer;
import com.se.aiconomy.client.common.CustomDialog;
import com.se.aiconomy.server.handler.UserRequestHandler;
import com.se.aiconomy.server.model.dto.user.request.UserUpdateRequest;
import com.se.aiconomy.server.service.impl.UserServiceImpl;
import com.se.aiconomy.server.storage.service.impl.JSONStorageServiceImpl;
import javafx.animation.FadeTransition;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

/**
 * Controller for handling user settings in the application.
 * Provides functionality for updating user profile, toggling switches,
 * handling logout, and managing UI interactions.
 */
public class SettingsController extends BaseController {

    private final UserRequestHandler userRequestHandler = new UserRequestHandler(new UserServiceImpl(JSONStorageServiceImpl.getInstance()));

    @FXML
    private Button logoutButton;
    @FXML
    private Rectangle switchTrack;
    @FXML
    private Circle switchThumb;
    private boolean isOn = true;
    @FXML
    private Rectangle switchAITrack;
    @FXML
    private Circle switchAIThumb;
    @FXML
    private ComboBox<String> currencyComboBox;
    @FXML
    private ComboBox<String> languageComboBox;
    @FXML
    private ComboBox<String> dateFormatComboBox;
    @FXML
    private VBox vbox1;
    @FXML
    private VBox vbox2;
    @FXML
    private VBox vbox3;
    @FXML
    private TextField firstNameTextField;
    @FXML
    private TextField lastNameTextField;
    @FXML
    private TextField phoneNumberTextField;
    @FXML
    private DatePicker birthDatePicker;

    /**
     * Validates the user input form for updating profile information.
     *
     * @return true if the form is valid, false otherwise
     */
    private boolean checkForm() {
        String firstName = firstNameTextField.getText();
        String lastName = lastNameTextField.getText();
        String phone = phoneNumberTextField.getText();
        LocalDate birthDate = birthDatePicker.getValue();
        String currency = currencyComboBox.getValue();

        if (firstName == null || firstName.trim().isEmpty()) {
            CustomDialog.show("Error", "Please enter your first name!", "error", "Try Again");
            return false;
        }
        if (!firstName.matches("[A-Za-z\\s]+")) {
            CustomDialog.show("Error", "First name can only contain letters and spaces.", "error", "Try Again");
            return false;
        }
        if (firstName.length() > 30) {
            CustomDialog.show("Error", "First name is too long (max 30 characters).", "error", "Try Again");
            return false;
        }

        if (lastName == null || lastName.trim().isEmpty()) {
            CustomDialog.show("Error", "Please enter your last name!", "error", "Try Again");
            return false;
        }
        if (!lastName.matches("[A-Za-z\\s]+")) {
            CustomDialog.show("Error", "Last name can only contain letters and spaces.", "error", "Try Again");
            return false;
        }
        if (lastName.length() > 30) {
            CustomDialog.show("Error", "Last name is too long (max 30 characters).", "error", "Try Again");
            return false;
        }

        if (phone == null || phone.trim().isEmpty()) {
            CustomDialog.show("Error", "Please enter your phone number!", "error", "Try Again");
            return false;
        }
        if (!phone.matches("\\d{10,15}")) {
            CustomDialog.show("Error", "Phone number must be 10–15 digits.", "error", "Try Again");
            return false;
        }

        if (birthDate == null) {
            CustomDialog.show("Error", "Please select your birth date!", "error", "Try Again");
            return false;
        }
        if (birthDate.isAfter(LocalDate.now())) {
            CustomDialog.show("Error", "Birth date cannot be in the future!", "error", "Try Again");
            return false;
        }

        if (currency == null) {
            CustomDialog.show("Error", "Please select a currency!", "error", "Try Again");
            return false;
        }

        return true;
    }

    /**
     * Toggles the main switch UI component.
     */
    @FXML
    private void toggleSwitch() {
        isOn = !isOn;
        if (isOn) {
            switchTrack.setFill(Paint.valueOf("#2563eb")); // ON blue
            switchThumb.setTranslateX(12); // Move right
        } else {
            switchTrack.setFill(Paint.valueOf("#ccc")); // OFF gray
            switchThumb.setTranslateX(-12); // Move left
        }
    }

    /**
     * Toggles the AI switch UI component.
     */
    @FXML
    private void toggleAISwitch() {
        isOn = !isOn;
        if (isOn) {
            switchAITrack.setFill(Paint.valueOf("#2563eb")); // ON blue
            switchAIThumb.setTranslateX(12); // Move right
        } else {
            switchAITrack.setFill(Paint.valueOf("#ccc")); // OFF gray
            switchAIThumb.setTranslateX(-12); // Move left
        }
    }

    /**
     * Handles the save button click event.
     *
     * @param actionEvent the action event
     */
    @FXML
    public void onSaveClick(ActionEvent actionEvent) {
        saveChanges();
    }

    /**
     * Saves the changes made in the settings form.
     */
    private void saveChanges() {
        if (!checkForm()) {
            return;
        }
        try {
            UserUpdateRequest userUpdateRequest = new UserUpdateRequest();
            userUpdateRequest.setUserId(userInfo.getId());
            userUpdateRequest.setFirstName(firstNameTextField.getText());
            userUpdateRequest.setLastName(lastNameTextField.getText());
            userUpdateRequest.setPhone(phoneNumberTextField.getText());
            userUpdateRequest.setBirthDate(birthDatePicker.getValue());
            userUpdateRequest.setCurrency(currencyComboBox.getValue());
            userRequestHandler.handleUpdateUserRequest(userUpdateRequest);
            userInfo.setFirstName(firstNameTextField.getText());
            userInfo.setLastName(lastNameTextField.getText());
            userInfo.setPhone(phoneNumberTextField.getText());
            userInfo.setBirthDate(birthDatePicker.getValue());
            userInfo.setCurrency(currencyComboBox.getValue());
        } catch (Exception e) {
            e.printStackTrace();
            CustomDialog.show("Error", e.getMessage(), "error", "Try Again");
        }
    }

    /**
     * Initializes the settings controller and loads user data.
     */
    @FXML
    public void initialize() {
        if (userInfo == null) {
            Platform.runLater(() -> {
                // Delay initialization to the event dispatch thread
                if (userInfo != null) {
                    init();
                }
            });
        } else {
            init();
        }
    }

    /**
     * Initializes the UI components with user information.
     */
    @FXML
    public void init() {
        currencyComboBox.setItems(FXCollections.observableArrayList("USD", "EUR", "GBP", "CNY", "JPY"));
        languageComboBox.setItems(FXCollections.observableArrayList("English", "Chinese", "Spanish", "French"));
        dateFormatComboBox.setItems(FXCollections.observableArrayList("YYYY-MM-DD", "YYYY/MM/DD", "MM-DD-YYYY"));

        currencyComboBox.setValue(userInfo.getCurrency());
        languageComboBox.setValue("English");
        dateFormatComboBox.setValue("YYYY/MM/DD");

        firstNameTextField.setText(userInfo.getFirstName());
        lastNameTextField.setText(userInfo.getLastName());
        phoneNumberTextField.setText(userInfo.getPhone());
        birthDatePicker.setValue(userInfo.getBirthDate());
    }

    /**
     * Handles the logout action and navigates back to the login page.
     *
     * @param event the action event
     */
    @FXML
    private void logout(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/login.fxml"));
            Parent loginRoot = loader.load();

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

            Scene currentScene = stage.getScene();
            Scene loginScene = new Scene(loginRoot, currentScene.getWidth(), currentScene.getHeight());

            StyleClassFixer.fixStyleClasses(loginRoot);
            stage.setScene(loginScene);
            stage.show();

            FadeTransition fadeIn = new FadeTransition(Duration.millis(200), loginRoot);
            fadeIn.setFromValue(0.0);
            fadeIn.setToValue(1.0);
            fadeIn.play();

        } catch (IOException e) {
            e.printStackTrace();
            CustomDialog.show("Error", "Failed to log out!", "error", "OK");
        }
    }

    /**
     * Handles the click event on a VBox option, updating its style and icon.
     *
     * @param event the mouse event
     */
    @FXML
    private void handleClick(MouseEvent event) {
        resetAllBoxes(); // Reset all boxes to default styles

        VBox clickedVBox = (VBox) event.getSource();

        // Set the styles for the clicked box (background, border, etc.)
        clickedVBox.setStyle(
                "-fx-background-color: #eff6ff; -fx-border-color: #2563eb; -fx-border-width: 1; -fx-padding: 15; -fx-border-radius: 10; -fx-background-radius: 10;");

        // Iterate through the children of the clicked VBox to update the Circle and Image styles
        for (Node node : clickedVBox.getChildren()) {
            if (node instanceof StackPane stackPane) {
                for (Node child : stackPane.getChildren()) {
                    if (child instanceof Circle circle) {
                        circle.setFill(Color.web("#bfdbfe")); // Set the circle color to blue
                    } else if (child instanceof ImageView imageView) {
                        String oldUrl = imageView.getImage().getUrl();
                        if (oldUrl.contains(".png")) {
                            String blueUrl = oldUrl.replace(".png", "_blue.png");
                            imageView.setImage(new Image(blueUrl)); // Change the image to the "_blue" version
                        }
                    }
                }
            }
        }

        // Update the label's style for the clicked box (bold and blue text)
        for (Node node : clickedVBox.getChildren()) {
            if (node instanceof Label label) {
                label.setStyle("-fx-font-size: 12px; -fx-font-weight: bold; -fx-text-fill: #2563eb;");
            }
        }

        // Save the selected label (if needed for further logic)
        for (Node node : clickedVBox.getChildren()) {
            if (node instanceof Label label) {
                Label selectedLabel = label;
            }
        }
    }

    /**
     * Resets all option boxes to their default styles.
     */
    private void resetAllBoxes() {
        List<VBox> allBoxes = List.of(vbox1, vbox2, vbox3);

        // Reset all boxes to the default style
        for (VBox box : allBoxes) {
            box.setStyle(
                    "-fx-background-color: white; -fx-border-color: #e5e7eb; -fx-border-radius: 10; -fx-background-radius: 10; -fx-padding: 15;");

            // Reset the styles of the label (only reset color and font-weight)
            for (Node node : box.getChildren()) {
                if (node instanceof Label label) {
                    label.setStyle("-fx-font-size: 12px; -fx-font-weight: normal; -fx-text-fill: #6b7280;");
                }
            }

            // Reset Circle and Image colors for all boxes
            for (Node node : box.getChildren()) {
                if (node instanceof StackPane stackPane) {
                    for (Node child : stackPane.getChildren()) {
                        if (child instanceof Circle circle) {
                            circle.setFill(Color.web("#f3f4f6")); // Default circle color (gray-100)
                        } else if (child instanceof ImageView imageView) {
                            String oldUrl = imageView.getImage().getUrl();
                            if (oldUrl.contains("_blue.png")) {
                                String originalUrl = oldUrl.replace("_blue.png", ".png");
                                imageView.setImage(new Image(originalUrl)); // Restore original image
                            }
                        }
                    }
                }
            }
        }
    }
}