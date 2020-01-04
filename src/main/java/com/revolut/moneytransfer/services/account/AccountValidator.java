package com.revolut.moneytransfer.services.account;

import com.revolut.moneytransfer.exception.AccountsError;
import com.revolut.moneytransfer.exception.AccountsException;
import com.revolut.moneytransfer.protocols.account.AccountCreateRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class AccountValidator {

    private final Logger logger = LoggerFactory.getLogger(AccountValidator.class);

    void validateAccountRequest(AccountCreateRequest accountCreateRequest) {
        if (accountCreateRequest.getBalance() == null || accountCreateRequest.getBalance() < 0) {
            logger.error("Balance is NULL or balance is negative");
            throw new AccountsException(AccountsError.BALANCE_NEGATIVE);
        }
        if (accountCreateRequest.getCurrency() == null) {
            throw new AccountsException(AccountsError.CURRENCY_NOT_SUPPORTED);
        }
    }
}
