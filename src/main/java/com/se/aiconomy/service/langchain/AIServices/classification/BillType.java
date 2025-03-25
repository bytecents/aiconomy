package com.se.aiconomy.service.langchain.AIServices.classification;

public enum BillType {
    GROCERIES("groceries"),
    RENT("rent"),
    ENTERTAINMENT("entertainment"),
    UTILITIES("utilities"),
    TRANSPORT("transport"),
    HEALTHCARE("healthcare"),
    EDUCATION("education"),
    DINING("dining"),
    SHOPPING("shopping"),
    OTHER("other");

    private final String type;

    BillType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }
}
