package com.revolut.moneytransfer.repo;

import com.revolut.moneytransfer.constants.DBConstants;
import com.revolut.moneytransfer.db.H2DataUtil;
import com.revolut.moneytransfer.exception.AccountsError;
import com.revolut.moneytransfer.exception.AccountsException;
import com.revolut.moneytransfer.protocols.account.Account;
import com.revolut.moneytransfer.protocols.currency.Currency;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class AccountRepository {
    private final Logger logger = LoggerFactory.getLogger(AccountRepository.class);
    private H2DataUtil db;

    public AccountRepository() {
        db = H2DataUtil.getInstance();
        String createTableQuery = "CREATE TABLE IF NOT EXISTS " + DBConstants.ACCOUNT_TABLE_NAME +
                " (accountId varchar(100), balance decimal(18,2), currency varchar(10)) ";
        logger.info("Creating Accounts table: " + createTableQuery);
        int response = db.insertUpdateQuery(createTableQuery);
        logger.info("Create Account table response: " + response);
        if (response != 0) {
            throw new AccountsException(AccountsError.ACCOUNTS_TABLE_NOT_EXIST);
        }
    }

    public Account saveAccount(Account account) {
        String saveQuery = "insert into " + DBConstants.ACCOUNT_TABLE_NAME + " (" +
                "accountId, " +
                "balance, " +
                "currency) " +
                "values ( " +
                "'" + account.getAccountId() + "' , " +
                account.getBalance() + ", '" +
                account.getCurrency().getPath() + "')";
        logger.info("Insert Account in table: " + saveQuery);

        int response = db.insertUpdateQuery(saveQuery);
        logger.info("Insert Account table response: " + response);

        if (response != 1) {
            throw new AccountsException(AccountsError.ACCOUNT_CREATION_FAILED);
        }
        return account;
    }

    public Account getAccountFromAccountNum(String accountId) {
        String getQuery = "select accountId, " +
                "balance, " +
                "currency " +
                "from " + DBConstants.ACCOUNT_TABLE_NAME + " " +
                "where accountId = '" + accountId + "'";
        logger.info("Get account from table for accountId " + accountId + ": " + getQuery);
        ArrayList<HashMap> rs = db.selectQuery(getQuery);
        List<Account> accounts = new ArrayList<>();


        for (HashMap r : rs) {
            Account acc = new Account();
            acc.setAccountId((String) r.get("ACCOUNTID"));
            acc.setCurrency(Currency.valueOf((String) r.get("CURRENCY")));
            acc.setBalance(((BigDecimal) r.get("BALANCE")).doubleValue());
            logger.info("Account result row from table: " + acc.toString());
            accounts.add(acc);
        }

        return accounts.isEmpty() ? null : accounts.get(0);
    }

    public void updateAccountBalance(String accountId, Double balance) {
        String updateQuery = "UPDATE " + DBConstants.ACCOUNT_TABLE_NAME + " " +
                "SET balance = " + balance +
                " WHERE accountId = '" + accountId + "'";
        logger.info("Update account row for accountId " + accountId + ": " + updateQuery);
        int modifiedRows = db.insertUpdateQuery(updateQuery);
        logger.info("Number of rows updated for accountId " + accountId + ": " + modifiedRows);
        System.out.println("No of rows affected = " + modifiedRows);
    }
}