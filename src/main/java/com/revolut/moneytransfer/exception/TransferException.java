package com.revolut.moneytransfer.exception;

public class TransferException extends RuntimeException {
    private TransferError error;

    public TransferException(TransferError error) {
        super(error.getDescription());
        this.error = error;
    }

    public TransferError getError() {
        return error;
    }
}
