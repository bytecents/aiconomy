package com.se.aiconomy.client.controller;

import com.se.aiconomy.server.common.exception.ServiceException;
import com.se.aiconomy.server.handler.DashboardRequestHandler;
import com.se.aiconomy.server.model.dto.TransactionDto;
import com.se.aiconomy.server.model.entity.Account;
import com.se.aiconomy.server.service.TransactionService;
import com.se.aiconomy.server.service.impl.AccountServiceImpl;
import com.se.aiconomy.server.service.impl.BudgetServiceImpl;
import com.se.aiconomy.server.service.impl.TransactionServiceImpl;
import com.se.aiconomy.server.storage.service.impl.JSONStorageServiceImpl;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.StringConverter;

import java.io.IOException;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class DashboardController extends BaseController {
    private final DashboardRequestHandler dashBoardRequestHandler = new DashboardRequestHandler(
            new AccountServiceImpl(JSONStorageServiceImpl.getInstance()),
            new TransactionServiceImpl(),
            new BudgetServiceImpl(JSONStorageServiceImpl.getInstance())
    );
    private final Label noTransactionLabel = new Label("You don’t seem to have any transactions yet.");
    private final Hyperlink createTransactionLink = new Hyperlink("Create one");
    private final Label noBudgetLabel = new Label("You don’t seem to have any budgets yet.");
    private final Hyperlink createBudgetLink = new Hyperlink("Create one");
    private final Label noAccountLabel = new Label("You don’t seem to have any accounts yet.");
    private final Hyperlink createAccountLink = new Hyperlink("Create one");
    @FXML
    Button quickAddButton;
    @FXML
    private Label welcomeTextField;
    @FXML
    private Label dateTextField;
    @FXML
    private Label netWorth;
    @FXML
    private Label monthlySpending;
    @FXML
    private Label monthlyIncome;
    @FXML
    private Label creditCardDue;
    @FXML
    private VBox transactionListVBox; // 包含 transactionItem1, item2 或提示内容的容器
    @FXML
    private HBox transactionItem1;
    @FXML
    private HBox transactionItem2;
    @FXML
    private Label transactionTitle1;
    @FXML
    private Label transactionCategory1;
    @FXML
    private Label transactionAmount1;
    @FXML
    private Label transactionTime1;
    @FXML
    private Label transactionTitle2;
    @FXML
    private Label transactionCategory2;
    @FXML
    private Label transactionAmount2;
    @FXML
    private Label transactionTime2;
    @FXML
    private VBox budgetProgressVBox;
    @FXML
    private Label budgetProgressCategoryLabel1;
    @FXML
    private ProgressBar budgetProgressProgressBar1;
    @FXML
    private Label budgetProgressCategoryRate1;
    @FXML
    private Label budgetProgressCategoryLabel2;
    @FXML
    private Label budgetProgressCategoryRate2;
    @FXML
    private ProgressBar budgetProgressProgressBar2;
    @FXML
    private Label budgetProgressCategoryLabel3;
    @FXML
    private Label budgetProgressCategoryRate3;
    @FXML
    private ProgressBar budgetProgressProgressBar3;
    @FXML
    private VBox accountOverviewVBox;
    @FXML
    private HBox accountCard1;
    @FXML
    private HBox accountCard2;
    @FXML
    private Label accountBankName1;
    @FXML
    private Label accountBankName2;
    @FXML
    private Label accountBalance1;
    @FXML
    private Label accountBalance2;
    @FXML
    private Label accountType1;
    @FXML
    private Label accountType2;
    @FXML
    private LineChart<String, Number> spendingTrends;
    @FXML
    private NumberAxis yAxis;

    /**
     * Initializes the dashboard controller. If userInfo is not set, waits for it to be set before initializing.
     */
    @FXML
    void initialize() {
        if (userInfo == null) {
            Platform.runLater(() -> {
                if (userInfo != null) {
                    init();
                }
            });
        } else {
            init();
        }
    }

    /**
     * Initializes dashboard data and UI components.
     */
    private void init() {
        welcomeTextField.setText("Welcome back, " + userInfo.getFirstName() + "!");
        setDate();
        createTransactionLink.setOnAction(event -> {
            try {
                goToTransaction(event);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

        createBudgetLink.setOnAction(event -> {
            try {
                goToBudget(event);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        createAccountLink.setOnAction(event -> {
            try {
                goToAccount(event);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        try {
            setDashboardBasicData();
            setDashBoardTransactionData();
            setDashboardSpendTrendsData();
            setDashBoardBudgetsData();
            setDashboardAccountOverviewData();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * Sets the current date in the date label.
     */
    private void setDate() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEEE, MMMM d, yyyy", Locale.ENGLISH);
        String today = LocalDate.now().format(formatter);
        dateTextField.setText(today);
    }

    /**
     * Loads and displays basic dashboard data such as net worth, spending, income, and credit card due.
     *
     * @throws ServiceException if data retrieval fails
     */
    private void setDashboardBasicData() throws ServiceException {
        DashboardRequestHandler.DashboardData dashboardData = dashBoardRequestHandler.getDashboardData(userInfo.getId(), LocalDate.now().getMonth());
        System.out.println("dashboardData: " + dashboardData);
        netWorth.setText("$ " + dashboardData.getNetWorth());
        monthlySpending.setText("$ " + dashboardData.getMonthlySpending());
        monthlyIncome.setText("$ " + dashboardData.getMonthlyIncome());
        creditCardDue.setText("$ " + dashboardData.getCreditCardDue());
    }

    /**
     * Loads and displays recent transaction data.
     *
     * @throws ServiceException if data retrieval fails
     */
    private void setDashBoardTransactionData() throws ServiceException {
        List<TransactionDto> transactions = new ArrayList<>();

        try {
            TransactionService transactionService = new TransactionServiceImpl();
            transactions = transactionService.getTransactionsByUserId(userInfo.getId());

        } catch (ServiceException e) {
            if (e.getMessage() != null && e.getMessage().contains("No transactions found for user ID")) {
                transactions = new ArrayList<>();
            } else {
                System.out.println(e.getMessage());
                return;
            }
        } finally {
            transactionListVBox.getChildren().clear();
            if (transactions.isEmpty()) {
                VBox emptyBox = new VBox(5, noTransactionLabel, createTransactionLink);
                emptyBox.setAlignment(Pos.CENTER_LEFT);
                transactionListVBox.getChildren().add(emptyBox);
                return;
            }

            HBox[] items = {transactionItem1, transactionItem2};
            Label[][] labels = {
                    {transactionTitle1, transactionCategory1, transactionAmount1, transactionTime1},
                    {transactionTitle2, transactionCategory2, transactionAmount2, transactionTime2}
            };

            int index = 0;
            for (TransactionDto tx : transactions) {
                if (index >= 2) break;

                String title = tx.getProduct();
                String category = tx.getBillType().getDisplayName();
                String time = formatTime(tx.getTime());
                String amount = (tx.getIncomeOrExpense().equalsIgnoreCase("Income") ? "+" : "-") + "$" + tx.getAmount();
                String amountStyle = tx.getIncomeOrExpense().equalsIgnoreCase("Income") ? "text-green-500" : "text-red-500";

                labels[index][0].setText(title);
                labels[index][1].setText(category);
                labels[index][2].setText(amount);
                labels[index][2].getStyleClass().setAll("font-medium", amountStyle, "text-base");
                labels[index][3].setText(time);

                items[index].setVisible(true);
                transactionListVBox.getChildren().add(items[index]); // 重新添加可见项
                index++;
            }

            for (int i = index; i < 2; i++) {
                items[i].setVisible(false);
            }
        }
    }

    /**
     * Loads and displays spending trends data.
     *
     * @throws ServiceException if data retrieval fails
     */
    private void setDashboardSpendTrendsData() {
        spendingTrends.setLegendVisible(false);

        yAxis.setTickLabelFormatter(new StringConverter<Number>() {
            @Override
            public String toString(Number object) {
                return "$" + object.intValue();
            }

            @Override
            public Number fromString(String string) {
                return Integer.parseInt(string.replace("$", ""));
            }
        });

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Expenses");
        series.getData().add(new XYChart.Data<>("1", 500));
        series.getData().add(new XYChart.Data<>("5", 800));
        series.getData().add(new XYChart.Data<>("10", 600));
        series.getData().add(new XYChart.Data<>("15", 1200));
        series.getData().add(new XYChart.Data<>("20", 800));
        series.getData().add(new XYChart.Data<>("25", 950));
        series.getData().add(new XYChart.Data<>("30", 700));

        spendingTrends.getData().add(series);
    }

    /**
     * Loads and displays budget progress for up to three categories.
     *
     * @throws ServiceException if data retrieval fails
     */

    private void setDashBoardBudgetsData() {
        Map<String, Double> budgetSpendingRatio;

        try {
            budgetSpendingRatio = dashBoardRequestHandler.getBudgetSpendingRatio(userInfo.getId());
        } catch (ServiceException e) {
            if (e.getMessage() != null && e.getMessage().contains("No budgets found")) {
                budgetSpendingRatio = new HashMap<>();
            } else {
                System.out.println(e.getMessage());
                return;
            }
        }
        Label[] labels = {
                budgetProgressCategoryLabel1,
                budgetProgressCategoryLabel2,
                budgetProgressCategoryLabel3
        };
        Label[] rates = {
                budgetProgressCategoryRate1,
                budgetProgressCategoryRate2,
                budgetProgressCategoryRate3
        };
        ProgressBar[] bars = {
                budgetProgressProgressBar1,
                budgetProgressProgressBar2,
                budgetProgressProgressBar3
        };

        if (budgetSpendingRatio.isEmpty()) {
            budgetProgressVBox.getChildren().clear();

            VBox emptyBox = new VBox(5, noBudgetLabel, createBudgetLink);
            emptyBox.setAlignment(Pos.CENTER_LEFT);
            budgetProgressVBox.getChildren().add(emptyBox);
            return;
        }

        int index = 0;
        for (Map.Entry<String, Double> entry : budgetSpendingRatio.entrySet()) {
            if (index >= 3) break;

            labels[index].setText(entry.getKey());
            adjustRatioText(rates[index], entry.getValue());
            adjustProgressBar(bars[index], entry.getValue());

            labels[index].setVisible(true);
            rates[index].setVisible(true);
            bars[index].setVisible(true);
            index++;
        }

        // 隐藏未使用的项
        for (int i = index; i < 3; i++) {
            labels[i].setVisible(false);
            rates[i].setVisible(false);
            bars[i].setVisible(false);
        }
    }

    /**
     * Loads and displays account overview for up to two accounts.
     *
     * @throws ServiceException if data retrieval fails
     */
    private void setDashboardAccountOverviewData() {
        List<Account> userAccount = new ArrayList<>();

        try {
            userAccount = dashBoardRequestHandler.getAccountsForUser(userInfo.getId());
        } catch (ServiceException e) {
            if (e.getMessage() != null && e.getMessage().contains("No accounts found for user ID")) {
                userAccount = new ArrayList<>(); // 用空列表继续流程
            } else {
                System.out.println(e.getMessage());
                return;
            }
        } finally {
            accountOverviewVBox.getChildren().clear(); // 清空所有旧数据

            if (userAccount.isEmpty()) {
                VBox emptyBox = new VBox(5, noAccountLabel, createAccountLink);
                emptyBox.setAlignment(Pos.CENTER_LEFT);
                accountOverviewVBox.getChildren().add(emptyBox);
                return;
            }

            Label[] bankNames = {accountBankName1, accountBankName2};
            Label[] types = {accountType1, accountType2};
            Label[] balances = {accountBalance1, accountBalance2};
            HBox[] cards = {accountCard1, accountCard2}; // 每个账户卡片（外层 HBox）

            int count = 0;
            for (Account account : userAccount) {
                if (count >= 2) break;

                bankNames[count].setText(account.getBankName());
                types[count].setText(account.getAccountType());
                balances[count].setText("$ " + account.getBalance());

                cards[count].setVisible(true);
                accountOverviewVBox.getChildren().add(cards[count]);

                count++;
            }

            // 隐藏多余账户卡
            for (int i = count; i < 2; i++) {
                cards[i].setVisible(false);
            }
        }
    }

    /**
     * Adjusts the style and text of a label based on the budget ratio.
     *
     * @param label the label to update
     * @param ratio the budget ratio
     */
    private void adjustRatioText(Label label, double ratio) {
        if (label != null) {
            String redTextClass = "text-red-500";
            String greenTextClass = "text-green-500";
            String yellowTextClass = "text-yellow-500";
            String targetTextClass;
            DecimalFormat decimalFormat = new DecimalFormat("##.##%");
            String percentage = decimalFormat.format(ratio);
            if (0 <= ratio && ratio < 0.33) {
                targetTextClass = greenTextClass;
            } else if (ratio >= 0.33 && ratio < 0.67) {
                targetTextClass = yellowTextClass;
            } else if (ratio >= 0.67) {
                targetTextClass = redTextClass;
            } else {
                targetTextClass = redTextClass;
            }
            ObservableList<String> styleClasses = label.getStyleClass();
            styleClasses.removeAll(redTextClass, greenTextClass, yellowTextClass);
            if (!styleClasses.contains(targetTextClass)) {
                styleClasses.add(targetTextClass);
            }
            label.setText(percentage);
        }
    }

    /**
     * Adjusts the style and progress of a progress bar based on the budget ratio.
     *
     * @param progressBar the progress bar to update
     * @param ratio       the budget ratio
     */
    private void adjustProgressBar(ProgressBar progressBar, double ratio) {
        if (progressBar != null) {
            String redClass = "red-bar";
            String greenClass = "green-bar";
            String yellowClass = "yellow-bar";

            String targetClass;
            if (0 <= ratio && ratio < 0.33) {
                targetClass = greenClass;
            } else if (ratio >= 0.33 && ratio < 0.67) {
                targetClass = yellowClass;
            } else if (ratio >= 0.67) {
                targetClass = redClass;
            } else {
                targetClass = redClass;
            }

            ObservableList<String> styleClasses = progressBar.getStyleClass();
            styleClasses.removeAll(redClass, greenClass, yellowClass);
            if (!styleClasses.contains(targetClass)) {
                styleClasses.add(targetClass);
            }
            double progress = ratio;
            if (ratio < 0) progress = 0;
            if (ratio > 1) progress = 1;
            progressBar.setProgress(progress);
        }
    }

    /**
     * Handles the quick add button action, navigating to the transaction page.
     *
     * @param actionEvent the action event
     * @throws IOException if navigation fails
     */
    @FXML
    public void quickAdd(ActionEvent actionEvent) throws IOException {
        goToTransaction(actionEvent);
    }

    /**
     * Navigates to the transactions page.
     *
     * @param actionEvent the action event
     * @throws IOException if navigation fails
     */
    public void goToTransaction(ActionEvent actionEvent) throws IOException {
        mainController.switchToTransactions();
    }

    /**
     * Navigates to the budgets page.
     *
     * @param actionEvent the action event
     * @throws IOException if navigation fails
     */
    public void goToBudget(ActionEvent actionEvent) throws IOException {
        mainController.switchToBudgets();
    }

    /**
     * Navigates to the accounts page.
     *
     * @param actionEvent the action event
     * @throws IOException if navigation fails
     */
    public void goToAccount(ActionEvent actionEvent) throws IOException {
        mainController.switchToAccounts();
    }

    /**
     * Navigates to the settings page.
     *
     * @param actionEvent the action event
     * @throws IOException if navigation fails
     */
    public void goToSettings(ActionEvent actionEvent) throws IOException {
        mainController.switchToSettings();
    }

    /**
     * Navigates to the analytics page.
     *
     * @param actionEvent the action event
     * @throws IOException if navigation fails
     */
    public void goToAnalytics(ActionEvent actionEvent) throws IOException {
        mainController.switchToAnalytics();
    }

    /**
     * reformat the date
     *
     * @param time time to be formatted to "yyyy-MM-dd".
     * @return formatted date
     */
    private String formatTime(LocalDateTime time) {
        return time.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
    }
}