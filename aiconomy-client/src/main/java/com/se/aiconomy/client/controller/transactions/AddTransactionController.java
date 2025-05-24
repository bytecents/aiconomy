package com.se.aiconomy.client.controller.transactions;

import com.se.aiconomy.client.common.CustomDialog;
import com.se.aiconomy.client.common.MyFXMLLoader;
import com.se.aiconomy.client.controller.BaseController;
import com.se.aiconomy.server.common.exception.ServiceException;
import com.se.aiconomy.server.handler.AccountRequestHandler;
import com.se.aiconomy.server.handler.TransactionRequestHandler;
import com.se.aiconomy.server.langchain.common.model.DynamicBillType;
import com.se.aiconomy.server.langchain.common.model.Transaction;
import com.se.aiconomy.server.model.dto.TransactionDto;
import com.se.aiconomy.server.model.dto.account.request.GetAccountsByUserIdRequest;
import com.se.aiconomy.server.model.dto.transaction.request.GetTransactionByUserIdRequest;
import com.se.aiconomy.server.model.dto.transaction.request.TransactionImportRequest;
import com.se.aiconomy.server.model.entity.Account;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.util.StringConverter;
import lombok.Setter;

import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.UnaryOperator;

@Setter
public class AddTransactionController extends BaseController implements Initializable {
    @FXML
    private VBox category1;
    @FXML
    private VBox category2;
    @FXML
    private VBox category3;
    @FXML
    private VBox category4;
    @FXML private VBox category5;
    @FXML private VBox category6;
    @FXML private VBox category7;
    @FXML private VBox category8;
    @FXML private Map<String, VBox> categoryList = new java.util.HashMap<>();

    @FXML
    private VBox categoryPanel;
    @FXML
    private ComboBox<String> accountComboBox;
    @FXML
    private TextField descriptionInput;
    @FXML
    private DatePicker datePicker;
    @FXML
    private TextField amountInput;
    @FXML
    private Button incomeBtn;
    @FXML
    private Button expenseBtn;
    @FXML
    private StackPane rootPane;
    private AccountRequestHandler accountHandler = new AccountRequestHandler();
    private TransactionRequestHandler handler = new TransactionRequestHandler();
    private boolean isExpense = true;
    private String chosenCategory;
    private BaseController parentController;
    private AddTransactionController.OnCloseListener closeListener;

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
    public void switchToExpense() {
        isExpense = true;
        expenseBtn.getStyleClass().remove("inactive");
        incomeBtn.getStyleClass().remove("Income");
        incomeBtn.getStyleClass().add("inactive");
        expenseBtn.getStyleClass().add("Expense");
        refreshDisableCategory();
    }

    @FXML
    public void switchToIncome() {
        isExpense = false;
        incomeBtn.getStyleClass().remove("inactive");
        expenseBtn.getStyleClass().remove("Expense");
        expenseBtn.getStyleClass().add("inactive");
        incomeBtn.getStyleClass().add("Income");
        refreshDisableCategory();
    }

