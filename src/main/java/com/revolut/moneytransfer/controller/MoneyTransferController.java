package com.revolut.moneytransfer.controller;

import com.google.gson.Gson;
import com.revolut.moneytransfer.exception.AccountsException;
import com.revolut.moneytransfer.exception.TransferError;
import com.revolut.moneytransfer.exception.TransferException;
import com.revolut.moneytransfer.protocols.account.AccountCreateRequest;
import com.revolut.moneytransfer.protocols.account.AccountCreateResponse;
import com.revolut.moneytransfer.protocols.transfer.TransferRequest;
import com.revolut.moneytransfer.protocols.transfer.TransferResponse;
import com.revolut.moneytransfer.protocols.transfer.TransferStatusResponse;
import com.revolut.moneytransfer.services.account.AccountService;
import com.revolut.moneytransfer.services.transfer.TransferService;

import static spark.Spark.*;

public class MoneyTransferController {

    private AccountService accountService;
    private TransferService transferService;

    MoneyTransferController() {

        this.accountService = new AccountService();
        this.transferService = new TransferService();
    }

    private void AccountRestAPIs() {
        post("/createAccount", (request, response) -> {
            Gson gson = new Gson();
            AccountCreateRequest accountCreateRequest = gson.fromJson(request.body(), AccountCreateRequest.class);
            AccountCreateResponse account = accountService.createAccount(accountCreateRequest);
            response.status(201);
            System.out.println(account.getAccountId());
            return account;
        });

        get("/balance/:accountId", ((request, response) -> {
            String accountId = request.params(":accountId");
            response.status(201);

            return accountService.getAccountBalance(accountId);
        }));

        get("/transferHistory/:accountId", ((request, response) -> {
            String accountId = request.params(":accountId");
            response.status(201);

            return transferService.getTransferHistory(accountId);
        }));
    }

    private void TransferRestAPIs() {
        post("/transfer", (request, response) -> {
            Gson gson = new Gson();
            TransferRequest transferRequest = gson.fromJson(request.body(), TransferRequest.class);
            TransferResponse transferResponse = transferService.createTransfer(transferRequest);
            response.status(201);
            System.out.println(transferResponse.getTransferId());
            return transferResponse;
        });

        get("/transferStatus/:transferId", ((request, response) -> {
            String transferId = request.params(":transferId");
            TransferStatusResponse transfer = transferService.getTransaction(transferId);

            response.status(201);
            return transfer;
        }));
    }

    private void ErrorApis() {
        exception(AccountsException.class, (exception, request, response) -> {
            response.status(exception.getError().getCode());
            response.body(exception.getMessage());
        });

        exception(TransferException.class, (exception, request, response) -> {
            response.status(exception.getError().getCode());
            response.body(exception.getMessage());
        });
    }

    public static void main(String[] args) {
        MoneyTransferController moneyTransferController = new MoneyTransferController();

        moneyTransferController.AccountRestAPIs();
        moneyTransferController.TransferRestAPIs();
        moneyTransferController.ErrorApis();
    }
}
