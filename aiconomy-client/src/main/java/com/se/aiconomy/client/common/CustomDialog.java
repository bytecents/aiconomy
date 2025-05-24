package com.se.aiconomy.client.common;

import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import lombok.Getter;
import lombok.Setter;

import java.util.Objects;

/**
 * CustomDialog provides a static method to display a styled dialog with customizable content and appearance.
 * The dialog supports different types (success, error, info, default) with corresponding icons and colors.
 */
@Getter
@Setter
public class CustomDialog {

    /**
     * Displays a custom dialog with the specified parameters.
     *
     * @param summary    The title of the dialog.
     * @param detail     The detailed message to display in the dialog.
     * @param type       The type of dialog ("success", "error", "info", or any other for default).
     * @param buttonText The text to display on the confirmation button.
     */
    public static void show(String summary, String detail, String type, String buttonText) {
        Dialog<String> dialog = new Dialog<>();
        dialog.setTitle(summary);
        dialog.setContentText(detail);

        HBox contentBox = new HBox();

        Text icon = new Text(); // Icon to display
        Text content = new Text(detail); // Dialog detail text

        Color fontColor;

        // Set icon, background, and font color based on dialog type
        switch (type) {
            case "success":
                icon.setText("✔");
                icon.setFill(Color.GREEN);
                dialog.getDialogPane().setStyle("-fx-background-color: #dcfce7; -fx-border-radius: 15px; -fx-padding: 20px;");
                fontColor = Color.web("#16a34a");
                break;
            case "error":
                icon.setText("✘");
                icon.setFill(Color.RED);
                dialog.getDialogPane().setStyle("-fx-background-color: #fee2e2; -fx-border-radius: 15px; -fx-padding: 20px;");
                fontColor = Color.web("#dc2626");
                break;
            case "info":
                icon.setText("ℹ️");
                icon.setFill(Color.BLUE);
                dialog.getDialogPane().setStyle("-fx-background-color: #dbeafe; -fx-border-radius: 15px; -fx-padding: 20px;");
                fontColor = Color.web("#2563eb");
                break;
            default:
                icon.setText("ℹ️");
                icon.setFill(Color.GRAY);
                dialog.getDialogPane().setStyle("-fx-background-color: #ffffff; -fx-border-radius: 15px; -fx-padding: 20px;");
                fontColor = Color.web("#ffffff");
                break;
        }

        content.setFont(Font.font("System", FontWeight.BOLD, 16));
        content.setFill(fontColor);

        contentBox.getChildren().addAll(icon, content);
        contentBox.setSpacing(10);

        dialog.setWidth(400);
        dialog.setHeight(200);
        dialog.setResizable(true);

        Stage stage = (Stage) dialog.getDialogPane().getScene().getWindow();
        stage.initStyle(StageStyle.DECORATED);

        dialog.getDialogPane().setContent(contentBox);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.OK);
        Button okButton = (Button) dialog.getDialogPane().lookupButton(ButtonType.OK);
        okButton.setText(buttonText);

        dialog.showAndWait();
    }
}
