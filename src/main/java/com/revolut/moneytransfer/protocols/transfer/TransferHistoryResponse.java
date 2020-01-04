package com.revolut.moneytransfer.protocols.transfer;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.List;

public class TransferHistoryResponse {
    List<TransferHistoryElement> transfers;

    public List<TransferHistoryElement> getTransfers() {
        return transfers;
    }

    public void setTransfers(List<TransferHistoryElement> transfers) {
        this.transfers = transfers;
    }

    @Override
    public String toString() {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        return gson.toJson(this);
    }
}