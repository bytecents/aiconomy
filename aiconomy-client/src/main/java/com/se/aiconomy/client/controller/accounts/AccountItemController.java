package com.se.aiconomy.client.controller.accounts;

import com.se.aiconomy.client.controller.BaseController;
import com.se.aiconomy.server.model.entity.Account;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import lombok.Getter;
import lombok.Setter;

import java.io.IOException;

public class AccountItemController extends BaseController {
    @Setter
    @Getter
    private AccountsController accountsController;
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
    @FXML
    private Button updateButton;
    @FXML
    private StackPane rootPane;
    @Getter
    @Setter
    private Account account;

    @FXML
    private void onUpdateButtonClicked(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/accounts/update_accounts.fxml"));
            Parent dialogContent = loader.load();
            UpdateAccountController controller = loader.getController();
            rootPane = accountsController.getRootPane();
            controller.setRootPane(rootPane);
            controller.setUserInfo(this.userInfo);
            controller.setAccount(this.account);
            controller.setAccountsController(accountsController);
            // 设置弹窗样式（你可以在 FXML 里设也行）
            dialogContent.setStyle("-fx-background-color: white; -fx-background-radius: 10; -fx-padding: 20;");

            // 创建遮罩
            Region overlay = new Region();
            overlay.setStyle("-fx-background-color: rgba(0,0,0,0.5);");
            overlay.setPrefSize(rootPane.getWidth(), rootPane.getHeight());

            // 弹窗容器（居中）
            StackPane dialogWrapper = new StackPane(dialogContent);
            dialogWrapper.setMaxWidth(500);
            dialogWrapper.setMaxHeight(600);

            // 点击遮罩关闭弹窗
            overlay.setOnMouseClicked((MouseEvent e) -> {
                rootPane.getChildren().removeAll(overlay, dialogWrapper);
            });

            rootPane.getChildren().addAll(overlay, dialogWrapper);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

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
        accountTypeLabel.setText(accountType);

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
