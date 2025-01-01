package com.eteration.simplebanking.controller;

import com.eteration.simplebanking.dto.request.CreateAccountRequest;
import com.eteration.simplebanking.dto.request.PhoneBillPaymentRequest;
import com.eteration.simplebanking.dto.request.CreditDebitRequest;
import com.eteration.simplebanking.dto.response.BankAccountResponse;
import com.eteration.simplebanking.dto.response.CreateAccountResponse;
import com.eteration.simplebanking.dto.response.CreditDebitResponse;
import com.eteration.simplebanking.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/account/v1")
public class AccountController {

    private final AccountService accountService;

    @Autowired
    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @PostMapping("/credit/{accountNumber}")
    public ResponseEntity<CreditDebitResponse> creditAccount(
            @PathVariable String accountNumber,
            @RequestBody CreditDebitRequest request) {
        try {
            CreditDebitResponse response = accountService.creditAccount(accountNumber, request.getAmount());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            CreditDebitResponse response = new CreditDebitResponse("FAILED: " + e.getMessage(), null);
            return ResponseEntity.badRequest().body(response);
        }
    }

    @PostMapping("/debit/{accountNumber}")
    public ResponseEntity<CreditDebitResponse> debitAccount(
            @PathVariable String accountNumber,
            @RequestBody CreditDebitRequest request) {
        try {
            CreditDebitResponse response = accountService.debitAccount(accountNumber,
                    request.getAmount());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            CreditDebitResponse response = new CreditDebitResponse("FAILED: "
                    + e.getMessage(), null);
            return ResponseEntity.badRequest().body(response);
        }
    }

    @PostMapping("/phoneBillPayment/{accountNumber}")
    public ResponseEntity<CreditDebitResponse> phoneBillPayment(
            @PathVariable String accountNumber,
            @RequestBody PhoneBillPaymentRequest request) {
        try {
            CreditDebitResponse response = accountService.phoneBillPayment(accountNumber,
                    request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            CreditDebitResponse response = new CreditDebitResponse("FAILED: "
                    + e.getMessage(), null);
            return ResponseEntity.badRequest().body(response);
        }
    }

    @GetMapping("/{accountNumber}")
    public ResponseEntity<BankAccountResponse> getAccountDetails(
            @PathVariable String accountNumber) {
        try {
            BankAccountResponse response = accountService
                    .getAccountDetailsResponse(accountNumber);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/create")
    public ResponseEntity<CreateAccountResponse> createAccount(
            @RequestBody CreateAccountRequest request) {
        try {
            CreateAccountResponse response = accountService.createAccount(request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            CreateAccountResponse response = new CreateAccountResponse("FAILED: "
                    + e.getMessage(), null);
            return ResponseEntity.badRequest().body(response);
        }
    }
}