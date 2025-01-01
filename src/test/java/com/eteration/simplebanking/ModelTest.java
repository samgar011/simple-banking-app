package com.eteration.simplebanking;


import com.eteration.simplebanking.exception.InsufficientBalanceException;
import com.eteration.simplebanking.model.Account;
import com.eteration.simplebanking.model.DepositTransaction;
import com.eteration.simplebanking.model.WithdrawalTransaction;
import com.eteration.simplebanking.repository.AccountRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import javax.transaction.Transactional;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@Transactional
public class ModelTest {

    @Autowired
    private AccountRepository accountRepository;

    @Test
    public void testCreateAccountAndSetBalance0() {
        Account account = new Account("Kerem Karaca", "17892");
        accountRepository.save(account); // Persist the account
        assertEquals("Kerem Karaca", account.getOwner());
        assertEquals("17892", account.getAccountNumber());
        assertEquals(0.0, account.getBalance(), 0.001);
    }

    @Test
    public void testDepositIntoBankAccount() {
        Account account = new Account("Demet Demircan", "9834");
        accountRepository.save(account);

        DepositTransaction depositTrx = new DepositTransaction(100.0, account);
        account.post(depositTrx);
        accountRepository.save(account);

        assertEquals(100.0, account.getBalance(), 0.001);
        assertEquals(1, account.getTransactions().size());
    }

    @Test
    public void testWithdrawFromBankAccount() throws InsufficientBalanceException {
        Account account = new Account("Demet Demircan", "9834");
        accountRepository.save(account);

        DepositTransaction depositTrx = new DepositTransaction(100.0, account);
        account.post(depositTrx);
        accountRepository.save(account);

        assertEquals(100.0, account.getBalance(), 0.001);
        assertEquals(1, account.getTransactions().size());

        WithdrawalTransaction withdrawalTrx = new WithdrawalTransaction(50.0, account);
        account.post(withdrawalTrx);
        accountRepository.save(account);

        assertEquals(50.0, account.getBalance(), 0.001);
        assertEquals(2, account.getTransactions().size());
    }

    @Test
    public void testWithdrawException() {
        Assertions.assertThrows(InsufficientBalanceException.class, () -> {
            Account account = new Account("Demet Demircan", "9834");
            accountRepository.save(account);

            DepositTransaction depositTrx = new DepositTransaction(100.0, account);
            account.post(depositTrx);
            accountRepository.save(account);

            assertEquals(100.0, account.getBalance(), 0.001);

            WithdrawalTransaction withdrawalTrx = new WithdrawalTransaction(500.0, account);
            account.post(withdrawalTrx);
            accountRepository.save(account);
        });
    }

    @Test
    public void testTransactions() throws InsufficientBalanceException {
        // Create and persist account
        Account account = new Account("Canan Kaya", "1234");
        accountRepository.save(account);
        assertEquals(0, account.getTransactions().size());

        // Deposit Transaction
        DepositTransaction depositTrx = new DepositTransaction(100.0, account);
        account.post(depositTrx);
        accountRepository.save(account);
        assertEquals(100.0, account.getBalance(), 0.001);
        assertEquals(1, account.getTransactions().size());

        // Withdrawal Transaction
        WithdrawalTransaction withdrawalTrx = new WithdrawalTransaction(60.0, account);
        account.post(withdrawalTrx);
        accountRepository.save(account);
        assertEquals(40.0, account.getBalance(), 0.001);
        assertEquals(2, account.getTransactions().size());
    }
}