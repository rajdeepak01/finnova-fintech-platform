package com.finnova.account;

import com.finnova.account.AccountDtos.*;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/accounts")
public class AccountController {

    private final AccountService service;

    public AccountController(AccountService service) {
        this.service = service;
    }

    @GetMapping
    public List<AccountResponse> list() {
        return service.list().stream().map(AccountResponse::from).toList();
    }

    @GetMapping("/{id}")
    public AccountResponse get(@PathVariable Long id) {
        return AccountResponse.from(service.get(id));
    }

    @GetMapping("/{id}/transactions")
    public List<LedgerEntryResponse> transactions(@PathVariable Long id) {
        return service.statement(id).stream().map(LedgerEntryResponse::from).toList();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AccountResponse create(@Valid @RequestBody CreateAccountRequest req) {
        BigDecimal initial = req.initialDeposit() == null ? BigDecimal.ZERO : req.initialDeposit();
        return AccountResponse.from(service.create(req.ownerName(), initial));
    }

    @PostMapping("/{id}/deposit")
    public AccountResponse deposit(@PathVariable Long id, @Valid @RequestBody MoneyRequest req) {
        return AccountResponse.from(service.deposit(id, req.amount(), req.description()));
    }

    @PostMapping("/{id}/withdraw")
    public AccountResponse withdraw(@PathVariable Long id, @Valid @RequestBody MoneyRequest req) {
        return AccountResponse.from(service.withdraw(id, req.amount(), req.description()));
    }

    @PostMapping("/{id}/transfer")
    public AccountResponse transfer(@PathVariable Long id, @Valid @RequestBody TransferRequest req) {
        return AccountResponse.from(service.transfer(id, req.toAccountId(), req.amount(), req.description()));
    }
}
