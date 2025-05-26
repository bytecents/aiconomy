package com.se.aiconomy.client.controller.transactions;

import com.se.aiconomy.client.common.CustomDialog;
import com.se.aiconomy.client.controller.BaseController;
import com.se.aiconomy.server.common.exception.ServiceException;
import com.se.aiconomy.server.handler.AccountRequestHandler;
import com.se.aiconomy.server.handler.TransactionRequestHandler;
import com.se.aiconomy.server.model.dto.account.request.GetAccountsByUserIdRequest;
import com.se.aiconomy.server.model.entity.Account;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
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
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.function.UnaryOperator;

/**
 * Controller for adding a new transaction.
 * Handles user input, category selection, and transaction saving logic.
 */
@Setter
public class AddTransactionController extends BaseController implements Initializable {

    /**
     * VBox for Food & Dining category.
     */
    @FXML
    private VBox category1;
    /**
     * VBox for Transportation category.
     */
    @FXML
    private VBox category2;
    /**
     * VBox for Shopping category.
     */
    @FXML
    private VBox category3;
    /**
     * VBox for Housing category.
     */
    @FXML
    private VBox category4;
    /**
     * VBox for Education category.
     */
    @FXML
    private VBox category5;
    /**
     * VBox for Travel category.
     */
    @FXML
    private VBox category6;
    /**
     * VBox for Gifts category.
     */
    @FXML
    private VBox category7;
    /**
     * VBox for Groceries category.
     */
    @FXML
    private VBox category8;
    /**
     * Map of category names to their corresponding VBox.
     */
    @FXML
    private Map<String, VBox> categoryList = new java.util.HashMap<>();

    /**
     * Panel containing all categories.
     */
    @FXML
    private VBox categoryPanel;
    /**
     * ComboBox for selecting an account.
     */
    @FXML
    private ComboBox<String> accountComboBox;
    /**
     * TextField for transaction description.
     */
    @FXML
    private TextField descriptionInput;
    /**
     * DatePicker for selecting the transaction date.
     */
    @FXML
    private DatePicker datePicker;
    /**
     * TextField for entering the transaction amount.
     */
    @FXML
    private TextField amountInput;
    /**
     * Button to select income type.
     */
    @FXML
    private Button incomeBtn;
    /**
     * Button to select expense type.
     */
    @FXML
    private Button expenseBtn;
    /**
     * Root pane for dialogs and overlays.
     */
    @FXML
    private StackPane rootPane;

    /**
     * Handler for account-related requests.
     */
    private AccountRequestHandler accountHandler = new AccountRequestHandler();
    /**
     * Handler for transaction-related requests.
     */
    private TransactionRequestHandler handler = new TransactionRequestHandler();
    /**
     * Flag indicating if the transaction is an expense.
     */
    private boolean isExpense = true;
    /**
     * The currently chosen category.
     */
    private String chosenCategory;
    /**
     * Reference to the parent controller.
     */
    private BaseController parentController;
    /**
     * Listener for close events.
     */
    private AddTransactionController.OnCloseListener closeListener;

    /**
     * Initializes the controller and UI components.
     * Loads user accounts and sets up category selection.
     *
     * @param location  the location used to resolve relative paths for the root object, or null if unknown
     * @param resources the resources used to localize the root object, or null if not localized
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
     * Switches the transaction type to expense.
     * Updates button styles and category availability.
     */
    @FXML
    public void switchToExpense() {
        isExpense = true;
        expenseBtn.getStyleClass().remove("inactive");
        incomeBtn.getStyleClass().remove("Income");
        incomeBtn.getStyleClass().add("inactive");
        expenseBtn.getStyleClass().add("Expense");
        refreshDisableCategory();
    }

    /**
     * Switches the transaction type to income.
     * Updates button styles and disables categories.
     */
    @FXML
    public void switchToIncome() {
        isExpense = false;
        incomeBtn.getStyleClass().remove("inactive");
        expenseBtn.getStyleClass().remove("Expense");
        expenseBtn.getStyleClass().add("inactive");
        incomeBtn.getStyleClass().add("Income");
        refreshDisableCategory();
    }

    /**
     * Initializes UI components, sets up input filters, loads accounts, and sets default category.
     */
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

    /**
     * Refreshes the category buttons' styles based on the transaction type.
     * Disables categories for income, enables for expense.
     */
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

    /**
     * Selects a category for the transaction.
     * Only available for expense transactions.
     *
     * @param category the category name to select
     */
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

    /**
     * Handles click event for Food &amp; Dining category.
     *
     * @param mouseEvent the mouse event
     */
    @FXML
    public void chooseCategory1(MouseEvent mouseEvent) {
        chooseCategory("Food & Dining");
    }

    /**
     * Handles click event for Transportation category.
     *
     * @param mouseEvent the mouse event
     */
    @FXML
    public void chooseCategory2(MouseEvent mouseEvent) {
        chooseCategory("Transportation");
    }

    /**
     * Handles click event for Shopping category.
     *
     * @param mouseEvent the mouse event
     */
    @FXML
    public void chooseCategory3(MouseEvent mouseEvent) {
        chooseCategory("Shopping");
    }

    /**
     * Handles click event for Housing category.
     *
     * @param mouseEvent the mouse event
     */
    @FXML
    public void chooseCategory4(MouseEvent mouseEvent) {
        chooseCategory("Housing");
    }

    /**
     * Handles click event for Education category.
     *
     * @param mouseEvent the mouse event
     */
    @FXML
    public void chooseCategory5(MouseEvent mouseEvent) {
        chooseCategory("Education");
    }

    /**
     * Handles click event for Travel category.
     *
     * @param mouseEvent the mouse event
     */
    @FXML
    public void chooseCategory6(MouseEvent mouseEvent) {
        chooseCategory("Travel");
    }

    /**
     * Handles click event for Gifts category.
     *
     * @param mouseEvent the mouse event
     */
    @FXML
    public void chooseCategory7(MouseEvent mouseEvent) {
        chooseCategory("Gifts");
    }

    /**
     * Handles click event for Groceries category.
     *
     * @param mouseEvent the mouse event
     */
    @FXML
    public void chooseCategory8(MouseEvent mouseEvent) {
        chooseCategory("Groceries");
    }

    /**
     * Handles account selection event.
     *
     * @param actionEvent the action event
     */
    public void onAccountSelected(ActionEvent actionEvent) {
    }

    /**
     * Sets the listener for the close event.
     *
     * @param listener the listener to set
     */
    @FXML
    public void setOnCloseListener(AddTransactionController.OnCloseListener listener) {
        this.closeListener = listener;
    }

    /**
     * Closes the add transaction panel and notifies the listener.
     */
    public void closeAddTransaction() {
        if (closeListener != null) {
            closeListener.onCloseAddTransactionPanel();
        }
    }

    /**
     * Handles the save transaction button click.
     * Validates input and saves the transaction.
     *
     * @param mouseEvent the mouse event
     */
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
        }
        if (parentController instanceof TransactionsController transactionsController) {
            transactionsController.refreshTransactionList();
            System.out.println("Transaction refreshed.");
        }
        closeAddTransaction();
    }

    /**
     * Listener interface for close events.
     */
    public interface OnCloseListener {
        /**
         * Called when the add transaction panel is closed.
         */
        void onCloseAddTransactionPanel();
    }
}