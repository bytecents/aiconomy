package com.se.aiconomy.server.langchain.common.model;

import dev.langchain4j.model.output.structured.Description;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Setter
@Getter
public class Budget {

    @Description("The total budget allocated for the month or period.")
    private double totalBudget;

    @Description("The amount spent from the budget so far.")
    private double spent;

    @Description("The number of alerts triggered due to budget issues.")
    private int alerts;

    @Description("A list of category budgets within the total budget.")
    private List<CategoryBudget> categoryBudgets;

    public double getLeft() {
        return totalBudget - spent;
    }

    @Setter
    @Getter
    @AllArgsConstructor
    public static class CategoryBudget {

        @Description("The category to which the budget is allocated (e.g., Food & Dining, Transportation).")
        private BillType category;

        @Description("The allocated budget for this category.")
        private double budget;

        @Description("The amount spent in this category so far.")
        private double spent;

        public double getLeft() {
            return budget - spent;
        }

        public double getPercentageUsed() {
            return (budget > 0) ? (spent / budget) * 100 : 0;
        }

        public double getOverBudget() {
            return (spent > budget) ? spent - budget : 0;
        }
    }

    @Setter
    @Getter
    @ToString
    public static class AIAnalysis {

        @Description("The health of the budget based on the analysis (a score or percentage).")
        private double budgetHealth;

        @Description("The total amount of potential savings identified by the AI.")
        private double potentialSavings;

        @Description("A list of AI-generated recommendations to optimize the budget.")
        private List<Recommendation> recommendations;

        @Setter
        @Getter
        @ToString
        public static class Recommendation {

            @Description("The category of the recommendation (e.g., Food & Dining, Transportation).")
            private BillType category;

            @Description("The suggestion provided by the AI for improving budget management.")
            private String suggestion;

            @Description("The suggested new totalBudget amount for the category.")
            private double suggestedBudgetReallocation;

            @Description("The amount of potential savings that can be achieved through the recommendation.")
            private double potentialSavings;

            @Description("The priority level of the recommendation (e.g., High, Medium, Low).")
            private String priority;
        }
    }
}
