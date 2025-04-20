package com.se.aiconomy.server.langchain.common.model;

import lombok.Getter;

@Getter
public enum BillType {
    FOOD_DINING("Food & Dining"),
    TRANSPORTATION("Transportation"),
    SHOPPING("Shopping"),
    HOUSING("Housing"),
    EDUCATION("Education"),
    TRAVEL("Travel"),
    GIFTS("Gifts"),
    GROCERIES("Groceries"),
    RENT("Rent"),
    ENTERTAINMENT("Entertainment"),
    UTILITIES("Utilities"),
    HEALTHCARE("Healthcare"),
    OTHER("other");

    private final String type;

    BillType(String type) {
        this.type = type;
    }
}
