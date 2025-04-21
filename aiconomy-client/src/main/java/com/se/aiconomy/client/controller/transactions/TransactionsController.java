package com.se.aiconomy.client.controller.transactions;

import com.se.aiconomy.client.common.MyFXMLLoader;
import com.se.aiconomy.client.controller.BaseController;
import com.se.aiconomy.client.controller.BudgetController;
import com.se.aiconomy.server.handler.TransactionRequestHandler;
import com.se.aiconomy.server.model.dto.TransactionDto;
import com.se.aiconomy.server.model.dto.transaction.request.GetTransactionByUserIdRequest;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class TransactionsController extends BaseController implements Initializable {
    @FXML private VBox transactionItems;
    private final TransactionRequestHandler handler = new TransactionRequestHandler();

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
    public void refreshTransactionList() {
        GetTransactionByUserIdRequest request = new GetTransactionByUserIdRequest();
        request.setUserId(userInfo.getId());
        try {
            List<TransactionDto> transactionsByUserId = handler.handleGetTransactionsByUserId(request);
            transactionItems.getChildren().clear();
            for (TransactionDto transactionDto : transactionsByUserId) {
                if (transactionDto.getProduct().equals("食品杂货")) continue;
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
//        transactionItems.getChildren().clear();
//
//        for (int i = 0; i < 10; i++) {
////            TransactionRowController transactionRow = new TransactionRowController();
////            transactionRow.setTransactionId("Transaction " + (i + 1));
////            transactionRow.setAmount(100 + i * 10);
//            MyFXMLLoader loader = new MyFXMLLoader("/fxml/transactions/transaction-item.fxml");
//            Parent transactionItem = loader.load();
//            TransactionItemController controller = loader.getController();
//            transactionItems.getChildren().add(transactionItem);
//        }
    }
}
