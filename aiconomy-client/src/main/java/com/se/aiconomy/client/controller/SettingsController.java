package com.se.aiconomy.client.controller;

import com.se.aiconomy.server.handler.UserRequestHandler;
import com.se.aiconomy.server.model.dto.user.request.UserUpdateRequest;
import com.se.aiconomy.server.service.impl.UserServiceImpl;
import com.se.aiconomy.server.storage.service.impl.JSONStorageServiceImpl;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;

import java.util.List;

public class SettingsController extends BaseController {

    private final UserRequestHandler userRequestHandler = new UserRequestHandler(new UserServiceImpl(JSONStorageServiceImpl.getInstance()));
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

    @FXML
    private void toggleSwitch() {
        isOn = !isOn;
        if (isOn) {
            switchTrack.setFill(Paint.valueOf("#2563eb")); // ON 蓝色
            switchThumb.setTranslateX(12); // 右移
        } else {
            switchTrack.setFill(Paint.valueOf("#ccc")); // OFF 灰色
            switchThumb.setTranslateX(-12); // 左移
        }
    }

    @FXML
    private void toggleAISwitch() {
        isOn = !isOn;
        if (isOn) {
            switchAITrack.setFill(Paint.valueOf("#2563eb")); // ON 蓝色
            switchAIThumb.setTranslateX(12); // 右移
        } else {
            switchAITrack.setFill(Paint.valueOf("#ccc")); // OFF 灰色
            switchAIThumb.setTranslateX(-12); // 左移
        }
    }

    @FXML
    public void onSaveClick(ActionEvent actionEvent) {
        saveChanges();
    }

    private void saveChanges() {
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
        }
    }

    @FXML
    public void initialize() {
        if (userInfo == null) {
            Platform.runLater(() -> {
                // 延迟到事件调度线程中处理
                if (userInfo != null) {
                    init();
                }
            });
        } else {
            init();
        }
    }

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

    @FXML
    private void handleClick(MouseEvent event) {
        resetAllBoxes(); // Reset all boxes to default styles

        VBox clickedVBox = (VBox) event.getSource();

        // Set the styles for the clicked box (background, border, etc.)
        clickedVBox.setStyle(
                "-fx-background-color: #eff6ff; -fx-border-color: #2563eb; -fx-border-width: 1; -fx-padding: 15; -fx-border-radius: 10; -fx-background-radius: 10;");

        // Iterate through the children of the clicked VBox to update the Circle and
        // Image styles
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
                label.setStyle("-fx-font-size: 12px; -fx-font-weight: bold; -fx-text-fill: #2563eb;"); // Set bold and
                // blue for
                // selected box
            }
        }

        // Update Label's style for clicked box
        for (Node node : clickedVBox.getChildren()) {
            if (node instanceof Label label) {
                label.setStyle("-fx-font-size: 12px; -fx-font-weight: bold; -fx-text-fill: #2563eb;");

                // Save the selected label
                Label selectedLabel = label;
            }
        }
    }

    private void resetAllBoxes() {
        List<VBox> allBoxes = List.of(
                vbox1, vbox2, vbox3);

        // Reset all boxes to the default style
        for (VBox box : allBoxes) {
            box.setStyle(
                    "-fx-background-color: white; -fx-border-color: #e5e7eb; -fx-border-radius: 10; -fx-background-radius: 10; -fx-padding: 15;");

            // Reset the styles of the label (only reset color and font-weight)
            for (Node node : box.getChildren()) {
                if (node instanceof Label label) {
                    label.setStyle("-fx-font-size: 12px; -fx-font-weight: normal; -fx-text-fill: #6b7280;"); // Default
                    // label
                    // style
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
