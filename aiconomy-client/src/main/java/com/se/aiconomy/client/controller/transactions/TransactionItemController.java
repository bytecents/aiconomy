package com.se.aiconomy.client.controller.transactions;

import com.se.aiconomy.client.controller.BaseController;
import com.se.aiconomy.server.common.exception.ServiceException;
import com.se.aiconomy.server.handler.AccountRequestHandler;
import com.se.aiconomy.server.handler.TransactionRequestHandler;
import com.se.aiconomy.server.langchain.common.model.BillType;
import com.se.aiconomy.server.langchain.common.model.Transaction;
import com.se.aiconomy.server.model.dto.TransactionDto;
import com.se.aiconomy.server.model.dto.account.request.GetAccountsByUserIdRequest;
import com.se.aiconomy.server.model.entity.Account;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import lombok.Setter;

import java.net.URL;
import java.text.DecimalFormat;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

public class TransactionItemController extends BaseController implements Initializable {
    @FXML private Label accountLabel;
    @FXML private ComboBox<String> categoryCombobox;
    @FXML private Label amountLabel;
    @FXML private Label descriptionLabel;
    @FXML private Label remarkLabel;
    @FXML private Label dateLabel;
    private final TransactionRequestHandler handler = new TransactionRequestHandler();
    private TransactionDto transaction;
    @Setter
    private TransactionsController parentController;

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

    public void init() {}

    @FXML
    public void setTransaction(TransactionDto transaction) {
        this.transaction = transaction;
        String accountId = transaction.getAccountId();
        boolean isExpense = transaction.getIncomeOrExpense().equalsIgnoreCase("expense") || transaction.getIncomeOrExpense().equals("支出");

        String rawAmount = transaction.getAmount();
        DecimalFormat df = new DecimalFormat("0.00");

        String amount = (isExpense ? "-" : "+") + "$" + df.format(Double.parseDouble(rawAmount));
        String description = transaction.getProduct();
//        String remark = transaction.getRemark();
        String category = transaction.getBillType().getDisplayName();
        String date = transaction.getTime().toLocalDate().format(DateTimeFormatter.ofPattern("MMM d, uuuu", Locale.ENGLISH));
        if (isExpense) {
            amountLabel.getStyleClass().add("text-red-500");
        } else {
            amountLabel.getStyleClass().add("text-green-500");
        }
        accountLabel.setText(accountId);
        descriptionLabel.setText(description);
//        remarkLabel.setText(remark);
        amountLabel.setText(amount);
        dateLabel.setText(date);

        if (isExpense) {
            for (BillType billType : BillType.values()) {
                categoryCombobox.getItems().add(billType.getType());
            }
            categoryCombobox.getSelectionModel().select(category);
        } else {
            categoryCombobox.getItems().add("Income");
            categoryCombobox.getSelectionModel().select("Income");
            categoryCombobox.setDisable(true);
        }
    }

    public void deleteTransaction(MouseEvent mouseEvent) {
        try {
            handler.handleDeleteTransaction(transaction.getId());
            parentController.refreshTransactionList();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void handleCategoryChange(ActionEvent actionEvent) {
//        String selectedCategory = categoryCombobox.getValue();
//        if (selectedCategory != null && !selectedCategory.equals("All Categories")) {
//            BillType billType = BillType.fromType(selectedCategory);
//            transaction.setBillType(billType);
//            try {
//                handler.handleUpdateTransactionStatus(transaction.getId(), transaction.getIncomeOrExpense());
//            } catch (ServiceException e) {
//                e.printStackTrace();
//            }
//        }
    }
}
