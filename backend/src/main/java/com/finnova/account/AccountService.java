package com.finnova.account;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@Service
public class AccountService {

    private final AccountRepository accounts;
    private final LedgerEntryRepository ledger;

    public AccountService(AccountRepository accounts, LedgerEntryRepository ledger) {
        this.accounts = accounts;
        this.ledger = ledger;
    }

    @Transactional(readOnly = true)
    public List<Account> list() {
        return accounts.findAllByOrderByIdAsc();
    }

    @Transactional(readOnly = true)
    public Account get(Long id) {
        return accounts.findById(id).orElseThrow(() -> new AccountNotFoundException(id));
    }

    @Transactional(readOnly = true)
    public List<LedgerEntry> statement(Long accountId) {
        get(accountId); // 404 if the account does not exist
        return ledger.findByAccountIdOrderByIdDesc(accountId);
    }

    @Transactional
    public Account create(String ownerName, BigDecimal initialDeposit) {
        Account account = accounts.save(new Account(ownerName.trim(), newAccountNumber()));
        if (initialDeposit != null && initialDeposit.signum() > 0) {
            account.credit(initialDeposit);
            ledger.save(new LedgerEntry(account.getId(), EntryType.DEPOSIT, initialDeposit,
                    account.getBalance(), "Opening deposit", null));
        }
        return account;
    }

    @Transactional
    public Account deposit(Long id, BigDecimal amount, String description) {
        Account account = get(id);
        account.credit(amount);
        ledger.save(new LedgerEntry(id, EntryType.DEPOSIT, amount, account.getBalance(),
                blankToNull(description), null));
        return account;
    }

    @Transactional
    public Account withdraw(Long id, BigDecimal amount, String description) {
        Account account = get(id);
        account.debit(amount);
        ledger.save(new LedgerEntry(id, EntryType.WITHDRAWAL, amount, account.getBalance(),
                blankToNull(description), null));
        return account;
    }

    /** Both legs happen in ONE database transaction: either both succeed or neither does. */
    @Transactional
    public Account transfer(Long fromId, Long toId, BigDecimal amount, String description) {
        if (fromId.equals(toId)) {
            throw new IllegalArgumentException("Cannot transfer to the same account");
        }
        Account from = get(fromId);
        Account to = get(toId);

        from.debit(amount);
        to.credit(amount);

        String note = blankToNull(description);
        ledger.save(new LedgerEntry(fromId, EntryType.TRANSFER_OUT, amount, from.getBalance(),
                note, to.getAccountNumber()));
        ledger.save(new LedgerEntry(toId, EntryType.TRANSFER_IN, amount, to.getBalance(),
                note, from.getAccountNumber()));
        return from;
    }

    private String newAccountNumber() {
        String number;
        do {
            number = "FN" + ThreadLocalRandom.current().nextLong(1_000_000_000L, 10_000_000_000L);
        } while (accounts.existsByAccountNumber(number));
        return number;
    }

    private static String blankToNull(String s) {
        return (s == null || s.isBlank()) ? null : s.trim();
    }
}
