package com.se.aiconomy.client.controller.accounts;

import com.se.aiconomy.client.common.CustomDialog;
import com.se.aiconomy.client.controller.BaseController;
import com.se.aiconomy.server.handler.AccountRequestHandler;
import com.se.aiconomy.server.model.dto.account.request.DeleteAccountRequest;
import com.se.aiconomy.server.model.dto.account.request.UpdateAccountRequest;
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

/**
 * Controller for updating or deleting an account.
 * Handles the logic for the update account dialog, including form validation, saving, and deleting the account.
 */
public class UpdateAccountController extends BaseController {

    /**
     * Handler for account-related requests.
     */
    private final AccountRequestHandler accountRequestHandler = new AccountRequestHandler(new AccountServiceImpl(JSONStorageServiceImpl.getInstance()));

    /**
     * Save button in the dialog.
     */
    @FXML
    public Button saveButton;

    /**
     * Delete button in the dialog.
     */
    @FXML
    public Button deleteButton;

    /**
     * Reference to the parent AccountsController.
     */
    @Setter
    @Getter
    private AccountsController accountsController;

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
     * The account being updated.
     */
    @Getter
    @Setter
    private Account account;

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
     * Initializes the ComboBoxes and form fields with the account's current values.
     */
    private void init() {
        bankComboBox.setItems(FXCollections.observableArrayList("Chase", "Bank of America", "Wells Fargo", "Citibank"));
        typeComboBox.setItems(FXCollections.observableArrayList("Checking", "Saving", "Credit", "Investment"));
        bankComboBox.setValue(account.getBankName());
        typeComboBox.setValue(account.getAccountType());
        accountNameTextField.setText(account.getAccountName());
        balanceTextField.setText(Double.toString(account.getBalance()));
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
     * Handles the delete action, deleting the account.
     *
     * @param event the action event triggered by clicking the delete button
     */
    @FXML
    private void onDelete(ActionEvent event) {
        deleteAccount();
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
            UpdateAccountRequest updateAccountRequest = new UpdateAccountRequest();
            Account newAccount = new Account();
            newAccount.setAccountName(accountNameTextField.getText());
            newAccount.setAccountType(typeComboBox.getValue());
            newAccount.setBalance(Double.parseDouble(balanceTextField.getText()));
            newAccount.setBankName(bankComboBox.getValue());
            newAccount.setId(account.getId());
            newAccount.setUserId(userInfo.getId());
            updateAccountRequest.setAccount(newAccount);
            updateAccountRequest.setUserId(userInfo.getId());
            accountRequestHandler.handleUpdateAccountRequest(updateAccountRequest);
        } catch (Exception e) {
            e.printStackTrace();
            CustomDialog.show("Error", e.getMessage(), "error", "Try Again");
        }
        return true;
    }

    /**
     * Deletes the account.
     */
    private void deleteAccount() {
        try {
            DeleteAccountRequest deleteAccountRequest = new DeleteAccountRequest();
            deleteAccountRequest.setAccountId(account.getId());
            deleteAccountRequest.setUserId(userInfo.getId());
            accountRequestHandler.handleDeleteAccountRequest(deleteAccountRequest);
        } catch (Exception e) {
            e.printStackTrace();
            CustomDialog.show("Error", e.getMessage(), "error", "Try Again");
        }
    }

    /**
     * Closes the dialog and refreshes the parent account list.
     *
     * @param event the action event that triggered the close
     */
    private void closeDialog(ActionEvent event) {
        if (rootPane != null) {
            rootPane.getChildren().removeIf(node ->
                    node != rootPane.getChildren().get(0)
            );
        }
        accountsController.refreshRootPane();
    }
}