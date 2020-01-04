package com.revolut.moneytransfer.repo;

import com.revolut.moneytransfer.protocols.account.Account;

import java.util.HashMap;
import java.util.Map;

public class AccountRepositoryMock extends AccountRepository{

    private Map<String, Account> accountStore;

    public AccountRepositoryMock(){
        accountStore=new HashMap<>();
    }

    @Override
    public Account saveAccount(Account account) {
        accountStore.put(account.getAccountId(),account);
        return account;
    }

    @Override
    public Account getAccountFromAccountNum(String accountId) {
        return accountStore.getOrDefault(accountId,null);
    }

    @Override
    public void updateAccountBalance(String accountId, Double balance) {
        Account account = accountStore.get(accountId);
        account.setBalance(balance);
        accountStore.put(accountId,account);
    }
}
