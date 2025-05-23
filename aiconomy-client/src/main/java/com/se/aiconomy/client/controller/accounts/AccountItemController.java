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
    private Label accountNameLabel;
    @FXML
    private Label balanceLabel;
    @FXML
    private Label creditLimitLabel;
    @FXML
    private Label currentDebtLabel;
    @FXML
    private Label paymentDueDateLabel;
    @FXML
    private Label minimumPaymentLabel;
    @FXML
    private Label statusLabel;
    @FXML
    private ImageView iconImage;
    @FXML
    private Circle iconCircle;

    public void setAccountData(
            String bankName,
            String accountName,
            String accountType,
            String balance,
            String creditLimit,
            String currentDebt,
            String dueDate,
            String minPayment,
            String status,
            String balanceColor,
            String iconPath,
            String circleColor
    ) {
        bankNameLabel.setText(bankName);
        accountNameLabel.setText(accountName);
        accountTypeLabel.setText("Type: " + accountType);

        balanceLabel.setText("Balance: " + balance);
        balanceLabel.setStyle("-fx-text-fill: " + balanceColor);

        creditLimitLabel.setText("Limit: " + creditLimit);
        currentDebtLabel.setText("Debt: " + currentDebt);
        currentDebtLabel.setStyle("-fx-text-fill: #DC2626");

        paymentDueDateLabel.setText("Due: " + dueDate);
        minimumPaymentLabel.setText("Min Pay: " + minPayment);
        statusLabel.setText(status);
        statusLabel.setStyle("-fx-text-fill: #10B981");

        iconImage.setImage(new Image(iconPath));
        iconCircle.setFill(Paint.valueOf(circleColor));
    }
}
