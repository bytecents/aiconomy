package com.se.aiconomy.client.controller.budgets;

import com.se.aiconomy.server.langchain.common.model.Budget;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.text.Text;

/**
 * Controller for displaying AI optimization recommendation items in the budget UI.
 */
public class AiOptimizeItemController {
    /** Label to display the potential monthly savings. */
    @FXML
    private Label potentialSavingLabel;

    /** Text area to display detailed suggestion information. */
    @FXML
    private Text detailsLabel;

    /** Label to display the priority of the recommendation. */
    @FXML
    private Label priorityLabel;

    /** Label to display the suggested budget reallocation. */
    @FXML
    private Label descriptionLabel;

    /** Label to display the category of the recommendation. */
    @FXML
    private Label categoryLabel;

    /**
     * Sets the recommendation data to be displayed in the UI components.
     *
     * @param recommendation the AI analysis recommendation to display
     */
    public void setRecommendation(Budget.AIAnalysis.Recommendation recommendation) {
        potentialSavingLabel.setText("Potential Saving: $" + recommendation.getPotentialSavings() + "/month");
        detailsLabel.setText(recommendation.getSuggestion());
        priorityLabel.setText(recommendation.getPriority() + " Priority");
        descriptionLabel.setText("Change to $" + recommendation.getSuggestedBudgetReallocation());
        categoryLabel.setText(recommendation.getCategory().getType());
    }
}