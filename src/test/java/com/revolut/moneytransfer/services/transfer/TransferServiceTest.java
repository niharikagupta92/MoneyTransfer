package com.revolut.moneytransfer.services.transfer;

import com.revolut.moneytransfer.builder.AccountBuilder;
import com.revolut.moneytransfer.exception.AccountsError;
import com.revolut.moneytransfer.exception.AccountsException;
import com.revolut.moneytransfer.exception.TransferError;
import com.revolut.moneytransfer.exception.TransferException;
import com.revolut.moneytransfer.protocols.account.Account;
import com.revolut.moneytransfer.protocols.currency.Currency;
import com.revolut.moneytransfer.protocols.transfer.*;
import com.revolut.moneytransfer.repo.AccountRepositoryMock;
import com.revolut.moneytransfer.repo.TransferRepositoryMock;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class TransferServiceTest {
    private TransferRepositoryMock transferRepositoryMock;
    private TransferService transferService;
    private AccountRepositoryMock accountRepositoryMock;

    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Before
    public void setup() {
        transferService = new TransferService();
        transferRepositoryMock = new TransferRepositoryMock();
        accountRepositoryMock = new AccountRepositoryMock();
        transferService.transferRepository = transferRepositoryMock;
        transferService.accountRepository = accountRepositoryMock;
        transferService.transferValidator = new TransferValidator(accountRepositoryMock, transferService.currencyConverter);

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
    public void createTransferShouldCreateTransferIfValid() {
        TransferRequest transferRequest = new TransferRequest();
        transferRequest.setAmount(10.0);
        transferRequest.setCurrency(Currency.SGD);
        transferRequest.setSender("1");
        transferRequest.setReceiver("2");

        TransferResponse transferResponse = transferService.createTransfer(transferRequest);
        assertNotNull("Transfer Id is null", transferResponse.getTransferId());
        Transfer transfer = transferRepositoryMock.getTransactionById(transferResponse.getTransferId());
        assertEquals("Transfer amount mismatch", transferRequest.getAmount(), transfer.getAmount());
        assertEquals("Transfer Currency mismatch", transferRequest.getCurrency(), transfer.getCurrency());
        assertEquals("Transfer Sender mismatch", transferRequest.getSender(), transfer.getSenderAccount());
        assertEquals("Transfer Receiver mismatch", transferRequest.getReceiver(), transfer.getReceiverAccount());
    }

    @Test
    public void getTransactionShouldReturnTransferObjectForValidTransferId() {
        TransferRequest transferRequest = new TransferRequest();
        transferRequest.setAmount(10.0);
        transferRequest.setCurrency(Currency.SGD);
        transferRequest.setSender("1");
        transferRequest.setReceiver("2");

        TransferResponse transferResponse = transferService.createTransfer(transferRequest);
        TransferStatusResponse transfer = transferService.getTransaction(transferResponse.getTransferId());
        assertEquals("Transfer Status is not Pending", TransferStatus.Pending, transfer.getStatus());
    }

    @Test
    public void getTransactionShouldThrowExceptionIfTransferNotFound() {
        exception.expect(TransferException.class);
        exception.expectMessage(TransferError.TRANSFER_NOT_EXIST.getDescription());
        transferService.getTransaction("1");
    }

    @Test
    public void executeTransferShouldUpdateBalanceIfTransferValid() {
        TransferRequest transferRequest = new TransferRequest();
        transferRequest.setAmount(10.0);
        transferRequest.setCurrency(Currency.SGD);
        transferRequest.setSender("1");
        transferRequest.setReceiver("2");
        TransferResponse transferResponse = transferService.createTransfer(transferRequest);
        TransferStatusResponse transfer = transferService.getTransaction(transferResponse.getTransferId());
        transferService.executeTransfer(transferRepositoryMock.getTransactionById(transfer.getTransferId()));

        TransferStatusResponse updatedTransfer = transferService.getTransaction(transferResponse.getTransferId());
        assertEquals("Transfer status is incomplete", TransferStatus.Success, updatedTransfer.getStatus());
        Double expectedSenderBalance = 90.0;
        Double expectedReceiverBalance = 20.0;
        assertEquals("Sender balance is incorrect", expectedSenderBalance, accountRepositoryMock.getAccountFromAccountNum("1").getBalance());
        assertEquals("Receiver balance is incorrect", expectedReceiverBalance, accountRepositoryMock.getAccountFromAccountNum("2").getBalance());
    }

    @Test
    public void executeTransferShouldUpdateBalanceWithCurrencyConversion1() {
        TransferRequest transferRequest = new TransferRequest();
        transferRequest.setAmount(10.0);
        transferRequest.setCurrency(Currency.SGD);
        transferRequest.setSender("1");
        transferRequest.setReceiver("3");
        TransferResponse transferResponse = transferService.createTransfer(transferRequest);
        TransferStatusResponse transfer = transferService.getTransaction(transferResponse.getTransferId());
        transferService.executeTransfer(transferRepositoryMock.getTransactionById(transfer.getTransferId()));

        TransferStatusResponse updatedTransfer = transferService.getTransaction(transferResponse.getTransferId());
        assertEquals("Transfer status is incomplete", TransferStatus.Success, updatedTransfer.getStatus());
        Double expectedSenderBalance = 90.0;
        Double expectedReceiverBalance = 10.0 + transferService.currencyConverter.convertCurrency(Currency.SGD, Currency.USD, 10.0);
        assertEquals("Sender balance is incorrect", expectedSenderBalance, accountRepositoryMock.getAccountFromAccountNum("1").getBalance());
        assertEquals("Receiver balance is incorrect", expectedReceiverBalance, accountRepositoryMock.getAccountFromAccountNum("3").getBalance());
    }

    @Test
    public void executeTransferShouldUpdateBalanceWithCurrencyConversion2() {
        TransferRequest transferRequest = new TransferRequest();
        transferRequest.setAmount(10.0);
        transferRequest.setCurrency(Currency.USD);
        transferRequest.setSender("1");
        transferRequest.setReceiver("3");
        TransferResponse transferResponse = transferService.createTransfer(transferRequest);
        TransferStatusResponse transfer = transferService.getTransaction(transferResponse.getTransferId());
        transferService.executeTransfer(transferRepositoryMock.getTransactionById(transfer.getTransferId()));

        TransferStatusResponse updatedTransfer = transferService.getTransaction(transferResponse.getTransferId());
        assertEquals("Transfer status is incomplete", TransferStatus.Success, updatedTransfer.getStatus());
        Double expectedSenderBalance = 100.0 - transferService.currencyConverter.convertCurrency(Currency.USD, Currency.SGD, 10.0);
        Double expectedReceiverBalance = 20.0;
        assertEquals("Sender balance is incorrect", expectedSenderBalance, accountRepositoryMock.getAccountFromAccountNum("1").getBalance());
        assertEquals("Receiver balance is incorrect", expectedReceiverBalance, accountRepositoryMock.getAccountFromAccountNum("3").getBalance());
    }

    @Test
    public void executeTransferShouldThrowExceptionIfInsufficientBalance() {
        exception.expect(TransferException.class);
        exception.expectMessage(TransferError.SENDER_INSUFFICIENT_BALANCE.getDescription());
        TransferRequest transferRequest = new TransferRequest();
        transferRequest.setAmount(1000.0);
        transferRequest.setCurrency(Currency.SGD);
        transferRequest.setSender("1");
        transferRequest.setReceiver("2");
        TransferResponse transferResponse = transferService.createTransfer(transferRequest);
        TransferStatusResponse transfer = transferService.getTransaction(transferResponse.getTransferId());
        transferService.executeTransfer(transferRepositoryMock.getTransactionById(transfer.getTransferId()));

        TransferStatusResponse updatedTransfer = transferService.getTransaction(transferResponse.getTransferId());
        assertEquals("Transfer status is incomplete", TransferStatus.Failure, updatedTransfer.getStatus());
        Double expectedSenderBalance = 100.0;
        Double expectedReceiverBalance = 10.0;
        assertEquals("Sender balance is incorrect", expectedSenderBalance, accountRepositoryMock.getAccountFromAccountNum("1").getBalance());
        assertEquals("Receiver balance is incorrect", expectedReceiverBalance, accountRepositoryMock.getAccountFromAccountNum("2").getBalance());
    }

    @Test
    public void getTransferHistoryShouldThrowExceptionIfAccountNotPresent() {
        exception.expect(AccountsException.class);
        exception.expectMessage(AccountsError.ACCOUNT_NOT_EXIST.getDescription());

        transferService.getTransferHistory("100");
    }

    @Test
    public void getTransferHistoryShouldFetchTransfersForAccount() {
        TransferRequest transferRequest1 = new TransferRequest();
        transferRequest1.setAmount(10.0);
        transferRequest1.setCurrency(Currency.SGD);
        transferRequest1.setSender("1");
        transferRequest1.setReceiver("2");
        TransferResponse transferResponse1 = transferService.createTransfer(transferRequest1);

        TransferRequest transferRequest2 = new TransferRequest();
        transferRequest2.setAmount(1.0);
        transferRequest2.setCurrency(Currency.USD);
        transferRequest2.setSender("3");
        transferRequest2.setReceiver("1");
        TransferResponse transferResponse2 = transferService.createTransfer(transferRequest2);

        TransferHistoryResponse transferHistoryResponse1 = transferService.getTransferHistory("1");
        TransferHistoryResponse transferHistoryResponse2 = transferService.getTransferHistory("2");
        TransferHistoryResponse transferHistoryResponse3 = transferService.getTransferHistory("3");
        assertEquals("Number of transfers for account 1 don't match",2, transferHistoryResponse1.getTransfers().size());
        assertEquals("Number of transfers for account 2 don't match",1, transferHistoryResponse2.getTransfers().size());
        assertEquals("Number of transfers for account 3 don't match",1, transferHistoryResponse3.getTransfers().size());

    }

}
