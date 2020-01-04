package com.revolut.moneytransfer.protocols.account;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.revolut.moneytransfer.protocols.currency.Currency;

public class AccountCreateRequest {

    Double balance;
    Currency currency;

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
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        return gson.toJson(this);
    }
}
