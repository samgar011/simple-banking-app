package com.eteration.simplebanking.model;


import com.eteration.simplebanking.dto.response.TransactionResponse;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue("DEPOSIT")
public class DepositTransaction extends Transaction {

    public DepositTransaction() {
        super();
    }

    public DepositTransaction(double amount, Account account) {
        super(amount, account);
    }

    @Override
    public void process(Account account) {
        double newBalance = account.getBalance() + getAmount();
        account.modifyBalance(newBalance);
    }

    public TransactionResponse toResponse() {
        return new TransactionResponse(
                getDate(),
                getAmount(),
                getApprovalCode(),
                "DEPOSIT"
        );
    }

    @Override
    public String toString() {
        return "DepositTransaction{" +
                "date=" + getDate() +
                ", amount=" + getAmount() +
                ", approvalCode='" + getApprovalCode() + '\'' +
                '}';
    }
}
