package com.revolut.moneytransfer.builder;

import com.revolut.moneytransfer.protocols.transfer.TransferResponse;
import com.revolut.moneytransfer.protocols.transfer.TransferStatus;

public class TransferResponseBuilder {
    private TransferResponse transferResponse;

    public TransferResponseBuilder() {
        this.transferResponse = new TransferResponse();
    }

    public TransferResponseBuilder transferId(String transferId) {
        transferResponse.setTransferId(transferId);
        return this;
    }

    public TransferResponseBuilder status(TransferStatus status) {
        transferResponse.setStatus(status);
        return this;
    }

    public TransferResponse build() {
        return transferResponse;
    }
}
