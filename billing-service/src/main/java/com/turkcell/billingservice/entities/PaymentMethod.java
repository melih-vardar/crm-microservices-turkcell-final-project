package com.turkcell.billingservice.entities;

public enum PaymentMethod {
    CREDIT_CARD("Kredi Kartı"),
    DEBIT_CARD("Banka Kartı"),
    BANK_TRANSFER("Banka Transferi"),
    CASH("Nakit");

    private final String displayName;

    PaymentMethod(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}