package com.eteration.simplebanking;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.eteration.simplebanking.dto.response.BankAccountResponse;
import com.eteration.simplebanking.dto.response.TransactionResponse;
import com.eteration.simplebanking.exception.InsufficientBalanceException;
import com.eteration.simplebanking.model.Account;
import com.eteration.simplebanking.model.DepositTransaction;
import com.eteration.simplebanking.model.Transaction;
import com.eteration.simplebanking.model.WithdrawalTransaction;
import com.eteration.simplebanking.repository.AccountRepository;
import com.eteration.simplebanking.repository.TransactionRepository;
import com.eteration.simplebanking.service.TransactionFactory;

import com.eteration.simplebanking.service.impl.AccountServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

public class AccountServiceTests {

    @InjectMocks
    private AccountServiceImpl accountService;

    @Mock
    private AccountRepository bankAccountRepository;

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private TransactionFactory transactionFactory;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testCreditAccount() throws Exception {
        String accountNumber = "17892";
        double amount = 1000.0;

        Account account = new Account("Kerem Karaca", accountNumber);
        when(bankAccountRepository.findByAccountNumber(accountNumber)).thenReturn(Optional.of(account));

        // Create a DepositTransaction instance to be returned by the factory
        DepositTransaction depositTrx = new DepositTransaction(amount, account);
        when(transactionFactory.createTransaction("DEPOSIT", amount, account)).thenReturn(depositTrx);

        // Mocking transactionRepository.save to return the same DepositTransaction with approvalCode
        when(transactionRepository.save(any(DepositTransaction.class))).thenAnswer(invocation -> {
            DepositTransaction trx = invocation.getArgument(0);
            // Simulate @PrePersist by generating a new approvalCode
            trx.setBankAccount(account);
            trx.generateApprovalCode(); // Ensure approvalCode is set
            return trx;
        });

        when(bankAccountRepository.save(any(Account.class))).thenReturn(account);

        String returnedApprovalCode = accountService.postTransaction(accountNumber, "DEPOSIT", amount);

        // Retrieve the approvalCode from the savedDeposit
        String expectedApprovalCode = depositTrx.getApprovalCode();

        // Assert that the returnedApprovalCode matches the expectedApprovalCode
        assertEquals(expectedApprovalCode, returnedApprovalCode);

        // Assert that approvalCode is a valid UUID
        assertDoesNotThrow(() -> UUID.fromString(returnedApprovalCode));

        verify(bankAccountRepository, times(1)).findByAccountNumber(accountNumber);
        verify(transactionFactory, times(1)).createTransaction("DEPOSIT", amount, account);
        verify(transactionRepository, times(1)).save(any(DepositTransaction.class));
        verify(bankAccountRepository, times(1)).save(any(Account.class));
    }

    @Test
    public void testDebitAccount_SufficientBalance() throws Exception {
        String accountNumber = "17892";
        double creditAmount = 1000.0;
        double debitAmount = 500.0;

        Account account = new Account("Kerem Karaca", accountNumber);
        account.modifyBalance(creditAmount);
        when(bankAccountRepository.findByAccountNumber(accountNumber)).thenReturn(Optional.of(account));

        // Create a WithdrawalTransaction instance to be returned by the factory
        WithdrawalTransaction withdrawalTrx = new WithdrawalTransaction(debitAmount, account);
        when(transactionFactory.createTransaction("WITHDRAWAL", debitAmount, account)).thenReturn(withdrawalTrx);

        // Mocking transactionRepository.save to return the same WithdrawalTransaction with approvalCode
        when(transactionRepository.save(any(WithdrawalTransaction.class))).thenAnswer(invocation -> {
            WithdrawalTransaction trx = invocation.getArgument(0);
            // Simulate @PrePersist by generating a new approvalCode
            trx.setBankAccount(account);
            trx.generateApprovalCode(); // Ensure approvalCode is set
            return trx;
        });

        when(bankAccountRepository.save(any(Account.class))).thenReturn(account);

        String returnedApprovalCode = accountService.postTransaction(accountNumber, "WITHDRAWAL", debitAmount);

        // Retrieve the approvalCode from the savedWithdrawal
        String expectedApprovalCode = withdrawalTrx.getApprovalCode();

        // Assert that the returnedApprovalCode matches the expectedApprovalCode
        assertEquals(expectedApprovalCode, returnedApprovalCode);

        // Assert that approvalCode is a valid UUID
        assertDoesNotThrow(() -> UUID.fromString(returnedApprovalCode));

        verify(bankAccountRepository, times(1)).findByAccountNumber(accountNumber);
        verify(transactionFactory, times(1)).createTransaction("WITHDRAWAL", debitAmount, account);
        verify(transactionRepository, times(1)).save(any(WithdrawalTransaction.class));
        verify(bankAccountRepository, times(1)).save(any(Account.class));
    }

