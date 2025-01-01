package com.eteration.simplebanking.model;


import com.eteration.simplebanking.exception.InsufficientBalanceException;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue("WITHDRAWAL")
public class WithdrawalTransaction extends Transaction {

    public WithdrawalTransaction() {
        super();
    }

    public WithdrawalTransaction(double amount, Account account) {
        super(amount, account);
    }



    @Override
    public void process(Account account) {
        System.out.println("Attempting to withdraw: " + getAmount() + " from account: " + account.getAccountNumber());
        System.out.println("Current balance: " + account.getBalance());

        // Check if the account has sufficient balance
        if (account.getBalance() < getAmount()) {
            System.out.println("Insufficient balance. Throwing InsufficientBalanceException.");
            throw new InsufficientBalanceException("Insufficient balance");
        }

        // Proceed with withdrawal
        double newBalance = account.getBalance() - getAmount();
        account.modifyBalance(newBalance);

        System.out.println("Withdrawal successful. New balance: " + account.getBalance());
    }

    @Override
    public String toString() {
        return "WithdrawalTransaction{" +
                "date=" + getDate() +
                ", amount=" + getAmount() +
                ", approvalCode='" + getApprovalCode() + '\'' +
                '}';
    }
}



