package com.se.aiconomy.client.controller.transactions;

import com.se.aiconomy.client.common.CustomDialog;
import com.se.aiconomy.client.common.MyFXMLLoader;
import com.se.aiconomy.client.controller.BaseController;
import com.se.aiconomy.server.handler.AccountRequestHandler;
import com.se.aiconomy.server.handler.TransactionRequestHandler;
import com.se.aiconomy.server.langchain.common.model.BillType;
import com.se.aiconomy.server.langchain.common.model.DynamicBillType;
import com.se.aiconomy.server.langchain.common.model.Transaction;
import com.se.aiconomy.server.model.dto.TransactionDto;
import com.se.aiconomy.server.model.dto.account.request.GetAccountsByUserIdRequest;
import com.se.aiconomy.server.model.dto.transaction.request.GetTransactionByUserIdRequest;
import com.se.aiconomy.server.model.dto.transaction.request.TransactionClassificationRequest;
import com.se.aiconomy.server.model.dto.transaction.request.TransactionImportRequest;
import com.se.aiconomy.server.model.entity.Account;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.input.InputMethodEvent;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.*;
import java.net.URL;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

/**
 * Controller for managing transactions in the UI.
 * Handles transaction listing, filtering, import/export, and template download.
 */
public class TransactionsController extends BaseController implements Initializable {
    private final TransactionRequestHandler handler = new TransactionRequestHandler();
    private final AccountRequestHandler accountHandler = new AccountRequestHandler();

    @FXML
    private ComboBox<String> categoryCombobox;
    @FXML
    private ComboBox<String> transactionTypeCombobox;
    @FXML
    private VBox transactionItems;
    private TransactionsController.OnOpenListener openListener;
    @FXML
    private TextField searchContent;

    /**
     * Sets the listener for opening the add transaction panel.
     *
     * @param listener the listener to set
     */
    @FXML
    public void setOnOpenListener(TransactionsController.OnOpenListener listener) {
        this.openListener = listener;
    }

    /**
     * Handles the add transaction button click event.
     * Notifies the listener to open the add transaction panel.
     */
    @FXML
    public void handleAddTransaction() {
        if (openListener != null) {
            openListener.onOpenAddTransactionPanel();
        }
    }

