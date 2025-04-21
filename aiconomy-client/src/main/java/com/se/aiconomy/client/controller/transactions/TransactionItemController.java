package com.se.aiconomy.client.controller.transactions;

import com.se.aiconomy.server.langchain.common.model.Transaction;
import com.se.aiconomy.server.model.dto.TransactionDto;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;

import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class TransactionItemController {
    @FXML private Label accountLabel;
    @FXML private ComboBox<String> categoryCombobox;
    @FXML private Label amountLabel;
    @FXML private Label descriptionLabel;
//    @FXML private Label remarkLabel;
    @FXML private Label dateLabel;

    @FXML
    public void setTransaction(TransactionDto transaction) {
        String account = transaction.getAccountId();
        boolean isExpense = transaction.getIncomeOrExpense().equals("expense");
        String amount = (isExpense ? "-" : "+") + "$" + transaction.getAmount();
        String description = transaction.getProduct();
//        String remark = transaction.getRemark();
        String category = transaction.getType();
        String date = transaction.getTime().toLocalDate().format(DateTimeFormatter.ofPattern("MMM d, uuuu", Locale.ENGLISH));

        accountLabel.setText(account);
        categoryCombobox.getSelectionModel().select(category);
        descriptionLabel.setText(description);
//        remarkLabel.setText(remark);
        amountLabel.setText(amount);
        dateLabel.setText(date);
    }
}
