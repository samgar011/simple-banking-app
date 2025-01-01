package com.eteration.simplebanking.dto.request;

public class CreateAccountRequest {
    private String accountNumber;
    private String owner;

    public CreateAccountRequest() {}

    public CreateAccountRequest(String accountNumber, String owner) {
        this.accountNumber = accountNumber;
        this.owner = owner;
    }

    // Getters and Setters
    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }
}
