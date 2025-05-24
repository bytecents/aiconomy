package com.se.aiconomy.client.controller.transactions;

import com.se.aiconomy.client.common.MyFXMLLoader;
import com.se.aiconomy.client.controller.BaseController;
import com.se.aiconomy.server.handler.TransactionRequestHandler;
import com.se.aiconomy.server.langchain.common.model.DynamicBillType;
import com.se.aiconomy.server.langchain.common.model.Transaction;
import com.se.aiconomy.server.model.dto.TransactionDto;
import com.se.aiconomy.server.model.dto.transaction.request.GetTransactionByUserIdRequest;
import com.se.aiconomy.server.model.dto.transaction.request.TransactionClassificationRequest;
import com.se.aiconomy.server.model.dto.transaction.request.TransactionImportRequest;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.*;
import java.net.URL;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

public class TransactionsController extends BaseController implements Initializable {
    private final TransactionRequestHandler handler = new TransactionRequestHandler();
    @FXML
    private VBox transactionItems;
    private TransactionsController.OnOpenListener openListener;

    @FXML
    public void setOnOpenListener(TransactionsController.OnOpenListener listener) {
        this.openListener = listener;
    }

    @FXML
    public void handleAddTransaction() {
        if (openListener != null) {
            openListener.onOpenAddTransactionPanel();
        }
    }

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

    @FXML
    public void refreshTransactionList() {
        GetTransactionByUserIdRequest request = new GetTransactionByUserIdRequest();
        request.setUserId(userInfo.getId());
        try {
            List<TransactionDto> transactionsByUserId = handler.handleGetTransactionsByUserId(request);
            Collections.reverse(transactionsByUserId);
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
                transactionItems.getChildren().add(transactionItem);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void init() {
        refreshTransactionList();
    }

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
        }
    }

    public void downloadCSVTemplate(MouseEvent mouseEvent) {
        downloadTemplate("/assets/transactions.csv", "transaction_template.csv");
    }

    public void downloadJsonTemplate(MouseEvent mouseEvent) {
        downloadTemplate("/assets/transactions.json", "transaction_template.json");
    }

    public void downloadExcelTemplate(MouseEvent mouseEvent) {
        downloadTemplate("/assets/transactions.xlsx", "transaction_template.xlsx");
    }

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

    public interface OnOpenListener {
        void onOpenAddTransactionPanel();
    }
}
