package com.se.aiconomy.client.controller.accounts;

import com.se.aiconomy.client.common.CustomDialog;
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

/**
 * Controller for adding a new account.
 * Handles the logic for the add account dialog, including form validation and saving the account.
 */
public class AddAccountController extends BaseController {

    /**
     * Handler for account-related requests.
     */
    private final AccountRequestHandler accountRequestHandler = new AccountRequestHandler(new AccountServiceImpl(JSONStorageServiceImpl.getInstance()));

    /**
     * Save button in the dialog.
     */
    public Button saveButton;

    /**
     * ComboBox for selecting the bank.
     */
    @FXML
    private ComboBox<String> bankComboBox;

    /**
     * ComboBox for selecting the account type.
     */
    @FXML
    private ComboBox<String> typeComboBox;

    /**
     * TextField for entering the account name.
     */
    @FXML
    private TextField accountNameTextField;

    /**
     * TextField for entering the account balance.
     */
    @FXML
    private TextField balanceTextField;

    /**
     * Root pane for the dialog. Only required if the FXML defines a StackPane with fx:id="rootPane".
     */
    @Setter
    @FXML
    private StackPane rootPane;

    /**
     * Reference to the parent AccountsController.
     */
    @FXML
    @Setter
    @Getter
    private AccountsController accountsController;

    /**
     * Initializes the controller after its root element has been completely processed.
     * If userInfo is not available, initialization is deferred to the JavaFX application thread.
     */
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

    /**
     * Initializes the ComboBoxes with default values.
     */
    private void init() {
        bankComboBox.setItems(FXCollections.observableArrayList("Chase", "Bank of America", "Wells Fargo", "Citibank"));
        typeComboBox.setItems(FXCollections.observableArrayList("Checking", "Saving", "Credit", "Investment"));

        // Set default values
        bankComboBox.setValue(bankComboBox.getItems().getFirst());
        typeComboBox.setValue(typeComboBox.getItems().getFirst());
    }

    /**
     * Handles the cancel action, closing the dialog.
     *
     * @param event the action event triggered by clicking the cancel button
     */
    @FXML
    private void onCancel(ActionEvent event) {
        closeDialog(event);
    }

    /**
     * Handles the save action, validating and saving the account.
     *
     * @param event the action event triggered by clicking the save button
     */
    @FXML
    private void onSave(ActionEvent event) {
        if (!saveAccount()) {
            return;
        }
        closeDialog(event);
    }

    /**
     * Validates the form fields.
     *
     * @return true if the form is valid, false otherwise
     */
    private boolean checkForm() {
        if (accountNameTextField.getText() == null || accountNameTextField.getText().trim().isEmpty()) {
            CustomDialog.show("Error", "Account name cannot be empty!", "error", "Try Again");
            return false;
        }
        String balanceText = balanceTextField.getText();
        if (balanceText == null || balanceText.trim().isEmpty()) {
            CustomDialog.show("Error", "Please input balance amount!", "error", "Try Again");
            return false;
        }

        try {
            double balanceAmount = Double.parseDouble(balanceText.trim());
            if (balanceAmount < 0) {
                CustomDialog.show("Error", "Balance amount must be non-negative!", "error", "Try Again");
                return false;
            }
        } catch (NumberFormatException e) {
            CustomDialog.show("Error", "Balance amount must be a valid number!", "error", "Try Again");
            return false;
        }
        return true;
    }

    /**
     * Saves the account if the form is valid.
     *
     * @return true if the account was saved successfully, false otherwise
     */
    private boolean saveAccount() {
        if (!checkForm()) {
            return false;
        }
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
            CustomDialog.show("Error", e.getMessage(), "error", "Try Again");
        }
        return true;
    }

    /**
     * Closes the dialog and refreshes the parent account list.
     *
     * @param event the action event that triggered the close
     */
    private void closeDialog(ActionEvent event) {
        if (rootPane != null) {
            rootPane.getChildren().removeIf(node ->
                    node != rootPane.getChildren().get(0) // Keep the main page, remove dialog and overlay
            );
        }
        accountsController.refresh();
    }
}