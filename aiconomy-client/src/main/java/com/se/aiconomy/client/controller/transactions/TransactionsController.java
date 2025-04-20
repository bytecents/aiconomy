package com.se.aiconomy.client.controller.transactions;

import com.se.aiconomy.client.common.MyFXMLLoader;
import com.se.aiconomy.client.controller.BaseController;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.util.ResourceBundle;

public class TransactionsController extends BaseController implements Initializable {
    @FXML
    private VBox transactionItems;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Initialize the transaction items list
        transactionItems.getChildren().clear();

        // Example: Add a few transaction rows (this should be replaced with actual data)
        for (int i = 0; i < 10; i++) {
//            TransactionRowController transactionRow = new TransactionRowController();
//            transactionRow.setTransactionId("Transaction " + (i + 1));
//            transactionRow.setAmount(100 + i * 10);
            MyFXMLLoader loader = new MyFXMLLoader("/fxml/transactions/transaction-item.fxml");
            Parent transactionItem = loader.load();
            TransactionItemController controller = loader.getController();
            transactionItems.getChildren().add(transactionItem);
        }
    }
}
