package com.se.aiconomy.client.controller.accounts;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;

public class AccountItemController {
    @FXML
    private Label bankNameLabel;
    @FXML
    private Label accountTypeLabel;
    @FXML
    private Label balanceLabel;
    @FXML
    private Label statusLabel;
    @FXML
    private ImageView iconImage;
    @FXML
    private Circle iconCircle;

    public void setAccountData(String bankName, String accountInfo, String balance, String status, String balanceColor, String iconPath, String circleColor) {
        bankNameLabel.setText(bankName);
        bankNameLabel.setStyle("-fx-font-weight: bold");
        accountTypeLabel.setText(accountInfo);
        accountTypeLabel.setStyle("-fx-text-fill: #6B7280");
        balanceLabel.setText(balance);
        balanceLabel.setStyle("-fx-font-weight: bold; " + "-fx-text-fill: " + balanceColor + "; ");
        statusLabel.setText(status);
        statusLabel.setStyle("-fx-text-fill: #10B981");
        iconImage.setImage(new Image(iconPath));
        iconCircle.setFill(Paint.valueOf(circleColor));
    }
}