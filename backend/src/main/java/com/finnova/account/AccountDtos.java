package com.finnova.account;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.Instant;

public final class AccountDtos {

    private AccountDtos() {}

    public record CreateAccountRequest(
            @NotBlank(message = "Owner name is required")
            @Size(max = 60, message = "Owner name must be at most 60 characters")
            String ownerName,

            @DecimalMin(value = "0.00", message = "Initial deposit cannot be negative")
            @Digits(integer = 12, fraction = 2, message = "Invalid amount")
            BigDecimal initialDeposit) {}

    public record MoneyRequest(
            @NotNull(message = "Amount is required")
            @DecimalMin(value = "0.01", message = "Amount must be at least 0.01")
            @Digits(integer = 12, fraction = 2, message = "Invalid amount (max 2 decimals)")
            BigDecimal amount,

            @Size(max = 100, message = "Description must be at most 100 characters")
            String description) {}

    public record TransferRequest(
            @NotNull(message = "Destination account is required")
            Long toAccountId,

            @NotNull(message = "Amount is required")
            @DecimalMin(value = "0.01", message = "Amount must be at least 0.01")
            @Digits(integer = 12, fraction = 2, message = "Invalid amount (max 2 decimals)")
            BigDecimal amount,

            @Size(max = 100, message = "Description must be at most 100 characters")
            String description) {}

    public record AccountResponse(Long id, String ownerName, String accountNumber,
                                  BigDecimal balance, Instant createdAt) {
        public static AccountResponse from(Account a) {
            return new AccountResponse(a.getId(), a.getOwnerName(), a.getAccountNumber(),
                    a.getBalance(), a.getCreatedAt());
        }
    }

    public record LedgerEntryResponse(Long id, EntryType type, BigDecimal amount, BigDecimal balanceAfter,
                                      String description, String counterpartAccountNumber, Instant createdAt) {
        public static LedgerEntryResponse from(LedgerEntry e) {
            return new LedgerEntryResponse(e.getId(), e.getType(), e.getAmount(), e.getBalanceAfter(),
                    e.getDescription(), e.getCounterpartAccountNumber(), e.getCreatedAt());
        }
    }
}
