package com.revolut.moneytransfer.services.account;

import com.revolut.moneytransfer.exception.AccountsError;
import com.revolut.moneytransfer.exception.AccountsException;
import com.revolut.moneytransfer.protocols.account.AccountCreateRequest;
import com.revolut.moneytransfer.protocols.currency.Currency;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class AccountValidatorTest {

    private AccountCreateRequest accountCreateRequest;
    private AccountValidator accountValidator;

    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Before
    public void setup(){
        accountCreateRequest = new AccountCreateRequest();
        accountValidator = new AccountValidator();
    }

    @Test
    public void nullBalanceShouldThrowException(){

        exception.expect(AccountsException.class);
        exception.expectMessage(AccountsError.BALANCE_NEGATIVE.getDescription());

        accountCreateRequest.setBalance(null);
        accountValidator.validateAccountRequest(accountCreateRequest);

    }

    @Test
    public void negativeBalanceShouldThrowException(){

        exception.expect(AccountsException.class);
        exception.expectMessage(AccountsError.BALANCE_NEGATIVE.getDescription());

        accountCreateRequest.setBalance(-100.0);
        accountValidator.validateAccountRequest(accountCreateRequest);

    }

    @Test
    public void invalidCurrencyShouldThrowException(){
        exception.expect(AccountsException.class);
        exception.expectMessage(AccountsError.CURRENCY_NOT_SUPPORTED.getDescription());

        accountCreateRequest.setBalance(100.0);
        accountCreateRequest.setCurrency(null);
        accountValidator.validateAccountRequest(accountCreateRequest);
    }

    @Test
    public void validBalanceShouldNotThrowException(){
        accountCreateRequest.setBalance(100.0);
        accountCreateRequest.setCurrency(Currency.SGD);
        accountValidator.validateAccountRequest(accountCreateRequest);
    }

}
