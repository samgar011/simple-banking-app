

```

Clone the Repository

    git clone https://github.com/samgar011/simple-banking-app.git
    cd banking-app
    
    ## Run Project
    
    ./gradlew clean build
    ./gradlew bootRun
    
    ## Access Swagger UI
    
    http://localhost:8080/swagger-ui.html
    
    ## Create a New Account
    
    POST /account/v1/create
    Request Body:
    {
      "owner": "John Doe",
      "accountNumber": "12345"
    }
    
    ## Credit Account Example
    
    Endpoint
    
    POST /account/v1/credit/{accountNumber}
    Request
    Path Parameter: accountNumber
    Example: 12345
    
    {
      "amount": 1000.0
    }
    
    Expected Response
    
    Success (200 OK):
    
    {
      "status": "OK",
      "approvalCode": "b2341c5a-3d56-4a77-bf48-b8b2a53e3e7d"
    }
    
    Failure (400 Bad Request):
    json
    
    {
      "status": "FAILED: Account not found",
      "approvalCode": null
    }
    
     ## Debit Account Example
    
    POST /account/v1/debit/{accountNumber}
    Request
    Path Parameter: accountNumber
    
    Example: 12345
    
    {
      "amount": 500.0
    }
    Expected Response
    Success (200 OK):
    
    {
      "status": "OK",
      "approvalCode": "a8d9f4bc-234d-4e92-8d72-9c4b2a1d73a9"
    }
    Failure (400 Bad Request):
    
    {
      "status": "FAILED: Insufficient balance",
      "approvalCode": null
    }
    
    ## Get Request
    
    Expected Response
    
    {
      "accountNumber": "12345",
      "owner": "John Doe",
      "balance": 5000.0,
      "createDate": "2025-01-01T13:34:05.472842",
      "transactions": [
        {
          "date": "2025-01-01T14:54:15.56622",
          "amount": 500.0,
          "approvalCode": "ac831b23-bfd6-41d7-bfd8-15666f9f3b36",
          "transactionType": "Deposit transaction for adding funds."
        },
        {
          "date": "2025-01-01T13:35:44.577273",
          "amount": 200.0,
          "approvalCode": "ac831b23-bfd6-41d7-bfd8-15666f9f3b36",
          "transactionType": "Withdrawal transaction for deducting funds."
        }
      ]
    }





