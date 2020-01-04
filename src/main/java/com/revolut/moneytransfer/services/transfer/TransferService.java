package com.revolut.moneytransfer.services.transfer;

import com.revolut.moneytransfer.builder.TransferBuilder;
import com.revolut.moneytransfer.builder.TransferHistoryElementBuilder;
import com.revolut.moneytransfer.builder.TransferResponseBuilder;
import com.revolut.moneytransfer.builder.TransferStatusResponseBuilder;
import com.revolut.moneytransfer.constants.DBConstants;
import com.revolut.moneytransfer.exception.AccountsError;
import com.revolut.moneytransfer.exception.AccountsException;
import com.revolut.moneytransfer.exception.TransferError;
import com.revolut.moneytransfer.exception.TransferException;
import com.revolut.moneytransfer.protocols.account.Account;
import com.revolut.moneytransfer.protocols.transfer.*;
import com.revolut.moneytransfer.repo.AccountRepository;
import com.revolut.moneytransfer.repo.TransferRepository;
import com.revolut.moneytransfer.services.currency.CurrencyConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class TransferService {

    TransferRepository transferRepository;
    AccountRepository accountRepository;
    private ScheduledExecutorService executorService;
    CurrencyConverter currencyConverter;
    TransferValidator transferValidator;
    private final Logger logger = LoggerFactory.getLogger(TransferService.class);

    public TransferService() {

        this.transferRepository = new TransferRepository();
        this.accountRepository = new AccountRepository();
        this.currencyConverter = new CurrencyConverter();
        this.transferValidator = new TransferValidator(accountRepository, currencyConverter);
        this.executorService = Executors.newScheduledThreadPool(1);
        schedule();
    }

    public TransferResponse createTransfer(TransferRequest transferRequest) {

        logger.info("Creating Transfer request: " + transferRequest.toString());
        transferValidator.validateTransferRequest(transferRequest);

        Transfer transfer = new TransferBuilder()
                .senderAccount(transferRequest.getSender())
                .receiverAccount(transferRequest.getReceiver())
                .amount(transferRequest.getAmount())
                .currency(transferRequest.getCurrency())
                .transferId(UUID.randomUUID().toString())
                .status(TransferStatus.Pending)
                .build();

        logger.info("Saving transfer: " + transfer.toString());

        Transfer savedTransfer = transferRepository.saveTransaction(transfer);

        return new TransferResponseBuilder()
                .transferId(savedTransfer.getTransferId())
                .status(savedTransfer.getStatus())
                .build();
    }

    public TransferStatusResponse getTransaction(String transferId) {
        logger.info("Getting transaction info for " + transferId);
        Transfer transfer = transferRepository.getTransactionById(transferId);
        if (transfer == null)
            throw new TransferException(TransferError.TRANSFER_NOT_EXIST);
        logger.info("Fetched transfer: " + transfer.toString());
        return new TransferStatusResponseBuilder()
                .transferId(transfer.getTransferId())
                .amount(transfer.getAmount())
                .currency(transfer.getCurrency())
                .senderAccount(transfer.getSenderAccount())
                .receiverAccount(transfer.getReceiverAccount())
                .status(transfer.getStatus())
                .createTimestamp(transfer.getCreateTimestamp())
                .build();
    }

    private void schedule() {

        logger.info("Creating scheduler for executing transfers");
        Runnable runnableTask = () -> {
            try {
                executeTransferRequests();
            } catch (Exception e) {
                e.printStackTrace();
            }
        };

        executorService.scheduleAtFixedRate(runnableTask, 0, 10, TimeUnit.SECONDS);
    }

    public TransferHistoryResponse getTransferHistory(String accountId) {
        logger.info("Fetching transfer history for accountId: " + accountId);
        Account acc = accountRepository.getAccountFromAccountNum(accountId);
        if (acc == null)
            throw new AccountsException(AccountsError.ACCOUNT_NOT_EXIST);

        List<Transfer> transferHistory = transferRepository.getTransferHistoryByAccountId(accountId);
        List<TransferHistoryElement> transfers = new ArrayList<>();
        transferHistory.forEach(transfer -> {
            TransferHistoryElement transferHistoryElement = new TransferHistoryElementBuilder()
                    .transferAmount(transfer.getAmount())
                    .receiverId(transfer.getReceiverAccount())
                    .senderId(transfer.getSenderAccount())
                    .transferCurrency(transfer.getCurrency())
                    .dateTime(transfer.getCreateTimestamp())
                    .status(transfer.getStatus())
                    .transferId(transfer.getTransferId())
                    .build();
            transfers.add(transferHistoryElement);
        });
        TransferHistoryResponse transferHistoryOuput = new TransferHistoryResponse();
        transferHistoryOuput.setTransfers(transfers);
        return transferHistoryOuput;
    }

    private void executeTransferRequests() {
        List<Transfer> transferList = transferRepository.getTransactionByStatus(TransferStatus.Pending, DBConstants.REQUEST_THROTTLE);

        transferList.forEach(transfer -> executeTransfer(transfer));
    }

    void executeTransfer(Transfer transfer) {

        if(transfer.getStatus()==TransferStatus.Failure || transfer.getStatus()==TransferStatus.Success)
            return;

        logger.info("Executing transfer request: " + transfer.toString());
        Double amount = transfer.getAmount();
        String senderAccount = transfer.getSenderAccount();
        String receiverAccount = transfer.getReceiverAccount();

        Account sender = accountRepository.getAccountFromAccountNum(senderAccount);
        if (sender == null) {
            transferRepository.updateTransferStatus(transfer.getTransferId(), TransferStatus.Failure);
            throw new TransferException(TransferError.SENDER_INVALID);
        }

        logger.info("Sender account: " + sender.toString());
        Account receiver = accountRepository.getAccountFromAccountNum(receiverAccount);
        if (receiver == null) {
            transferRepository.updateTransferStatus(transfer.getTransferId(), TransferStatus.Failure);
            throw new TransferException(TransferError.RECEIVER_INVALID);
        }

        logger.info("Receiver account: " + receiver.toString());

        Double senderCurrentBalance = sender.getBalance();
        Double receiverCurrentBalance = receiver.getBalance();

        Double amountSenderCurrency = currencyConverter.convertCurrency(transfer.getCurrency(), sender.getCurrency(), amount);
        Double amountReceiverCurrency = currencyConverter.convertCurrency(transfer.getCurrency(), receiver.getCurrency(), amount);

        if (amountSenderCurrency > senderCurrentBalance) {
            transferRepository.updateTransferStatus(transfer.getTransferId(), TransferStatus.Failure);
            throw new TransferException(TransferError.SENDER_INSUFFICIENT_BALANCE);
        }

        Double senderUpdatedBalance = senderCurrentBalance - amountSenderCurrency;
        Double receiverUpdatedBalance = receiverCurrentBalance + amountReceiverCurrency;

        logger.info("Updating balance of sender account " + senderAccount + " to balance " + senderUpdatedBalance);
        accountRepository.updateAccountBalance(senderAccount, senderUpdatedBalance);
        logger.info("Updating balance of receiver account " + receiverAccount + " to balance " + receiverUpdatedBalance);
        accountRepository.updateAccountBalance(receiverAccount, receiverUpdatedBalance);

        logger.info("Transaction " + transfer.getTransferId() + " Successful");
        transferRepository.updateTransferStatus(transfer.getTransferId(), TransferStatus.Success);
    }
}