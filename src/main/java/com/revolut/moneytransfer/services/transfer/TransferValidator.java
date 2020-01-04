package com.revolut.moneytransfer.services.transfer;

import com.revolut.moneytransfer.exception.TransferError;
import com.revolut.moneytransfer.exception.TransferException;
import com.revolut.moneytransfer.protocols.account.Account;
import com.revolut.moneytransfer.protocols.transfer.TransferRequest;
import com.revolut.moneytransfer.repo.AccountRepository;
import com.revolut.moneytransfer.services.currency.CurrencyConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class TransferValidator {

    private final Logger logger = LoggerFactory.getLogger(TransferValidator.class);
    private AccountRepository accountRepository;
    private CurrencyConverter currencyConverter;

    TransferValidator(AccountRepository accountRepository, CurrencyConverter currencyConverter) {
        this.accountRepository = accountRepository;
        this.currencyConverter = currencyConverter;
    }

    void validateTransferRequest(TransferRequest transferRequest) {
        if (transferRequest.getAmount() == null || transferRequest.getAmount() <= 0) {
            throw new TransferException(TransferError.INVLAID_TRANSFER_AMOUNT);
        }
        if (transferRequest.getSender() == null) {
            throw new TransferException(TransferError.SENDER_INVALID);
        }
        if (transferRequest.getReceiver() == null) {
            throw new TransferException(TransferError.RECEIVER_INVALID);
        }
        if (transferRequest.getCurrency() == null) {
            throw new TransferException(TransferError.INVALID_CURRENCY);
        }

        if (transferRequest.getSender().equals(transferRequest.getReceiver())) {
            throw new TransferException(TransferError.SENDER_CANNOT_BE_RECEIVER);
        }

        Account sender = accountRepository.getAccountFromAccountNum(transferRequest.getSender());
        if (sender == null)
            throw new TransferException(TransferError.SENDER_INVALID);

        logger.info("Sender account: " + sender.toString());
        Account receiver = accountRepository.getAccountFromAccountNum(transferRequest.getReceiver());
        if (receiver == null)
            throw new TransferException(TransferError.RECEIVER_INVALID);

        logger.info("Receiver account: " + receiver.toString());

        if (transferRequest.getCurrency() != sender.getCurrency() && transferRequest.getCurrency() != receiver.getCurrency())
            throw new TransferException(TransferError.CURRENCY_NEITHER_SENDER_NOR_RECEIVER);

        Double amountSenderCurrency = currencyConverter.convertCurrency(transferRequest.getCurrency(),
                sender.getCurrency(),
                transferRequest.getAmount());
        if (amountSenderCurrency > sender.getBalance()) {
            throw new TransferException(TransferError.SENDER_INSUFFICIENT_BALANCE);
        }
    }
}
