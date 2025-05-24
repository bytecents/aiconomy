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
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.util.StringConverter;

import java.io.IOException;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class DashboardController extends BaseController {
    private final DashboardRequestHandler dashBoardRequestHandler = new DashboardRequestHandler(new AccountServiceImpl(JSONStorageServiceImpl.getInstance()), new TransactionServiceImpl(), new BudgetServiceImpl(JSONStorageServiceImpl.getInstance()));
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

    private void init() {
        welcomeTextField.setText("Welcome back, " + userInfo.getFirstName() + "!");
        setDate();
        try {
            setDashboardBasicData();
            setDashBoardTransactionData(); // TBI
            setDashboardSpendTrendsData(); // TBI
            setDashBoardBudgetsData();
            setDashboardAccountOverviewData(); // TBI
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
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

    private void setDate() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEEE, MMMM d, yyyy", Locale.ENGLISH);
        String today = LocalDate.now().format(formatter);
        dateTextField.setText(today);
    }

    private void setDashboardBasicData() throws ServiceException {
        DashboardRequestHandler.DashboardData dashboardData = dashBoardRequestHandler.getDashboardData(userInfo.getId(), LocalDate.now().getMonth());
        System.out.println("dashboardData: " + dashboardData);
        netWorth.setText("$ " + dashboardData.getNetWorth());
        monthlySpending.setText("$ " + dashboardData.getMonthlySpending());
        monthlyIncome.setText("$ " + dashboardData.getMonthlyIncome());
        creditCardDue.setText("$ " + dashboardData.getCreditCardDue());
    }

    private void setDashBoardTransactionData() throws ServiceException {
        TransactionService transactionService = new TransactionServiceImpl();
        List<TransactionDto> transactionInfo = transactionService.getTransactionsByUserId(userInfo.getId());
        System.out.println(transactionInfo);
    }

    private void setDashboardSpendTrendsData() throws ServiceException {

    }

    private void setDashBoardBudgetsData() throws ServiceException {
        Map<String, Double> budgetSpendingRatio = dashBoardRequestHandler.getBudgetSpendingRatio(userInfo.getId());
        int count = 0;
        for (Map.Entry<String, Double> categoryInfo : budgetSpendingRatio.entrySet()) {
            if (count == 3) break;
            String category = categoryInfo.getKey();
            double ratio = categoryInfo.getValue();
            switch (count) {
                case 0: {
                    budgetProgressCategoryLabel1.setText(category);
                    adjustRatioText(budgetProgressCategoryRate1, ratio);
                    adjustProgressBar(budgetProgressProgressBar1, ratio);
                }
                case 1: {
                    budgetProgressCategoryLabel2.setText(category);
                    adjustRatioText(budgetProgressCategoryRate2, ratio);
                    adjustProgressBar(budgetProgressProgressBar2, ratio);
                }
                case 2: {
                    budgetProgressCategoryLabel3.setText(category);
                    adjustRatioText(budgetProgressCategoryRate3, ratio);
                    adjustProgressBar(budgetProgressProgressBar3, ratio);
                }
            }
            count++;
        }
    }

    private void setDashboardAccountOverviewData() throws ServiceException {
        List<Account> userAccount = dashBoardRequestHandler.getAccountsForUser(userInfo.getId());
        // System.out.println("user Account: " + userAccount.getFirst());
        int count = 0;
        for (Account account : userAccount) {
            if (count == 2) break;
            String accountBankName = account.getBankName();
            String accountType = account.getAccountType();
            double accountBalance = account.getBalance();
            switch (count) {
                case 0: {
                    accountBankName1.setText(accountBankName);
                    accountType1.setText(accountType);
                    accountBalance1.setText("$ " + accountBalance);
                }
                case 1: {
                    accountBankName2.setText(accountBankName);
                    accountType2.setText(accountType);
                    accountBalance2.setText("$ " + accountBalance);
                }
            }
            count++;
        }
    }

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

    @FXML
    public void quickAdd(ActionEvent actionEvent) throws IOException {
        goToTransaction(actionEvent);
    }

    public void goToTransaction(ActionEvent actionEvent) throws IOException {
        mainController.switchToTransactions();
    }

    public void goToBudget(ActionEvent actionEvent) throws IOException {
        mainController.switchToBudgets();
    }

    public void goToAccount(ActionEvent actionEvent) throws IOException {
        mainController.switchToAccounts();
    }

    public void goToSettings(ActionEvent actionEvent) throws IOException {
        mainController.switchToSettings();
    }

    public void goToAnalytics(ActionEvent actionEvent) throws IOException {
        mainController.switchToAnalytics();
    }
}