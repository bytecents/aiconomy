package com.se.aiconomy.client.controller.budgets;

import com.se.aiconomy.client.common.MyFXMLLoader;
import com.se.aiconomy.client.controller.BaseController;
import com.se.aiconomy.client.controller.ai.AiController;
import com.se.aiconomy.server.handler.BudgetRequestHandler;
import com.se.aiconomy.server.langchain.common.model.BillType;
import com.se.aiconomy.server.langchain.common.model.Budget;
import com.se.aiconomy.server.model.dto.budget.request.BudgetAnalysisRequest;
import dev.langchain4j.model.output.structured.Description;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import lombok.Setter;

public class AiOptimizePanelController extends BaseController {
    @FXML
    private VBox recommendations;
    @FXML
    private ProgressBar healthScoreBar;
    final BudgetRequestHandler handler = new BudgetRequestHandler();
    @FXML
    private Label optimizeStatus;
    @FXML
    private Label healthScore;
    @FXML
    private TextFlow potentialSaving;
    @FXML
    private Text potentialSavingLabel;
    @FXML
    private Label recommendationLabel;

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
            optimizeStatus.setText("Analysis Complete");
            healthScore.setText("Budget health score: " + analysis.getBudgetHealth() + "/100");
            healthScore.setVisible(true);
            healthScore.setManaged(true);
            potentialSavingLabel.setText("Found potential monthly savings of $" + analysis.getPotentialSavings());
            potentialSaving.setVisible(true);
            potentialSaving.setManaged(true);
            healthScoreBar.setProgress(analysis.getBudgetHealth() / 100.0);
            recommendationLabel.setVisible(true);
            recommendationLabel.setManaged(true);
            recommendations.getChildren().clear();
            for (Budget.AIAnalysis.Recommendation recommendation : analysis.getRecommendations()) {
                System.out.println("Category: " + recommendation.getCategory());
                System.out.println("Suggestion: " + recommendation.getSuggestion());
                System.out.println("Suggested Budget Reallocation: " + recommendation.getSuggestedBudgetReallocation());
                System.out.println("Potential Savings: " + recommendation.getPotentialSavings());
                System.out.println("Priority: " + recommendation.getPriority());
                MyFXMLLoader loader = new MyFXMLLoader("/fxml/budgets/ai-optimize-item.fxml");
                Parent root = loader.load();
                AiOptimizeItemController itemController = loader.getController();
                itemController.setRecommendation(recommendation);
                recommendations.getChildren().add(root);
            }
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
