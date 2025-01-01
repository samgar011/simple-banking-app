package com.eteration.simplebanking.service;

import com.eteration.simplebanking.dto.request.CreateAccountRequest;
import com.eteration.simplebanking.dto.request.PhoneBillPaymentRequest;
import com.eteration.simplebanking.dto.response.BankAccountResponse;
import com.eteration.simplebanking.dto.response.CreateAccountResponse;
import com.eteration.simplebanking.dto.response.CreditDebitResponse;

public interface AccountService {

    CreateAccountResponse createAccount(CreateAccountRequest request) throws Exception;

    BankAccountResponse getAccountDetailsResponse(String accountNumber) throws Exception;

    CreditDebitResponse creditAccount(String accountNumber, double amount) throws Exception;

    CreditDebitResponse debitAccount(String accountNumber, double amount) throws Exception;

    CreditDebitResponse phoneBillPayment(String accountNumber,
                                         PhoneBillPaymentRequest request) throws Exception;
    String postTransaction(String accountNumber, String transactionType,
                           double amount, Object... additionalParams) throws Exception;


}
