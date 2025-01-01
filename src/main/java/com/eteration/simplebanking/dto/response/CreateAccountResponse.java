package com.eteration.simplebanking.dto.response;

public class CreateAccountResponse {
    private String status;
    private String accountNumber;

    public CreateAccountResponse() {}

    public CreateAccountResponse(String status, String accountNumber) {
        this.status = status;
        this.accountNumber = accountNumber;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }
}