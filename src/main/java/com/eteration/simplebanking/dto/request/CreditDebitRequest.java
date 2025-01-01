package com.eteration.simplebanking.dto.request;

public class CreditDebitRequest {
    private double amount;

    // Constructors
    public CreditDebitRequest() {}

    public CreditDebitRequest(double amount) {
        this.amount = amount;
    }

    // Getter and Setter
    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }
}
