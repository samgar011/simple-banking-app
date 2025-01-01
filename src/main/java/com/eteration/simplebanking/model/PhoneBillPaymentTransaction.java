package com.eteration.simplebanking.model;

import com.eteration.simplebanking.exception.InsufficientBalanceException;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue("PHONE_BILL_PAYMENT")
public class PhoneBillPaymentTransaction extends Transaction {

    private String serviceProvider;
    private String phoneNumber;

    public PhoneBillPaymentTransaction() {
        super();
    }

    public PhoneBillPaymentTransaction(String serviceProvider, String phoneNumber, double amount, Account account) {
        super(amount, account);
        this.serviceProvider = serviceProvider;
        this.phoneNumber = phoneNumber;
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

    @Override
    public void process(Account account) {
        System.out.println("Attempting to pay phone bill: " + getAmount() + " to " + serviceProvider + " for phone number: " + phoneNumber);
        System.out.println("Current balance: " + account.getBalance());

        if (account.getBalance() < getAmount()) {
            System.out.println("Insufficient balance. Throwing InsufficientBalanceException.");
            throw new InsufficientBalanceException("Insufficient balance");
        }

        double newBalance = account.getBalance() - getAmount();
        account.modifyBalance(newBalance);

        System.out.println("Phone bill payment successful. New balance: " + account.getBalance());
    }

    @Override
    public String toString() {
        return "PhoneBillPaymentTransaction{" +
                "date=" + getDate() +
                ", amount=" + getAmount() +
                ", approvalCode='" + getApprovalCode() + '\'' +
                ", serviceProvider='" + serviceProvider + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                '}';
    }
}
