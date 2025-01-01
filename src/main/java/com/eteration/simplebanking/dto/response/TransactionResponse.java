package com.eteration.simplebanking.dto.response;

import com.eteration.simplebanking.model.DepositTransaction;
import com.eteration.simplebanking.model.PhoneBillPaymentTransaction;
import com.eteration.simplebanking.model.Transaction;
import com.eteration.simplebanking.model.WithdrawalTransaction;

import java.time.LocalDateTime;

public class TransactionResponse {
    private LocalDateTime date;
    private double amount;
    private String approvalCode;
    private String transactionType;

    public TransactionResponse(LocalDateTime date, double amount, String approvalCode, String transactionType) {
        this.date = date;
        this.amount = amount;
        this.approvalCode = approvalCode;
        this.transactionType = transactionType;
    }

    public static TransactionResponse fromTransaction(Transaction transaction) {
        String transactionType = transaction.getClass().getSimpleName();
        String description;

        if (transaction instanceof DepositTransaction) {
            description = "Deposit";
        } else if (transaction instanceof WithdrawalTransaction) {
            description = "Withdrawal";
        } else if (transaction instanceof PhoneBillPaymentTransaction) {
            description = "Phone bill payment transaction.";
        } else {
            description = "Unknown transaction type.";
        }
        return new TransactionResponse(
                transaction.getDate(),
                transaction.getAmount(),
                transaction.getApprovalCode(),
                transaction.getClass().getSimpleName()
        );
    }

    public LocalDateTime getDate() {
        return date;
    }

    public double getAmount() {
        return amount;
    }

    public String getApprovalCode() {
        return approvalCode;
    }

    public String getTransactionType() {
        return transactionType;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public void setApprovalCode(String approvalCode) {
        this.approvalCode = approvalCode;
    }

    public void setTransactionType(String transactionType) {
        this.transactionType = transactionType;
    }
}