    /**
     * Initializes the controller. If userInfo is not available, waits until it is set.
     *
     * @param location  The location used to resolve relative paths for the root object, or null if unknown.
     * @param resources The resources used to localize the root object, or null if not localized.
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        if (userInfo == null) {
            Platform.runLater(() -> {
                if (userInfo != null) {
                    init();
                } else {
                    System.out.println("User info is not available yet.");
                }
            });
        } else {
            init();
        }
    }

    /**
     * Refreshes the transaction list based on current filters and search content.
     */
    @FXML
    public void refreshTransactionList() {
        GetTransactionByUserIdRequest request = new GetTransactionByUserIdRequest();
        request.setUserId(userInfo.getId());
        try {
            List<TransactionDto> transactionsByUserId = handler.handleGetTransactionsByUserId(request);
            Collections.reverse(transactionsByUserId);
            String search = searchContent.getText();
            transactionsByUserId = transactionsByUserId.stream()
                    .filter(transaction -> transaction.getProduct().toLowerCase().contains(search.toLowerCase())).toList();
            if (categoryCombobox.getValue() != null && !categoryCombobox.getValue().equals("All Categories")) {
                transactionsByUserId = transactionsByUserId.stream()
                        .filter(transaction -> transaction.getBillType().getDisplayName().equals(categoryCombobox.getValue())).toList();
            }
            if (transactionTypeCombobox.getValue() != null && !transactionTypeCombobox.getValue().equals("All Accounts")) {
                transactionsByUserId = transactionsByUserId.stream()
                        .filter(transaction -> transaction.getAccountId().equals(transactionTypeCombobox.getValue())).toList();
            }
            transactionItems.getChildren().clear();
            for (TransactionDto transactionDto : transactionsByUserId) {
                if (transactionDto.getProduct() == null) {
                    System.out.println("Skip problem transaction: " + transactionDto);
                    continue;
                }
                MyFXMLLoader loader = new MyFXMLLoader("/fxml/transactions/transaction-item.fxml");
                Parent transactionItem = loader.load();
                TransactionItemController controller = loader.getController();
                controller.setTransaction(transactionDto);
                controller.setParentController(this);
                controller.setUserInfo(userInfo);
                transactionItems.getChildren().add(transactionItem);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Initializes UI components and loads account/category data.
     */
    @FXML
    private void init() {
        categoryCombobox.getItems().add("All Categories");
        for (BillType billType : BillType.values()) {
            categoryCombobox.getItems().add(billType.getType());
        }
        transactionTypeCombobox.getItems().add("All Accounts");
        GetAccountsByUserIdRequest request = new GetAccountsByUserIdRequest();
        request.setUserId(userInfo.getId());
        try {
            List<Account> accounts = accountHandler.handleGetAccountsByUserIdRequest(request);
            for (Account account : accounts) {
                transactionTypeCombobox.getItems().add(account.getBankName());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        refreshTransactionList();
    }

    /**
     * Downloads a template file for transaction import.
     *
     * @param templatePath the resource path of the template
     * @param fileName     the default file name for saving
     */
    private void downloadTemplate(String templatePath, String fileName) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Download Template");
        if (templatePath.endsWith(".csv")) {
            fileChooser.getExtensionFilters().add(
                    new FileChooser.ExtensionFilter("CSV", "*.csv")
            );
        } else if (templatePath.endsWith(".json")) {
            fileChooser.getExtensionFilters().add(
                    new FileChooser.ExtensionFilter("JSON", "*.json")
            );
        } else if (templatePath.endsWith(".xlsx")) {
            fileChooser.getExtensionFilters().add(
                    new FileChooser.ExtensionFilter("Excel", "*.xlsx")
            );
        }
        fileChooser.setInitialFileName(fileName);
        Stage stage = (Stage) transactionItems.getScene().getWindow();
        File file = fileChooser.showSaveDialog(stage);
        if (file != null) {
            try (InputStream in = getClass().getResourceAsStream(templatePath);
                 OutputStream out = new FileOutputStream(file)) {
                if (in == null) {
                    System.out.println("The template file does not exist.");
                    return;
                }
                byte[] buffer = new byte[4096];
                int bytesRead;
                while ((bytesRead = in.read(buffer)) != -1) {
                    out.write(buffer, 0, bytesRead);
                }
                System.out.println("Download Success" + file.getAbsolutePath());
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        } else {
            System.out.println("Download canceled.");
        }
    }

    /**
     * Handles the download CSV template button click.
     *
     * @param mouseEvent the mouse event
     */
    public void downloadCSVTemplate(MouseEvent mouseEvent) {
        downloadTemplate("/assets/transactions.csv", "transaction_template.csv");
    }

    /**
     * Handles the download JSON template button click.
     *
     * @param mouseEvent the mouse event
     */
    public void downloadJsonTemplate(MouseEvent mouseEvent) {
        downloadTemplate("/assets/transactions.json", "transaction_template.json");
    }

    /**
     * Handles the download Excel template button click.
     *
     * @param mouseEvent the mouse event
     */
    public void downloadExcelTemplate(MouseEvent mouseEvent) {
        downloadTemplate("/assets/transactions.xlsx", "transaction_template.xlsx");
    }

    /**
     * Handles the import transaction file button click.
     * Allows the user to select a file and imports transactions.
     *
     * @param mouseEvent the mouse event
     */
    public void importTransactionFile(MouseEvent mouseEvent) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Import Transaction File");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter(
                        "Support File (CSV, JSON, Excel)",
                        "*.csv", "*.json", "*.xlsx"
                )
        );
        Stage stage = (Stage) transactionItems.getScene().getWindow();
        File file = fileChooser.showOpenDialog(stage);
        if (file != null) {
            try {
                TransactionClassificationRequest request = new TransactionClassificationRequest();
                request.setUserId(userInfo.getId());
                request.setFilePath(file.getAbsolutePath());

                List<Map<Transaction, DynamicBillType>> result = handler.handleTransactionClassificationRequest(request);

                TransactionImportRequest request1 = new TransactionImportRequest();
                request1.setUserId(userInfo.getId());
                request1.setAccountId("Chase Bank");
                request1.setTransactions(result);
                handler.handleTransactionImportRequest(request1);

                refreshTransactionList();
                System.out.println("Transaction import successful.");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Handles the search transaction event (e.g., on key press).
     *
     * @param keyEvent the key event
     */
    public void searchTransaction(KeyEvent keyEvent) {
        refreshTransactionList();
    }

    /**
     * Handles the category filter change event.
     *
     * @param actionEvent the action event
     */
    public void handleCategory(ActionEvent actionEvent) {
        refreshTransactionList();
    }

    /**
     * Handles the transaction type (account) filter change event.
     *
     * @param actionEvent the action event
     */
    public void handleTransactionType(ActionEvent actionEvent) {
        refreshTransactionList();
    }

    /**
     * Handles the export transaction button click.
     * Allows the user to select a directory and exports transactions to an Excel file.
     *
     * @param actionEvent the action event
     */
    public void handleExportTransaction(ActionEvent actionEvent) {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Export Transaction File");

        Stage stage = (Stage) transactionItems.getScene().getWindow();
        File directory = directoryChooser.showDialog(stage);
        if (directory != null) {
            try {
                String filePath = directory.getAbsolutePath();
                handler.handleExportTransactionsToExcel(filePath + "/transactions.xlsx");
            } catch (Exception e) {
                CustomDialog.show("Error", e.getMessage(), "error", "OK");
                e.printStackTrace();
            }
        }
    }

    /**
     * Listener interface for open events.
     */
    public interface OnOpenListener {
        /**
         * Called when the add transaction panel should be opened.
         */
        void onOpenAddTransactionPanel();
    }
}