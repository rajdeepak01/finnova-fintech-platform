package com.finnova.account;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;

/** One immutable line in an account's statement. Never updated after creation. */
@Entity
@Table(name = "ledger_entries")
public class LedgerEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long accountId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EntryType type;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal amount;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal balanceAfter;

    private String description;

    /** For transfers: the account on the other side. */
    private String counterpartAccountNumber;

    @Column(nullable = false)
    private Instant createdAt = Instant.now();

    protected LedgerEntry() {}

    public LedgerEntry(Long accountId, EntryType type, BigDecimal amount, BigDecimal balanceAfter,
                       String description, String counterpartAccountNumber) {
        this.accountId = accountId;
        this.type = type;
        this.amount = amount;
        this.balanceAfter = balanceAfter;
        this.description = description;
        this.counterpartAccountNumber = counterpartAccountNumber;
    }

    public Long getId() { return id; }
    public Long getAccountId() { return accountId; }
    public EntryType getType() { return type; }
    public BigDecimal getAmount() { return amount; }
    public BigDecimal getBalanceAfter() { return balanceAfter; }
    public String getDescription() { return description; }
    public String getCounterpartAccountNumber() { return counterpartAccountNumber; }
    public Instant getCreatedAt() { return createdAt; }
}
