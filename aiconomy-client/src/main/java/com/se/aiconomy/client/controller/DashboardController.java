package com.se.aiconomy.client.controller;

import com.se.aiconomy.server.handler.DashboardRequestHandler;
import com.se.aiconomy.server.model.dto.transaction.request.GetTransactionByUserIdRequest;
import com.se.aiconomy.server.service.impl.AccountServiceImpl;
import com.se.aiconomy.server.service.impl.BudgetServiceImpl;
import com.se.aiconomy.server.service.impl.TransactionServiceImpl;
import com.se.aiconomy.server.storage.service.impl.JSONStorageServiceImpl;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import lombok.Setter;

import java.io.IOException;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.Month;
import java.util.Map;

public class DashboardController extends BaseController {
    private final DashboardRequestHandler dashBoardRequestHandler = new DashboardRequestHandler(new AccountServiceImpl(JSONStorageServiceImpl.getInstance()), new TransactionServiceImpl(), new BudgetServiceImpl(JSONStorageServiceImpl.getInstance()));
    @FXML
    public Label welcomeTextField;
    public Label netWorth;
    public Label monthlySpending;
    public Label monthlyIncome;
    public Label creditCardDue;
    public Label budgetProgressCategoryLabel1;
    public ProgressBar budgetProgressProgressBar1;
    public Label budgetProgressCategoryRate1;
    public Label budgetProgressCategoryLabel2;
    public Label budgetProgressCategoryRate2;
    public ProgressBar budgetProgressProgressBar2;
    public Label budgetProgressCategoryLabel3;
    public Label budgetProgressCategoryRate3;
    public ProgressBar budgetProgressProgressBar3;
    @FXML
    Button quickAddButton;

    @FXML
    void initialize() {
        if (userInfo == null) {
            Platform.runLater(() -> {
                // 延迟到事件调度线程中处理
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
        try {
            DashboardRequestHandler.DashboardData dashboardData = dashBoardRequestHandler.getDashboardData(userInfo.getId(), LocalDate.now().getMonth());
            netWorth.setText(Double.toString(dashboardData.getNetWorth()));
            monthlySpending.setText(Double.toString(dashboardData.getMonthlySpending()));
            monthlyIncome.setText(Double.toString(dashboardData.getMonthlyIncome()));
            creditCardDue.setText(Double.toString(dashboardData.getCreditCardDue()));

            Map<String, Double> budgetSpendingRatio = dashBoardRequestHandler.getBudgetSpendingRatio(userInfo.getId());
            int count = 0;
            for (Map.Entry<String, Double> categoryInfo : budgetSpendingRatio.entrySet()) {
                if (count == 3) break;
                String category = categoryInfo.getKey();
                DecimalFormat decimalFormat = new DecimalFormat("##.##%");
                String percentage = decimalFormat.format(categoryInfo.getValue());
                switch (count) {
                    case 0: {
                        budgetProgressCategoryLabel1.setText(category);
                        budgetProgressCategoryRate1.setText(percentage);
                        budgetProgressProgressBar1.setProgress(categoryInfo.getValue());
                    }
                    case 1: {
                        budgetProgressCategoryLabel2.setText(category);
                        budgetProgressCategoryRate2.setText(percentage);
                        budgetProgressProgressBar2.setProgress(categoryInfo.getValue());
                    }
                    case 2: {
                        budgetProgressCategoryLabel3.setText(category);
                        budgetProgressCategoryRate3.setText(percentage);
                        budgetProgressProgressBar3.setProgress(categoryInfo.getValue());
                    }
                }
                count++;
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    @FXML
    public void quickAdd(ActionEvent actionEvent) throws IOException {
        goToBudget(actionEvent);
        mainController.openAddBudgetPanel();
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