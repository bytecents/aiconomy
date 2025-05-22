package com.se.aiconomy.client.controller.accounts;

import com.se.aiconomy.client.controller.AddAccountController;
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
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.util.List;

public class AccountsController extends BaseController {

    private final AccountRequestHandler accountRequestHandler = new AccountRequestHandler(new AccountServiceImpl(JSONStorageServiceImpl.getInstance()));
    private List<Account> accountList;
    @FXML
    private VBox accountListVBox;
    @FXML
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
        for (Account account : accountList) {
            addAccountItem(account.getBankName(), account.getAccountType(), "$" + account.getBalance(), "Connected", "#3B82F6", "assets/well_fargo.png", "#DCFCE7");
        }
    }

    @FXML
    public void onAddAccountClick(ActionEvent event) {
        try {
            // 加载 add_budget.fxml
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/accounts/add_accounts.fxml"));
            Parent dialogContent = loader.load();
            // 获取 controller 并传入 rootPane
            AddAccountController controller = loader.getController();
            controller.setRootPane(rootPane); // ⚠️这里的 rootPane 是你的页面最外层 StackPane

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

            // 添加遮罩和弹窗到页面顶层
            rootPane.getChildren().addAll(overlay, dialogWrapper);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void addAccountItem(String bankName, String accountInfo, String balance, String status, String balanceColor, String iconPath, String circleColor) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/accounts/account_item.fxml"));
            Parent node = loader.load();
            AccountItemController controller = loader.getController();
            controller.setAccountData(bankName, accountInfo, balance, status, balanceColor, iconPath, circleColor);
            accountListVBox.getChildren().add(node);
            accountListVBox.getChildren().add(new Separator());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