    @FXML
    private void init() {
        UnaryOperator<TextFormatter.Change> filter = change -> {
            String newText = change.getControlNewText();
            if (newText.matches("^\\d*(\\.\\d*)?$")) {
                return change;
            }
            return null;
        };
        TextFormatter<String> amountFormatter = new TextFormatter<>(filter);
        amountInput.setTextFormatter(amountFormatter);

        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("MMM d, uuuu", Locale.ENGLISH);
        datePicker.setConverter(new StringConverter<>() {
            @Override
            public String toString(LocalDate date) {
                return (date != null) ? dateFormatter.format(date) : "";
            }

            @Override
            public LocalDate fromString(String string) {
                return (string != null && !string.isEmpty()) ? LocalDate.parse(string, dateFormatter) : null;
            }
        });

        datePicker.setValue(LocalDate.now());
        categoryList.put("Food & Dining", category1);
        categoryList.put("Transportation", category2);
        categoryList.put("Shopping", category3);
        categoryList.put("Housing", category4);
        categoryList.put("Education", category5);
        categoryList.put("Travel", category6);
        categoryList.put("Gifts", category7);
        categoryList.put("Groceries", category8);
        chooseCategory("Food & Dining");

        try {
            GetAccountsByUserIdRequest request = new GetAccountsByUserIdRequest();
            request.setUserId(userInfo.getId());
            List<Account> accounts = accountHandler.handleGetAccountsByUserIdRequest(request);
            for (Account account : accounts) {
                accountComboBox.getItems().add(account.getBankName());
            }
            accountComboBox.getSelectionModel().selectFirst();
        } catch (ServiceException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void refreshDisableCategory() {
        if (!isExpense) {
            for (VBox categoryBtn : categoryList.values()) {
                categoryBtn.getStyleClass().remove("category-chosen");
                categoryBtn.getStyleClass().add("category-disable");
            }
        } else {
            for (VBox categoryBtn : categoryList.values()) {
                categoryBtn.getStyleClass().remove("category-disable");
            }
            if (chosenCategory != null) {
                categoryList.get(chosenCategory).getStyleClass().add("category-chosen");
            }
        }
    }

    @FXML
    private void chooseCategory(String category) {
        if (category.equals(chosenCategory)) {
            return;
        }
        if (!isExpense) {
            return;
        }
        for (VBox categoryBtn : categoryList.values()) {
            categoryBtn.getStyleClass().remove("category-chosen");
        }
        categoryList.get(category).getStyleClass().add("category-chosen");
        chosenCategory = category;
    }

    @FXML
    public void chooseCategory1(MouseEvent mouseEvent) {
        chooseCategory("Food & Dining");
    }

    @FXML
    public void chooseCategory2(MouseEvent mouseEvent) {
        chooseCategory("Transportation");
    }

    @FXML
    public void chooseCategory3(MouseEvent mouseEvent) {
        chooseCategory("Shopping");
    }

    @FXML
    public void chooseCategory4(MouseEvent mouseEvent) {
        chooseCategory("Housing");
    }

    @FXML
    public void chooseCategory5(MouseEvent mouseEvent) {
        chooseCategory("Education");
    }

    @FXML
    public void chooseCategory6(MouseEvent mouseEvent) {
        chooseCategory("Travel");
    }

    @FXML
    public void chooseCategory7(MouseEvent mouseEvent) {
        chooseCategory("Gifts");
    }

    @FXML
    public void chooseCategory8(MouseEvent mouseEvent) {
        chooseCategory("Groceries");
    }

    public void onAccountSelected(ActionEvent actionEvent) {
    }

    @FXML
    public void setOnCloseListener(AddTransactionController.OnCloseListener listener) {
        this.closeListener = listener;
    }

    public void closeAddTransaction() {
        if (closeListener != null) {
            closeListener.onCloseAddTransactionPanel();
        }
    }

    @FXML
    public void handleSaveTransaction(MouseEvent mouseEvent) {
        String description = descriptionInput.getText();
        String amount = amountInput.getText();
        LocalDateTime dateTime = LocalDateTime.of(datePicker.getValue(), LocalTime.of(0, 0));
        String account = accountComboBox.getSelectionModel().getSelectedItem();
        System.out.println(description);
        try {
            handler.handleAddTransactionManually(
                    userInfo.getId(),
                    isExpense ? "Expense" : "Income",
                    amount,
                    dateTime,
                    description,
                    isExpense ? chosenCategory : "None",
                    account,
                    "None"
            );
            System.out.println("Transaction import successful.");

        } catch (ServiceException e) {
            CustomDialog.show("Error", e.getMessage(), "error", "OK");
            return;
//            e.printStackTrace();
        }
        if (parentController instanceof TransactionsController transactionsController) {
            transactionsController.refreshTransactionList();
            System.out.println("Transaction refreshed.");
        }
        closeAddTransaction();
    }

    public interface OnCloseListener {
        void onCloseAddTransactionPanel();
    }
}
