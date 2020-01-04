package com.revolut.moneytransfer.services.transfer;

import com.revolut.moneytransfer.builder.AccountBuilder;
import com.revolut.moneytransfer.exception.TransferError;
import com.revolut.moneytransfer.exception.TransferException;
import com.revolut.moneytransfer.protocols.account.Account;
import com.revolut.moneytransfer.protocols.currency.Currency;
import com.revolut.moneytransfer.protocols.transfer.TransferRequest;
import com.revolut.moneytransfer.repo.AccountRepositoryMock;
import com.revolut.moneytransfer.services.currency.CurrencyConverter;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class TransferValidatorTest {
    private AccountRepositoryMock accountRepositoryMock;
    private TransferValidator transferValidator;
    private TransferRequest transferRequest;
    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Before
    public void setup() {
        accountRepositoryMock = new AccountRepositoryMock();
        transferValidator = new TransferValidator(accountRepositoryMock, new CurrencyConverter());
        Account account1 = new AccountBuilder()
                .accountId("1")
                .balance(100.0)
                .currency(Currency.SGD)
                .build();

        Account account2 = new AccountBuilder()
                .accountId("2")
                .balance(10.0)
                .currency(Currency.SGD)
                .build();

        Account account3 = new AccountBuilder()
                .accountId("3")
                .balance(10.0)
                .currency(Currency.USD)
                .build();

        accountRepositoryMock.saveAccount(account1);
        accountRepositoryMock.saveAccount(account2);
        accountRepositoryMock.saveAccount(account3);
    }

    @Test
    public void validateShouldThrowExceptionIfNullAmount() {
        exception.expect(TransferException.class);
        exception.expectMessage(TransferError.INVLAID_TRANSFER_AMOUNT.getDescription());

        transferRequest = new TransferRequest();
        transferRequest.setAmount(null);
        transferValidator.validateTransferRequest(transferRequest);
    }

    @Test
    public void validateShouldThrowExceptionIfNegativeAmount() {
        exception.expect(TransferException.class);
        exception.expectMessage(TransferError.INVLAID_TRANSFER_AMOUNT.getDescription());

        transferRequest = new TransferRequest();
        transferRequest.setAmount(-10.0);
        transferValidator.validateTransferRequest(transferRequest);
    }

    @Test
    public void validateShouldThrowExceptionIfNullSender() {
        exception.expect(TransferException.class);
        exception.expectMessage(TransferError.SENDER_INVALID.getDescription());

        transferRequest = new TransferRequest();
        transferRequest.setAmount(100.0);
        transferRequest.setSender(null);
        transferValidator.validateTransferRequest(transferRequest);
    }

    @Test
    public void validateShouldThrowExceptionIfNullReceiver() {
        exception.expect(TransferException.class);
        exception.expectMessage(TransferError.RECEIVER_INVALID.getDescription());

        transferRequest = new TransferRequest();
        transferRequest.setAmount(100.0);
        transferRequest.setSender("1");
        transferRequest.setReceiver(null);
        transferValidator.validateTransferRequest(transferRequest);
    }

    @Test
    public void validateShouldThrowExceptionIfNullCurrency() {
        exception.expect(TransferException.class);
        exception.expectMessage(TransferError.INVALID_CURRENCY.getDescription());

        transferRequest = new TransferRequest();
        transferRequest.setAmount(100.0);
        transferRequest.setSender("1");
        transferRequest.setReceiver("2");
        transferRequest.setCurrency(null);
        transferValidator.validateTransferRequest(transferRequest);
    }

    @Test
    public void validateShouldThrowExceptionIfInvalidSender() {
        exception.expect(TransferException.class);
        exception.expectMessage(TransferError.SENDER_INVALID.getDescription());

        transferRequest = new TransferRequest();
        transferRequest.setAmount(100.0);
        transferRequest.setSender("10");
        transferRequest.setReceiver("2");
        transferRequest.setCurrency(Currency.SGD);
        transferValidator.validateTransferRequest(transferRequest);
    }

    @Test
    public void validateShouldThrowExceptionIfInvalidReceiver() {
        exception.expect(TransferException.class);
        exception.expectMessage(TransferError.RECEIVER_INVALID.getDescription());

        transferRequest = new TransferRequest();
        transferRequest.setAmount(100.0);
        transferRequest.setSender("1");
        transferRequest.setReceiver("20");
        transferRequest.setCurrency(Currency.SGD);
        transferValidator.validateTransferRequest(transferRequest);
    }

    @Test
    public void validateShouldThrowExceptionIfCurrencyDifferentFromSender() {
        exception.expect(TransferException.class);
        exception.expectMessage(TransferError.CURRENCY_NEITHER_SENDER_NOR_RECEIVER.getDescription());

        transferRequest = new TransferRequest();
        transferRequest.setAmount(100.0);
        transferRequest.setSender("1");
        transferRequest.setReceiver("2");
        transferRequest.setCurrency(Currency.EUR);
        transferValidator.validateTransferRequest(transferRequest);
    }

    @Test
    public void validateShouldThrowExceptionIfCurrencyDifferentFromSenderReceiver() {
        exception.expect(TransferException.class);
        exception.expectMessage(TransferError.CURRENCY_NEITHER_SENDER_NOR_RECEIVER.getDescription());

        transferRequest = new TransferRequest();
        transferRequest.setAmount(100.0);
        transferRequest.setSender("1");
        transferRequest.setReceiver("3");
        transferRequest.setCurrency(Currency.EUR);
        transferValidator.validateTransferRequest(transferRequest);
    }

    @Test
    public void validateShouldThrowExceptionIfInsufficientBalance() {
        exception.expect(TransferException.class);
        exception.expectMessage(TransferError.SENDER_INSUFFICIENT_BALANCE.getDescription());

        transferRequest = new TransferRequest();
        transferRequest.setAmount(101.0);
        transferRequest.setSender("1");
        transferRequest.setReceiver("2");
        transferRequest.setCurrency(Currency.SGD);
        transferValidator.validateTransferRequest(transferRequest);
    }

    @Test
    public void validateShouldThrowExceptionIfInsufficientBalanceAfterCurrencyConversion() {
        exception.expect(TransferException.class);
        exception.expectMessage(TransferError.SENDER_INSUFFICIENT_BALANCE.getDescription());

        transferRequest = new TransferRequest();
        transferRequest.setAmount(100.0);
        transferRequest.setSender("1");
        transferRequest.setReceiver("3");
        transferRequest.setCurrency(Currency.USD);
        transferValidator.validateTransferRequest(transferRequest);
    }

    @Test
    public void validateShouldNotThrowException() {

        transferRequest = new TransferRequest();
        transferRequest.setAmount(10.0);
        transferRequest.setSender("1");
        transferRequest.setReceiver("2");
        transferRequest.setCurrency(Currency.SGD);
        transferValidator.validateTransferRequest(transferRequest);
    }
}
