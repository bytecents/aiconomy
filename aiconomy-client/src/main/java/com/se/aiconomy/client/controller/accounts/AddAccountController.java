package com.se.aiconomy.client.controller.accounts;

import com.se.aiconomy.client.controller.BaseController;
import com.se.aiconomy.server.handler.AccountRequestHandler;
import com.se.aiconomy.server.model.dto.account.request.AddAccountsRequest;
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

import java.util.ArrayList;
import java.util.List;

public class AddAccountController extends BaseController {

    private final AccountRequestHandler accountRequestHandler = new AccountRequestHandler(new AccountServiceImpl(JSONStorageServiceImpl.getInstance()));

    public Button saveButton;
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
    @FXML
    @Setter
    @Getter
    private AccountsController accountsController;

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

        // default values
        bankComboBox.setValue(bankComboBox.getItems().getFirst());
        typeComboBox.setValue(typeComboBox.getItems().getFirst());
    }

    @FXML
    private void onCancel(ActionEvent event) {
        closeDialog(event);
    }

    @FXML
    private void onSave(ActionEvent event) {
        // TODO: 保存逻辑
        saveAccount();
        closeDialog(event);
    }

    private void saveAccount() {
        try {
            List<Account> accountList = new ArrayList<>();
            Account account = Account.builder()
                    .userId(userInfo.getId())
                    .bankName(bankComboBox.getValue())
                    .accountType(typeComboBox.getValue())
                    .accountName(accountNameTextField.getText())
                    .balance(Double.parseDouble(balanceTextField.getText()))
                    .build();
            accountList.add(account);
            AddAccountsRequest addAccountsRequest = new AddAccountsRequest();
            addAccountsRequest.setUserId(userInfo.getId());
            addAccountsRequest.setAccounts(accountList);
            accountRequestHandler.handleAddAccountRequest(addAccountsRequest);
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
        accountsController.refresh();
    }


}