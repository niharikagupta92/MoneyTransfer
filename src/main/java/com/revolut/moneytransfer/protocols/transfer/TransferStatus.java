package com.revolut.moneytransfer.protocols.transfer;

public enum TransferStatus {
    Pending("Pending"),
    Success("Success"),
    Failure("Failure");

    TransferStatus(String path) { this.path = path; }

    String path;

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
