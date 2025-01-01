package com.eteration.simplebanking.service.impl;

import com.eteration.simplebanking.model.*;
import com.eteration.simplebanking.service.TransactionFactory;
import org.springframework.stereotype.Component;

@Component
public class TransactionFactoryImpl implements TransactionFactory {

    @Override
    public Transaction createTransaction(String type, double amount,
                                         Account account, Object... additionalParams) {
        switch (type.toUpperCase()) {
            case "DEPOSIT":
                return new DepositTransaction(amount, account);
            case "WITHDRAWAL":
                return new WithdrawalTransaction(amount, account);
            case "PHONE_BILL_PAYMENT":
                if (additionalParams.length < 2) {
                    throw new IllegalArgumentException("PhoneBillPaymentTransaction requires serviceProvider and phoneNumber.");
                }
                String serviceProvider = (String) additionalParams[0];
                String phoneNumber = (String) additionalParams[1];
                return new PhoneBillPaymentTransaction(serviceProvider,
                        phoneNumber, amount, account);
            default:
                throw new IllegalArgumentException("Unsupported transaction type: " + type);
        }
    }
}