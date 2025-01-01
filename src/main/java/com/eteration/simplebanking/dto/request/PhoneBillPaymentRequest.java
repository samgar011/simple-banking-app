package com.eteration.simplebanking.dto.request;

public class PhoneBillPaymentRequest {
    private double amount;
    private String serviceProvider;
    private String phoneNumber;

    public PhoneBillPaymentRequest() {}

    public PhoneBillPaymentRequest(double amount, String serviceProvider, String phoneNumber) {
        this.amount = amount;
        this.serviceProvider = serviceProvider;
        this.phoneNumber = phoneNumber;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getServiceProvider() {
        return serviceProvider;
    }

    public void setServiceProvider(String serviceProvider) {
        this.serviceProvider = serviceProvider;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
}
