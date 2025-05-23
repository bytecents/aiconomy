package com.se.aiconomy.client.controller.accounts;

import com.se.aiconomy.client.controller.BaseController;
import com.se.aiconomy.server.handler.AccountRequestHandler;
import com.se.aiconomy.server.model.dto.account.request.DeleteAccountRequest;
import com.se.aiconomy.server.model.entity.Account;
import com.se.aiconomy.server.service.impl.AccountServiceImpl;
import com.se.aiconomy.server.storage.service.impl.JSONStorageServiceImpl;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.StackPane;
import lombok.Getter;
import lombok.Setter;

public class UpdateAccountController extends BaseController {

    private final AccountRequestHandler accountRequestHandler = new AccountRequestHandler(new AccountServiceImpl(JSONStorageServiceImpl.getInstance()));
    @FXML
    public Button saveButton;
    @FXML
    public Button deleteButton;
    @Setter
    @Getter
    private AccountsController accountsController;
    @FXML
    private ComboBox<String> bankComboBox;

    @FXML
    private ComboBox<String> typeComboBox;

    @FXML
    private TextField accountNameTextField;

    @FXML
    private TextField balanceTextField;
    /*
     * 只有当FXML中有StackPane并设置fx:id="rootPane"时才需要此方法
     * 当前FXML中无此定义，应注释掉
     */
    @Setter
    @FXML
    private StackPane rootPane;

    @Getter
    @Setter
    private Account account;

    @FXML
    public void initialize() {
        if (userInfo == null) {
            Platform.runLater(() -> {
                if (userInfo != null) {
                    init();
                }
            });
        } else {
            init();
        }
    }

    private void init() {
        bankComboBox.setItems(FXCollections.observableArrayList("Chase", "Bank of America", "Wells Fargo", "Citibank"));
        typeComboBox.setItems(FXCollections.observableArrayList("Checking", "Saving", "Credit", "Investment"));
        // 可选：设置默认值
        bankComboBox.setValue(account.getBankName());
        typeComboBox.setValue(account.getAccountType());
        accountNameTextField.setText(account.getAccountName());
        balanceTextField.setText(Double.toString(account.getBalance()));
    }

    @FXML
    private void onCancel(ActionEvent event) {
        closeDialog(event);
    }

    @FXML
    private void onSave(ActionEvent event) {
        saveAccount();
        closeDialog(event);
    }

    @FXML
    private void onDelete(ActionEvent event) {
        deleteAccount();
        closeDialog(event);
    }

    private void saveAccount() {
        // TODO: Implement saving logic
        try {
            System.out.println("Update TBI");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void deleteAccount() {
        try {
            DeleteAccountRequest deleteAccountRequest = new DeleteAccountRequest();
            deleteAccountRequest.setAccountId(account.getId());
            deleteAccountRequest.setUserId(userInfo.getId());
            accountRequestHandler.handleDeleteAccountRequest(deleteAccountRequest);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void closeDialog(ActionEvent event) {
        if (rootPane != null) {
            rootPane.getChildren().removeIf(node ->
                    node != rootPane.getChildren().get(0) // 保留主页面，移除弹窗和遮罩
            );
        }
        accountsController.refreshRootPane();
    }


}