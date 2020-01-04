package com.revolut.moneytransfer.protocols.account;

import com.google.gson.Gson;
import com.revolut.moneytransfer.protocols.currency.Currency;

public class Account {

    String accountId;
    Double balance;
    Currency currency;

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    public Double getBalance() {
        return balance;
    }

    public void setBalance(Double balance) {
        this.balance = balance;
    }

    public Currency getCurrency() {
        return currency;
    }

    public void setCurrency(Currency currency) {
        this.currency = currency;
    }

    @Override
    public String toString() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }
}
