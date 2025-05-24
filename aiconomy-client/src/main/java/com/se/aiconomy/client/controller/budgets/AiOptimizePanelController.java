package com.se.aiconomy.client.controller.budgets;

import com.se.aiconomy.client.controller.BaseController;
import com.se.aiconomy.client.controller.ai.AiController;
import com.se.aiconomy.server.handler.BudgetRequestHandler;
import com.se.aiconomy.server.langchain.common.model.Budget;
import com.se.aiconomy.server.model.dto.budget.request.BudgetAnalysisRequest;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import lombok.Setter;

public class AiOptimizePanelController extends BaseController {
    final BudgetRequestHandler handler = new BudgetRequestHandler();

    public void initialize() {
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
        BudgetAnalysisRequest request = new BudgetAnalysisRequest();
        request.setUserId(userInfo.getId());
        Task<Budget.AIAnalysis> task = new Task<>() {
            @Override
            protected Budget.AIAnalysis call() throws Exception {
                return handler.handleBudgetAnalysisRequest(request);
            }
        };

        task.setOnSucceeded(event -> {
            Budget.AIAnalysis analysis = task.getValue();

        });
        new Thread(task).start();
    }

    @Setter
    private OnCloseListener onCloseListener;

    @FXML
    public void closeAiOptimizePanel() {
        if (onCloseListener != null) {
            onCloseListener.onCloseAiOptimizePanel();
        }
    }

    public interface OnCloseListener {
        void onCloseAiOptimizePanel();
    }
}
