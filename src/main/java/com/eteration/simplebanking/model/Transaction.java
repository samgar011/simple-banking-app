package com.eteration.simplebanking.model;




import com.eteration.simplebanking.exception.InsufficientBalanceException;
import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "transaction_type")
@Table(name = "transactions")
public abstract class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDateTime date;

    @Column(nullable = false)
    private double amount;

    @Column(name = "approval_code", unique = true, nullable = false, updatable = false)
    private String approvalCode;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_number", referencedColumnName = "account_number")
    private Account account;

    @Transient
    private String type;

    @Transient
    private String description;

    public Transaction() {
        this.date = LocalDateTime.now();
    }

    public Transaction(double amount, Account account) {
        this();
        this.amount = amount;
        this.account = account;
    }

    public Long getId() {
        return id;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public double getAmount() {
        return amount;
    }

    public String getApprovalCode() {
        return approvalCode;
    }

    public Account getAccount() {
        return account;
    }

    public String getType() {
        return this.getClass().getSimpleName();
    }

    public String getDescription() {
        return description != null ? description : "No description available";
    }

    public void setBankAccount(Account account) {
        this.account = account;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @PrePersist
    public void generateApprovalCode() {
        if (this.approvalCode == null) {
            this.approvalCode = UUID.randomUUID().toString();
        }
    }
    public abstract void process(Account account) throws InsufficientBalanceException;
}
