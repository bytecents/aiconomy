package com.se.aiconomy.client.controller.budgets;

import com.se.aiconomy.server.langchain.common.model.Budget;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.text.Text;

public class AiOptimizeItemController {
    @FXML
    private Label potentialSavingLabel;
    @FXML
    private Text detailsLabel;
    @FXML
    private Label priorityLabel;
    @FXML
    private Label descriptionLabel;
    @FXML
    private Label categoryLabel;

    public void setRecommendation(Budget.AIAnalysis.Recommendation recommendation) {
        potentialSavingLabel.setText("Potential Saving: $" + recommendation.getPotentialSavings() + "/month");
        detailsLabel.setText(recommendation.getSuggestion());
        priorityLabel.setText(recommendation.getPriority() + " Priority");
        descriptionLabel.setText("Change to $" + recommendation.getSuggestedBudgetReallocation());
        categoryLabel.setText(recommendation.getCategory().getType());
    }
}
