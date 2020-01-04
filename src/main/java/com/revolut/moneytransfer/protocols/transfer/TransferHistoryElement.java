package com.revolut.moneytransfer.protocols.transfer;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.revolut.moneytransfer.protocols.currency.Currency;

public class TransferHistoryElement {
    String senderId;
    String receiverId;
    Double transferAmount;
    Currency transferCurrency;
    String dateTime;
    TransferStatus status;
    String transferId;

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public String getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(String receiverId) {
        this.receiverId = receiverId;
    }

    public Double getTransferAmount() {
        return transferAmount;
    }

    public void setTransferAmount(Double transferAmount) {
        this.transferAmount = transferAmount;
    }

    public Currency getTransferCurrency() {
        return transferCurrency;
    }

    public void setTransferCurrency(Currency transferCurrency) {
        this.transferCurrency = transferCurrency;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    public TransferStatus getStatus() {
        return status;
    }

    public void setStatus(TransferStatus status) {
        this.status = status;
    }

    public String getTransferId() {
        return transferId;
    }

    public void setTransferId(String transferId) {
        this.transferId = transferId;
    }

    @Override
    public String toString() {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        return gson.toJson(this);
    }
}
