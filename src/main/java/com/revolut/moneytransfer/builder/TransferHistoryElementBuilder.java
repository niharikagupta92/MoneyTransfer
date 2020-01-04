package com.revolut.moneytransfer.builder;

import com.revolut.moneytransfer.protocols.currency.Currency;
import com.revolut.moneytransfer.protocols.transfer.TransferHistoryElement;
import com.revolut.moneytransfer.protocols.transfer.TransferStatus;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class TransferHistoryElementBuilder {
    private TransferHistoryElement transferHistoryElement;

    public TransferHistoryElementBuilder() {
        this.transferHistoryElement = new TransferHistoryElement();
    }

    public TransferHistoryElementBuilder senderId(String senderId) {
        transferHistoryElement.setSenderId(senderId);
        return this;
    }

    public TransferHistoryElementBuilder receiverId(String receiverId) {
        transferHistoryElement.setReceiverId(receiverId);
        return this;
    }

    public TransferHistoryElementBuilder transferAmount(Double amount) {
        transferHistoryElement.setTransferAmount(amount);
        return this;
    }

    public TransferHistoryElementBuilder transferCurrency(Currency currency) {
        transferHistoryElement.setTransferCurrency(currency);
        return this;
    }

    public TransferHistoryElementBuilder dateTime(ZonedDateTime dateTime) {
        if(dateTime!= null)
            transferHistoryElement.setDateTime(dateTime.format(DateTimeFormatter.ISO_DATE_TIME));
        return this;
    }

    public TransferHistoryElementBuilder status(TransferStatus status) {
        transferHistoryElement.setStatus(status);
        return this;
    }

    public TransferHistoryElementBuilder transferId(String transferId) {
        transferHistoryElement.setTransferId(transferId);
        return this;
    }

    public TransferHistoryElement build() {
        return transferHistoryElement;
    }
}
