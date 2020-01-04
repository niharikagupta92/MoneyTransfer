package com.revolut.moneytransfer.builder;

import com.revolut.moneytransfer.protocols.account.Account;
import com.revolut.moneytransfer.protocols.currency.Currency;

public class AccountBuilder {
    private Account account;

    public AccountBuilder() {
        this.account = new Account();
    }

    public AccountBuilder accountId(String accountId){
        account.setAccountId(accountId);
        return this;
    }

    public AccountBuilder balance(Double balance) {
        account.setBalance(balance);
        return this;
    }

    public AccountBuilder currency(Currency currency) {
        account.setCurrency(currency);
        return this;
    }

    public Account build() {
        return account;
    }
}
