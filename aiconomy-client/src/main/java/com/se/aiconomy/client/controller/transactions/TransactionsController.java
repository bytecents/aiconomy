package com.se.aiconomy.client.controller.transactions;

import com.se.aiconomy.client.common.MyFXMLLoader;
import com.se.aiconomy.client.controller.BaseController;
import com.se.aiconomy.client.controller.BudgetController;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;

import java.net.URL;
import java.util.ResourceBundle;

public class TransactionsController extends BaseController implements Initializable {
    @FXML private VBox transactionItems;

    public interface OnOpenListener {
        void onOpenAddTransactionPanel();
    }

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
    private void init() {
        transactionItems.getChildren().clear();

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
