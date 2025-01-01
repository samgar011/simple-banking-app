package com.eteration.simplebanking.dto.response;

public class CreditDebitResponse {
    private String status;
    private String approvalCode;

    // Constructors
    public CreditDebitResponse() {}

    public CreditDebitResponse(String status, String approvalCode) {
        this.status = status;
        this.approvalCode = approvalCode;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getApprovalCode() {
        return approvalCode;
    }

    public void setApprovalCode(String approvalCode) {
        this.approvalCode = approvalCode;
    }
}
