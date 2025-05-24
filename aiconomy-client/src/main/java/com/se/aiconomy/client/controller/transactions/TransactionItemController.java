package com.se.aiconomy.client.controller.transactions;

import com.se.aiconomy.client.controller.BaseController;
import com.se.aiconomy.server.handler.TransactionRequestHandler;
import com.se.aiconomy.server.langchain.common.model.Transaction;
import com.se.aiconomy.server.model.dto.TransactionDto;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import lombok.Setter;

import java.text.DecimalFormat;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class TransactionItemController {
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

    @FXML
    public void setTransaction(TransactionDto transaction) {
        this.transaction = transaction;
        String account = transaction.getAccountId();
        boolean isExpense = transaction.getIncomeOrExpense().equalsIgnoreCase("expense") || transaction.getIncomeOrExpense().equals("支出");

        String rawAmount = transaction.getAmount();
        DecimalFormat df = new DecimalFormat("0.00");

        String amount = (isExpense ? "-" : "+") + "$" + df.format(Double.parseDouble(rawAmount));
        String description = transaction.getProduct();
        String remark = transaction.getRemark();
        String category = transaction.getBillType().getDisplayName();
        String date = transaction.getTime().toLocalDate().format(DateTimeFormatter.ofPattern("MMM d, uuuu", Locale.ENGLISH));
        if (isExpense) {
            amountLabel.getStyleClass().add("text-red-500");
        } else {
            amountLabel.getStyleClass().add("text-green-500");
        }
        accountLabel.setText(account);
        categoryCombobox.getSelectionModel().select(category);
        descriptionLabel.setText(description);
        remarkLabel.setText(remark);
        amountLabel.setText(amount);
        dateLabel.setText(date);
    }

    public void deleteTransaction(MouseEvent mouseEvent) {
        try {
            handler.handleDeleteTransaction(transaction.getId());
            parentController.refreshTransactionList();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
