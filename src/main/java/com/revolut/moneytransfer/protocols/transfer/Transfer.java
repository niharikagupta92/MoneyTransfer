package com.revolut.moneytransfer.protocols.transfer;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.revolut.moneytransfer.protocols.currency.Currency;

import java.time.ZonedDateTime;

public class Transfer {

    String senderAccount;
    String receiverAccount;
    Double amount;
    Currency currency;
    ZonedDateTime createTimestamp;
    String transferId;
    TransferStatus status;

    public String getSenderAccount() {
        return senderAccount;
    }

    public void setSenderAccount(String senderAccount) {
        this.senderAccount = senderAccount;
    }

    public String getReceiverAccount() {
        return receiverAccount;
    }

    public void setReceiverAccount(String receiverAccount) {
        this.receiverAccount = receiverAccount;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public Currency getCurrency() {
        return currency;
    }

    public void setCurrency(Currency currency) {
        this.currency = currency;
    }

    public void setCreateTimestamp(ZonedDateTime timestamp) {
        this.createTimestamp = timestamp;
    }

    public ZonedDateTime getCreateTimestamp() {
        return createTimestamp;
    }

    public String getTransferId() {
        return transferId;
    }

    public void setTransferId(String transferId) {
        this.transferId = transferId;
    }

    public TransferStatus getStatus() {
        return status;
    }

    public void setStatus(TransferStatus status) {
        this.status = status;
    }

    @Override
    public String toString() {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        return gson.toJson(this);
    }
}
