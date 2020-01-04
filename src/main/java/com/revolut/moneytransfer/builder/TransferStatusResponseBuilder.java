package com.revolut.moneytransfer.builder;

import com.revolut.moneytransfer.protocols.currency.Currency;
import com.revolut.moneytransfer.protocols.transfer.Transfer;
import com.revolut.moneytransfer.protocols.transfer.TransferStatus;
import com.revolut.moneytransfer.protocols.transfer.TransferStatusResponse;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class TransferStatusResponseBuilder {

    private TransferStatusResponse transferStatusResponse;

    public TransferStatusResponseBuilder() {
        this.transferStatusResponse = new TransferStatusResponse();
    }

    public TransferStatusResponseBuilder senderAccount(String senderAccount) {
        this.transferStatusResponse.setSenderAccount(senderAccount);
        return this;
    }

    public TransferStatusResponseBuilder receiverAccount(String receiverAccount) {
        transferStatusResponse.setReceiverAccount(receiverAccount);
        return this;
    }

    public TransferStatusResponseBuilder amount(Double amount) {
        transferStatusResponse.setAmount(amount);
        return this;
    }

    public TransferStatusResponseBuilder currency(Currency currency) {
        transferStatusResponse.setCurrency(currency);
        return this;
    }

    public TransferStatusResponseBuilder status(TransferStatus status) {
        transferStatusResponse.setStatus(status);
        return this;
    }

    public TransferStatusResponseBuilder transferId(String transferId) {
        transferStatusResponse.setTransferId(transferId);
        return this;
    }

    public TransferStatusResponseBuilder createTimestamp(ZonedDateTime timestamp) {
        if(timestamp!=null)
            transferStatusResponse.setCreateTimestamp(timestamp.format(DateTimeFormatter.ISO_DATE_TIME));
        return this;
    }

    public TransferStatusResponse build() {
        return transferStatusResponse;
    }
}