    @Test
    public void testDebitAccount_InsufficientBalance() throws Exception {
        String accountNumber = "17892";
        double creditAmount = 1000.0;
        double debitAmount = 1500.0;

        Account account = new Account("Kerem Karaca", accountNumber);
        account.modifyBalance(creditAmount);
        when(bankAccountRepository.findByAccountNumber(accountNumber)).thenReturn(Optional.of(account));

        // Create a mock WithdrawalTransaction to be returned by the factory
        WithdrawalTransaction withdrawalTrx = mock(WithdrawalTransaction.class);
        when(transactionFactory.createTransaction("WITHDRAWAL", debitAmount, account)).thenReturn(withdrawalTrx);

        // Simulate the process method throwing InsufficientBalanceException
        doThrow(new InsufficientBalanceException("Insufficient balance")).when(withdrawalTrx).process(account);

        // Execute and assert
        Exception exception = assertThrows(InsufficientBalanceException.class, () -> {
            accountService.postTransaction(accountNumber, "WITHDRAWAL", debitAmount);
        });

        assertEquals("Insufficient balance", exception.getMessage());

        verify(bankAccountRepository, times(1)).findByAccountNumber(accountNumber);
        verify(transactionFactory, times(1)).createTransaction("WITHDRAWAL", debitAmount, account);
        verify(transactionRepository, times(0)).save(any(WithdrawalTransaction.class));
        verify(bankAccountRepository, times(0)).save(any(Account.class));
    }

    @Test
    public void testGetAccountDetailsResponse() throws Exception {
        String accountNumber = "17892";
        String owner = "Kerem Karaca";
        double balance = 500.0;

        // Create mock Account object
        Account account = new Account(owner, accountNumber);
        account.modifyBalance(balance);

        // Mock repository to return Optional<Account>
        when(bankAccountRepository.findByAccountNumber(accountNumber))
                .thenReturn(Optional.of(account));

        // Mock transactions
        List<Transaction> transactions = List.of(
                new DepositTransaction(1000.0, account), // Example DepositTransaction
                new WithdrawalTransaction(500.0, account) // Example WithdrawalTransaction
        );

        // Use reflection or set transactions directly if possible
        java.lang.reflect.Field transactionsField = Account.class.getDeclaredField("transactions");
        transactionsField.setAccessible(true);
        transactionsField.set(account, transactions);

        // Mock service transformation to BankAccountResponse
        List<TransactionResponse> transactionResponses = transactions.stream()
                .map(TransactionResponse::fromTransaction)
                .collect(Collectors.toList());

        BankAccountResponse expectedResponse = new BankAccountResponse(
                accountNumber,
                owner,
                balance,
                account.getCreateDate(),
                transactionResponses
        );

        BankAccountResponse actualResponse = accountService.getAccountDetailsResponse(accountNumber);

        // Assertions
        assertEquals(expectedResponse.getAccountNumber(), actualResponse.getAccountNumber());
        assertEquals(expectedResponse.getOwner(), actualResponse.getOwner());
        assertEquals(expectedResponse.getBalance(), actualResponse.getBalance());
        assertEquals(expectedResponse.getTransactions().size(), actualResponse.getTransactions().size());

        // Verify repository interactions
        verify(bankAccountRepository, times(1)).findByAccountNumber(accountNumber);
    }

    @Test
    public void testGetAccountDetailsResponse_NotFound() {
        String accountNumber = "99999";

        when(bankAccountRepository.findByAccountNumber(accountNumber)).thenReturn(Optional.empty());

        Exception exception = assertThrows(Exception.class, () -> {
            accountService.getAccountDetailsResponse(accountNumber);
        });

        assertEquals("Account not found", exception.getMessage());
        verify(bankAccountRepository, times(1)).findByAccountNumber(accountNumber);
    }
}