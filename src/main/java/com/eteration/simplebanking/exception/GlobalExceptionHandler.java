package com.eteration.simplebanking.exception;

import com.eteration.simplebanking.dto.response.CreditDebitResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(InsufficientBalanceException.class)
    public ResponseEntity<CreditDebitResponse> handleInsufficientBalance(
            InsufficientBalanceException ex) {
        CreditDebitResponse response = new CreditDebitResponse("FAILED: "
                + ex.getMessage(), null);
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<CreditDebitResponse> handleGeneralException(Exception ex) {
        CreditDebitResponse response = new CreditDebitResponse("FAILED: "
                + ex.getMessage(), null);
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }
}
