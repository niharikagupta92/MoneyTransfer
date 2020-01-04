package com.revolut.moneytransfer.services.account;

import com.revolut.moneytransfer.exception.AccountsError;
import com.revolut.moneytransfer.exception.AccountsException;
import com.revolut.moneytransfer.protocols.account.Account;
import com.revolut.moneytransfer.protocols.account.AccountCreateRequest;
import com.revolut.moneytransfer.protocols.account.AccountCreateResponse;
import com.revolut.moneytransfer.protocols.currency.Currency;
import com.revolut.moneytransfer.repo.AccountRepositoryMock;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class AccountServiceTest {

    private AccountService accountService;
    private AccountRepositoryMock accountRepositoryMock;

    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Before
    public void setup() {
        accountService = new AccountService();
        accountRepositoryMock = new AccountRepositoryMock();
        accountService.accountRepository = accountRepositoryMock;
    }

    @Test
    public void createAccountShouldSaveAccountIfValid() {
        AccountCreateRequest accountCreateRequest = new AccountCreateRequest();
        accountCreateRequest.setCurrency(Currency.SGD);
        accountCreateRequest.setBalance(1021.0);

        AccountCreateResponse accountCreateResponse = accountService.createAccount(accountCreateRequest);
        assertNotNull("Account Id is Null", accountCreateResponse.getAccountId());
        Account account = accountRepositoryMock.getAccountFromAccountNum(accountCreateResponse.getAccountId());
        assertEquals("Balance Assertion Fail", accountCreateRequest.getBalance(), account.getBalance());
        assertEquals("Currency Assertion Fail", accountCreateRequest.getCurrency(), account.getCurrency());

    }

    @Test
    public void getAccountBalanceShouldReturnCurrentBalance() {
        AccountCreateRequest accountCreateRequest = new AccountCreateRequest();
        accountCreateRequest.setCurrency(Currency.SGD);
        accountCreateRequest.setBalance(1021.0);

        AccountCreateResponse accountCreateResponse = accountService.createAccount(accountCreateRequest);
        String balanceWithCurrency = accountService.getAccountBalance(accountCreateResponse.getAccountId());
        String expectedBalanceWithCurrency = "SGD 1021.0";
        assertEquals("Balance Assertion Fail", balanceWithCurrency, expectedBalanceWithCurrency);

    }

    @Test
    public void getAccountBalanceShouldThrowExceptionIfAccountDoesNotExist() {
        exception.expect(AccountsException.class);
        exception.expectMessage(AccountsError.ACCOUNT_NOT_EXIST.getDescription());
        accountService.getAccountBalance("1");
    }

}
