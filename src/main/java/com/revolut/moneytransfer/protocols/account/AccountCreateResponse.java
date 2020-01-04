package com.revolut.moneytransfer.protocols.account;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class AccountCreateResponse {

    String accountId;

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    @Override
    public String toString() {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        return gson.toJson(this);
    }
}
