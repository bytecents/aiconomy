package com.se.aiconomy.client.controller.accounts;

import com.se.aiconomy.client.controller.BaseController;
import com.se.aiconomy.server.common.exception.ServiceException;
import com.se.aiconomy.server.handler.AccountRequestHandler;
import com.se.aiconomy.server.model.dto.account.request.GetAccountsByUserIdRequest;
import com.se.aiconomy.server.model.entity.Account;
import com.se.aiconomy.server.service.impl.AccountServiceImpl;
import com.se.aiconomy.server.storage.service.impl.JSONStorageServiceImpl;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import lombok.Getter;
import lombok.Setter;

import java.io.IOException;
import java.util.List;

public class AccountsController extends BaseController {

    private final AccountRequestHandler accountRequestHandler = new AccountRequestHandler(new AccountServiceImpl(JSONStorageServiceImpl.getInstance()));
    private List<Account> accountList;
    @FXML
    private VBox accountListVBox;
    @FXML
    @Setter
    @Getter
    private StackPane rootPane; // 这个是 main.fxml 的最外层 StackPane
    @FXML
    private Label totalBalanceLabel;
    @FXML
    private Label activateAccountsLabel;

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

    private void init() {
        try {
            getBasicData();
            getAccountList();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void refreshRootPane() {
        init();
    }

    public void refresh() {
        init();
    }

    private void getBasicData() throws ServiceException {
        GetAccountsByUserIdRequest getAccountsByUserIdRequest = new GetAccountsByUserIdRequest();
        getAccountsByUserIdRequest.setUserId(userInfo.getId());
        accountList = accountRequestHandler.handleGetAccountsByUserIdRequest(getAccountsByUserIdRequest);
        double totalBalance = 0;
        for (Account account : accountList) {
            totalBalance += account.getBalance();
        }
        totalBalanceLabel.setText("$" + totalBalance);
        activateAccountsLabel.setText(Integer.toString(accountList.size()));
    }

    private void getAccountList() {
        accountListVBox.getChildren().clear();
        if (accountList.isEmpty()) {
            // Create the label and hyperlink
            Label messageLabel = new Label("It seems You don’t have any accounts. Try ");
            Hyperlink createLink = new Hyperlink("creating one");

            messageLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: #6B7280;");
            createLink.setStyle("-fx-font-size: 16px;");

            HBox promptBox = new HBox(messageLabel, createLink);
            promptBox.setSpacing(5);
            promptBox.setAlignment(Pos.CENTER);

            VBox container = new VBox(promptBox);
            container.setAlignment(Pos.CENTER);
            container.setPrefHeight(accountListVBox.getPrefHeight());

            accountListVBox.getChildren().add(container);

            createLink.setOnAction(e -> onAddAccountClick(null));
        } else {
            for (Account account : accountList) {
                addAccountItem(account);
            }
        }

    }

    @FXML
    public void onAddAccountClick(ActionEvent event) {
        try {
            // 加载 add_budget.fxml
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/accounts/add_accounts.fxml"));
            Parent dialogContent = loader.load();
            AddAccountController controller = loader.getController();
            controller.setRootPane(rootPane);
            controller.setUserInfo(this.userInfo);
            controller.setAccountsController(this);
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

            // 添加遮罩和弹窗到页面顶层
            rootPane.getChildren().addAll(overlay, dialogWrapper);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void addAccountItem(Account account) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/accounts/account_item.fxml"));
            Parent node = loader.load();
            AccountItemController controller = loader.getController();
            controller.setAccountsController(this);
            controller.setUserInfo(this.userInfo);
            controller.setAccount(account);
            String balanceColor = "Credit".equals(account.getAccountType()) ? "#DC2626" : "#3B82F6";
            String iconPath;
            String circleColor;

            switch (account.getAccountType()) {
                case "Saving":
                    iconPath = "assets/eng_bank.png";
                    circleColor = "#fca5a5";
                    break;
                case "Checking":
                    iconPath = "assets/dash_bank.png";
                    circleColor = "#dbeafe";
                    break;
                case "Credit":
                    iconPath = "assets/well_fargo.png";
                    circleColor = "#dcfce7";
                    break;
                default:
                    iconPath = "assets/logo.png";
                    circleColor = "#e0e7ff";
                    break;
            }

            String bankName = getOrNull(account.getBankName());
            String accountName = getOrNull(account.getAccountName());
            String accountType = getOrNull(account.getAccountType());
            String balance = formatCurrency(account.getBalance());
            String creditLimit = formatCurrency(account.getCreditLimit());
            String currentDebt = formatCurrency(account.getCurrentDebt());
            String paymentDueDate = account.getPaymentDueDate() != null
                    ? account.getPaymentDueDate().toLocalDate().toString()
                    : "not set";
            String minimumPayment = formatCurrency(account.getMinimumPayment());

            controller.setAccountData(
                    bankName,
                    accountName,
                    accountType,
                    balance,
                    creditLimit,
                    currentDebt,
                    paymentDueDate,
                    minimumPayment,
                    "Connected",
                    balanceColor,
                    iconPath,
                    circleColor
            );

            accountListVBox.getChildren().add(node);
            accountListVBox.getChildren().add(new Separator());

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String getOrNull(String val) {
        return (val == null || val.isBlank()) ? null : val;
    }

    private String formatCurrency(Double val) {
        return val == null ? null : "$" + String.format("%,.2f", val);
    }
}
