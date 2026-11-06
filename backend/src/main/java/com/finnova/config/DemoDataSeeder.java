package com.finnova.config;

import com.finnova.account.AccountRepository;
import com.finnova.account.AccountService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

/** H2 is in-memory, so data resets on every restart. Seed two demo accounts so the UI is never empty. */
@Component
public class DemoDataSeeder implements CommandLineRunner {

    private final AccountRepository accounts;
    private final AccountService service;

    public DemoDataSeeder(AccountRepository accounts, AccountService service) {
        this.accounts = accounts;
        this.service = service;
    }

    @Override
    public void run(String... args) {
        if (accounts.count() == 0) {
            service.create("Alice Demo", new BigDecimal("5000.00"));
            service.create("Bob Demo", new BigDecimal("2500.00"));
        }
    }
}
