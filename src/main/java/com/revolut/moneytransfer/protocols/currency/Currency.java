package com.revolut.moneytransfer.protocols.currency;

public enum Currency {
    USD("USD"),
    SGD("SGD"),
    EUR("EUR"),
    GBP("GBP");

    Currency(String path) {
        this.path = path;
    }

    String path;

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
