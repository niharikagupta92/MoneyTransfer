package com.revolut.moneytransfer.exception;

public class AccountsException extends RuntimeException {
    private AccountsError error;

    public AccountsException(AccountsError error){
        super(error.getDescription());
        this.error = error;
    }

    public AccountsError getError() {
        return error;
    }

    public void setError(AccountsError error) {
        this.error = error;
    }
}
