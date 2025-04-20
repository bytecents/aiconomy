package com.se.aiconomy.client.model.dto;

public class BudgetDto {
    // BudgetCategoryInfoRequest.java
    public class BudgetCategoryInfoRequest {
        private int userId;

        public BudgetCategoryInfoRequest(int userId) {
            this.userId = userId;
        }

        public int getUserId() {
            return userId;
        }
    }

}
