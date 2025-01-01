package com.eteration.simplebanking.service;

import com.eteration.simplebanking.model.Account;
import com.eteration.simplebanking.model.Transaction;

public interface TransactionFactory {
    Transaction createTransaction(String type, double amount,
                                  Account account, Object... additionalParams);
}
