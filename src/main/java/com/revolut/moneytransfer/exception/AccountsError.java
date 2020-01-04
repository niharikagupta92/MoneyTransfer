package com.revolut.moneytransfer.exception;

public enum AccountsError {
    ACCOUNTS_TABLE_NOT_EXIST(0, "Cannot create account table"),
    BALANCE_NEGATIVE(1, "Account Balance cannot be negative"),
    CURRENCY_NOT_SUPPORTED(2, "Currency not supported"),
    ACCOUNT_NOT_EXIST(3, "Account does not exist"),
    ACCOUNT_CREATION_FAILED(4, "Account creation failed");

    private final int code;
    private final String description;

    AccountsError(int code, String description) {
        this.code = code;
        this.description = description;
    }

    public int getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }
}
