package com.se.aiconomy.server.langchain.common.model;

import dev.langchain4j.model.output.structured.Description;
import lombok.*;

import java.util.List;

/**
 * Represents a budget, including total allocation, spending, alerts, and category breakdowns.
 */
@Setter
@Getter
@ToString
public class Budget {

    /**
     * The total budget allocated for the month or period.
     */
    @Description("The total budget allocated for the month or period.")
    private double totalBudget;

    /**
     * The amount spent from the budget so far.
     */
    @Description("The amount spent from the budget so far.")
    private double spent;

    /**
     * The number of alerts triggered due to budget issues.
     */
    @Description("The number of alerts triggered due to budget issues.")
    private int alerts;

    /**
     * A list of category budgets within the total budget.
     */
    @Description("A list of category budgets within the total budget.")
    private List<CategoryBudget> categoryBudgets;

    /**
     * Gets the remaining budget (total minus spent).
     *
     * @return the amount of budget left
     */
    public double getLeft() {
        return totalBudget - spent;
    }

    /**
     * Represents the budget for a specific category.
     */
    @Setter
    @Getter
    @ToString
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CategoryBudget {

        /**
         * The category to which the budget is allocated (e.g., Food & Dining, Transportation).
         */
        @Description("The category to which the budget is allocated (e.g., Food & Dining, Transportation).")
        private DynamicBillType category;

        /**
         * The allocated budget for this category.
         */
        @Description("The allocated budget for this category.")
        private double budget;

        /**
         * The amount spent in this category so far.
         */
        @Description("The amount spent in this category so far.")
        private double spent;

        /**
         * Gets the remaining budget for this category.
         *
         * @return the amount of budget left in this category
         */
        public double getLeft() {
            return budget - spent;
        }

        /**
         * Gets the percentage of the category budget that has been used.
         *
         * @return the percentage of budget used (0 if budget is 0)
         */
        public double getPercentageUsed() {
            return (budget > 0) ? (spent / budget) * 100 : 0;
        }

        /**
         * Gets the amount by which the category is over budget.
         *
         * @return the amount over budget, or 0 if not over
         */
        public double getOverBudget() {
            return (spent > budget) ? spent - budget : 0;
        }
    }

    /**
     * Represents AI-generated analysis and recommendations for the budget.
     */
    @Setter
    @Getter
    @ToString
    public static class AIAnalysis {

        /**
         * The health of the budget based on the analysis (a score or percentage).
         */
        @Description("The health of the budget based on the analysis (a score or percentage).")
        private double budgetHealth;

        /**
         * The total amount of potential savings identified by the AI.
         */
        @Description("The total amount of potential savings identified by the AI.")
        private double potentialSavings;

        /**
         * A list of AI-generated recommendations to optimize the budget.
         */
        @Description("A list of AI-generated recommendations to optimize the budget.")
        private List<Recommendation> recommendations;

        /**
         * Represents a single AI-generated recommendation for budget optimization.
         */
        @Setter
        @Getter
        @ToString
        public static class Recommendation {

            /**
             * The category of the recommendation (e.g., Food & Dining, Transportation).
             */
            @Description("The category of the recommendation (e.g., Food & Dining, Transportation).")
            private BillType category;

            /**
             * The suggestion provided by the AI for improving budget management.
             */
            @Description("The suggestion provided by the AI for improving budget management.")
            private String suggestion;

            /**
             * The suggested new totalBudget amount for the category.
             */
            @Description("The suggested new totalBudget amount for the category.")
            private double suggestedBudgetReallocation;

            /**
             * The amount of potential savings that can be achieved through the recommendation.
             */
            @Description("The amount of potential savings that can be achieved through the recommendation.")
            private double potentialSavings;

            /**
             * The priority level of the recommendation (e.g., High, Medium, Low).
             */
            @Description("The priority level of the recommendation (e.g., High, Medium, Low).")
            private String priority;
        }
    }
}