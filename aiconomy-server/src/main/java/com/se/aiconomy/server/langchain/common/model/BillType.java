package com.se.aiconomy.server.langchain.common.model;

import lombok.Getter;

/**
 * Enumeration representing different types of bills.
 * <p>
 * Each bill type is associated with a descriptive string value.
 * </p>
 */
@Getter
public enum BillType {
    /**
     * Food and dining expenses.
     */
    FOOD_DINING("Food & Dining"),
    /**
     * Transportation expenses.
     */
    TRANSPORTATION("Transportation"),
    /**
     * Shopping expenses.
     */
    SHOPPING("Shopping"),
    /**
     * Housing expenses.
     */
    HOUSING("Housing"),
    /**
     * Education expenses.
     */
    EDUCATION("Education"),
    /**
     * Travel expenses.
     */
    TRAVEL("Travel"),
    /**
     * Gifts expenses.
     */
    GIFTS("Gifts"),
    /**
     * Groceries expenses.
     */
    GROCERIES("Groceries"),
    /**
     * Rent expenses.
     */
    RENT("Rent"),
    /**
     * Entertainment expenses.
     */
    ENTERTAINMENT("Entertainment"),
    /**
     * Utilities expenses.
     */
    UTILITIES("Utilities"),
    /**
     * Healthcare expenses.
     */
    HEALTHCARE("Healthcare"),
    /**
     * Other types of expenses.
     */
    OTHER("other");

    /**
     * The string value representing the bill type.
     */
    private final String type;

    /**
     * Constructs a BillType enum with the specified string value.
     *
     * @param type the string value of the bill type
     */
    BillType(String type) {
        this.type = type;
    }
}