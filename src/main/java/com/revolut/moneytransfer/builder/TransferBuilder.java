package com.revolut.moneytransfer.builder;

import com.revolut.moneytransfer.protocols.currency.Currency;
import com.revolut.moneytransfer.protocols.transfer.Transfer;
import com.revolut.moneytransfer.protocols.transfer.TransferStatus;

public class TransferBuilder {
    private Transfer transfer;

    public TransferBuilder() {
        this.transfer = new Transfer();
    }

    public TransferBuilder senderAccount(String senderAccount) {
        this.transfer.setSenderAccount(senderAccount);
        return this;
    }

    public TransferBuilder receiverAccount(String receiverAccount) {
        transfer.setReceiverAccount(receiverAccount);
        return this;
    }

    public TransferBuilder amount(Double amount) {
        transfer.setAmount(amount);
        return this;
    }

    public TransferBuilder currency(Currency currency) {
        transfer.setCurrency(currency);
        return this;
    }

    public TransferBuilder status(TransferStatus status) {
        transfer.setStatus(status);
        return this;
    }

    public TransferBuilder transferId(String transferId) {
        transfer.setTransferId(transferId);
        return this;
    }

    public Transfer build() {
        return transfer;
    }
}
