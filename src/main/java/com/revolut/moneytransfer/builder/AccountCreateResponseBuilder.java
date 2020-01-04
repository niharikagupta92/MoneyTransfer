package com.revolut.moneytransfer.builder;

import com.revolut.moneytransfer.protocols.account.AccountCreateResponse;

public class AccountCreateResponseBuilder {
    private AccountCreateResponse response;

    public AccountCreateResponseBuilder() {
        this.response = new AccountCreateResponse();
    }

    public AccountCreateResponseBuilder accountId(String accountId) {
        response.setAccountId(accountId);
        return this;
    }

    public AccountCreateResponse build() {
        return response;
    }
}
