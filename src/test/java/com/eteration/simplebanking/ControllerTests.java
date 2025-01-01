package com.eteration.simplebanking;


import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import com.eteration.simplebanking.controller.AccountController;
import com.eteration.simplebanking.dto.request.CreditDebitRequest;
import com.eteration.simplebanking.dto.request.PhoneBillPaymentRequest;
import com.eteration.simplebanking.dto.response.BankAccountResponse;
import com.eteration.simplebanking.dto.response.CreditDebitResponse;
import com.eteration.simplebanking.dto.response.TransactionResponse;
import com.eteration.simplebanking.exception.InsufficientBalanceException;
import com.eteration.simplebanking.model.Account;
import com.eteration.simplebanking.model.DepositTransaction;
import com.eteration.simplebanking.model.Transaction;
import com.eteration.simplebanking.model.WithdrawalTransaction;
import com.eteration.simplebanking.service.AccountService;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@SpringBootTest
@AutoConfigureMockMvc
public class ControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AccountService service;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void givenId_Credit_thenReturnJson() throws Exception {
        String accountNumber = "17892";
        double amount = 1000.0;
        String approvalCode = UUID.randomUUID().toString();

        when(service.creditAccount(accountNumber, amount))
                .thenReturn(new CreditDebitResponse("OK", approvalCode));

        CreditDebitRequest request = new CreditDebitRequest(amount);
        String jsonRequest = objectMapper.writeValueAsString(request);

        mockMvc.perform(post("/account/v1/credit/" + accountNumber)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.approvalCode").value(approvalCode));

        verify(service, times(1)).creditAccount(accountNumber, amount);
    }

    @Test
    public void givenId_CreditAndThenDebit_thenReturnJson() throws Exception {
        String accountNumber = "17892";
        double creditAmount = 1000.0;
        double debitAmount = 50.0;
        String approvalCodeCredit = UUID.randomUUID().toString();
        String approvalCodeDebit = UUID.randomUUID().toString();

        when(service.creditAccount(accountNumber, creditAmount))
                .thenReturn(new CreditDebitResponse("OK", approvalCodeCredit));
        when(service.debitAccount(accountNumber, debitAmount))
                .thenReturn(new CreditDebitResponse("OK", approvalCodeDebit));

        mockMvc.perform(post("/account/v1/credit/" + accountNumber)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new CreditDebitRequest(creditAmount))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.approvalCode").value(approvalCodeCredit));

        mockMvc.perform(post("/account/v1/debit/" + accountNumber)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new CreditDebitRequest(debitAmount))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.approvalCode").value(approvalCodeDebit));

        verify(service, times(1)).creditAccount(accountNumber, creditAmount);
        verify(service, times(1)).debitAccount(accountNumber, debitAmount);
    }

    @Test
    public void givenId_CreditAndThenDebitMoreGetException_thenReturnJson() throws Exception {
        String accountNumber = "17892";
        double debitAmount = 5000.0;
        when(service.debitAccount(accountNumber, debitAmount))
                .thenThrow(new InsufficientBalanceException("Insufficient balance"));

        CreditDebitRequest debitRequest = new CreditDebitRequest(debitAmount);
        String jsonDebitRequest = objectMapper.writeValueAsString(debitRequest);

        mockMvc.perform(post("/account/v1/debit/" + accountNumber)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonDebitRequest))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value("FAILED: Insufficient balance"))
                .andExpect(jsonPath("$.approvalCode").doesNotExist());

        verify(service, times(1)).debitAccount(accountNumber, debitAmount);
    }

    @Test
    public void givenId_GetAccount_thenReturnJson() throws Exception {
        String accountNumber = "17892";
        String owner = "Kerem Karaca";
        double expectedBalance = 950.0;

        // Mock transactions
        List<TransactionResponse> transactionResponses = List.of(
                new TransactionResponse(LocalDateTime.now(), 1000.0, "ApprovalCode1", "DEPOSIT"),
                new TransactionResponse(LocalDateTime.now(), 50.0, "ApprovalCode2", "WITHDRAWAL")
        );

        // Mock service response
        BankAccountResponse mockResponse = new BankAccountResponse(
                accountNumber,
                owner,
                expectedBalance,
                LocalDateTime.now(),
                transactionResponses
        );

        when(service.getAccountDetailsResponse(accountNumber)).thenReturn(mockResponse);

        // Perform GET request
        mockMvc.perform(get("/account/v1/" + accountNumber)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accountNumber").value(accountNumber))
                .andExpect(jsonPath("$.owner").value(owner))
                .andExpect(jsonPath("$.balance").value(expectedBalance))
                .andExpect(jsonPath("$.transactions").isArray())
                .andExpect(jsonPath("$.transactions[0].transactionType").value("DEPOSIT"))
                .andExpect(jsonPath("$.transactions[1].transactionType").value("WITHDRAWAL"));

        verify(service, times(1)).getAccountDetailsResponse(accountNumber);
    }

    // Test for Phone Bill Payment Transaction
    @Test
    public void givenId_PhoneBillPayment_thenReturnJson() throws Exception {
        String accountNumber = "1234";
        double amount = 60.0;
        String serviceProvider = "Verizon";
        String phoneNumber = "555-1234";
        String approvalCode = UUID.randomUUID().toString();

        // Mock service response
        when(service.phoneBillPayment(eq(accountNumber), any(PhoneBillPaymentRequest.class)))
                .thenReturn(new CreditDebitResponse("OK", approvalCode));

        PhoneBillPaymentRequest request = new PhoneBillPaymentRequest(amount, serviceProvider, phoneNumber);
        String jsonRequest = objectMapper.writeValueAsString(request);

        mockMvc.perform(post("/account/v1/phoneBillPayment/" + accountNumber)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.approvalCode").value(approvalCode));

        verify(service, times(1)).phoneBillPayment(eq(accountNumber), any(PhoneBillPaymentRequest.class));
    }
}
