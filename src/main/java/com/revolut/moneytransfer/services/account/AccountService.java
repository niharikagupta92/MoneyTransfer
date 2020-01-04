package com.revolut.moneytransfer.services.account;

import com.revolut.moneytransfer.builder.AccountBuilder;
import com.revolut.moneytransfer.builder.AccountCreateResponseBuilder;
import com.revolut.moneytransfer.exception.AccountsError;
import com.revolut.moneytransfer.exception.AccountsException;
import com.revolut.moneytransfer.protocols.account.Account;
import com.revolut.moneytransfer.protocols.account.AccountCreateRequest;
import com.revolut.moneytransfer.protocols.account.AccountCreateResponse;
import com.revolut.moneytransfer.repo.AccountRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;

public class AccountService {
    AccountRepository accountRepository;
    private final Logger logger = LoggerFactory.getLogger(AccountService.class);
    private AccountValidator accountValidator;

    public AccountService() {
        this.accountRepository = new AccountRepository();
        accountValidator = new AccountValidator();
    }

    public AccountCreateResponse createAccount(AccountCreateRequest accountCreateRequest) {
        logger.info("Received account creation request: " + accountCreateRequest.toString());
        accountValidator.validateAccountRequest(accountCreateRequest);

        Account a = new AccountBuilder()
                .accountId(String.valueOf(UUID.randomUUID()))
                .balance(accountCreateRequest.getBalance())
                .currency(accountCreateRequest.getCurrency())
                .build();

        Account savedAccount = accountRepository.saveAccount(a);
        logger.info("Account created successfully: " + a.getAccountId());
        return new AccountCreateResponseBuilder()
                .accountId(savedAccount.getAccountId())
                .build();
    }

    public String getAccountBalance(String accountId) {
        logger.info("Fetching account balance for accountId: " + accountId);
        Account acc = accountRepository.getAccountFromAccountNum(accountId);
        if (acc == null)
            throw new AccountsException(AccountsError.ACCOUNT_NOT_EXIST);

        logger.info("Fetched account: " + acc.toString());
        return acc.getCurrency().getPath() + " " + acc.getBalance().toString();

    }
}
