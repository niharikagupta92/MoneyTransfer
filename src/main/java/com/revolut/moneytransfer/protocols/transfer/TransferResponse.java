package com.revolut.moneytransfer.protocols.transfer;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class TransferResponse {

    String transferId;
    TransferStatus status;

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
        Gson gson = new GsonBuilder().setPrettyPrinting().create();;
        return gson.toJson(this);
    }
}
