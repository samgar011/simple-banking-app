package com.eteration.simplebanking.service.impl;


import com.eteration.simplebanking.dto.request.CreateAccountRequest;
import com.eteration.simplebanking.dto.request.PhoneBillPaymentRequest;
import com.eteration.simplebanking.dto.response.BankAccountResponse;
import com.eteration.simplebanking.dto.response.CreateAccountResponse;
import com.eteration.simplebanking.dto.response.CreditDebitResponse;
import com.eteration.simplebanking.dto.response.TransactionResponse;
import com.eteration.simplebanking.exception.InsufficientBalanceException;
import com.eteration.simplebanking.model.Account;
import com.eteration.simplebanking.model.Transaction;
import com.eteration.simplebanking.repository.AccountRepository;
import com.eteration.simplebanking.repository.TransactionRepository;
import com.eteration.simplebanking.service.AccountService;
import com.eteration.simplebanking.service.TransactionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import javax.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;
    private final TransactionFactory transactionFactory;

    @Autowired
    public AccountServiceImpl(AccountRepository accountRepository,
                              TransactionRepository transactionRepository,
                              TransactionFactory transactionFactory) {
        this.accountRepository = accountRepository;
        this.transactionRepository = transactionRepository;
        this.transactionFactory = transactionFactory;
    }

    @Override
    @Transactional
    public CreateAccountResponse createAccount(CreateAccountRequest request)
            throws Exception {
        if (accountRepository.findByAccountNumber(request
                .getAccountNumber()).isPresent()) {
            throw new Exception("Account already exists");
        }

        Account account = new Account(request.getOwner(), request.getAccountNumber());
        accountRepository.save(account);

        return new CreateAccountResponse("OK", account.getAccountNumber());
    }

    @Override
    public BankAccountResponse getAccountDetailsResponse(String accountNumber)
            throws Exception {
        Account account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new Exception("Account not found"));

        List<TransactionResponse> transactionResponses = account
                .getTransactions()
                .stream()
                .map(TransactionResponse::fromTransaction)
                .collect(Collectors.toList());

        return new BankAccountResponse(
                account.getAccountNumber(),
                account.getOwner(),
                account.getBalance(),
                account.getCreateDate(),
                transactionResponses
        );
    }

    @Override
    @Transactional
    public CreditDebitResponse creditAccount(String accountNumber,
                                             double amount) throws Exception {
        String approvalCode = postTransaction(accountNumber, "DEPOSIT",
                amount);
        return new CreditDebitResponse("OK", approvalCode);
    }

    @Override
    @Transactional
    public CreditDebitResponse debitAccount(String accountNumber, double amount)
            throws Exception {
        try {
            String approvalCode = postTransaction(accountNumber,
                    "WITHDRAWAL", amount);
            return new CreditDebitResponse("OK", approvalCode);
        } catch (InsufficientBalanceException e) {
            return new CreditDebitResponse("FAILED: "
                    + e.getMessage(), null);
        }
    }

    @Override
    @Transactional
    public CreditDebitResponse phoneBillPayment(String accountNumber,
                                                PhoneBillPaymentRequest request)
            throws Exception {
        try {
            String approvalCode = postTransaction(
                    accountNumber,
                    "PHONE_BILL_PAYMENT",
                    request.getAmount(),
                    request.getServiceProvider(),
                    request.getPhoneNumber()
            );
            return new CreditDebitResponse("OK", approvalCode);
        } catch (InsufficientBalanceException e) {
            return new CreditDebitResponse("FAILED: " + e.getMessage(),
                    null);
        }
    }

    @Override
    @Transactional
    public String postTransaction(String accountNumber, String transactionType,
                                  double amount, Object... additionalParams)
            throws Exception {
        Account account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new Exception("Account not found"));

        Transaction transaction = transactionFactory.createTransaction(transactionType,
                amount, account, additionalParams);
        account.post(transaction);
        transactionRepository.save(transaction);
        accountRepository.save(account);

        return transaction.getApprovalCode();
    }
}
