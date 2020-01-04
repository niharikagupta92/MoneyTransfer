package com.revolut.moneytransfer.exception;

public enum TransferError {
    INVLAID_TRANSFER_AMOUNT(0, "Invalid Transfer Amount"),
    INVALID_CURRENCY(1, "Currency Not Supported"),
    SENDER_INVALID(2, "Sender Account Does Not Exist"),
    RECEIVER_INVALID(3, "Receiver Account Does not Exist"),
    SENDER_INSUFFICIENT_BALANCE(4, "Sender Insufficient Balance"),
    INVALID_CURRENCY_CONVERSION(5, "Currency Conversion Not Supported"),
    CURRENCY_NEITHER_SENDER_NOR_RECEIVER(6, "Currency doesn't match receiver or sender currency"),
    TRANSFER_CREATION_FAILED(7, "Transfer request cannot be saved"),
    TRANSFER_NOT_EXIST(8, "Transfer does not exist"),
    SENDER_CANNOT_BE_RECEIVER(9, "Sender cannot be same as receiver");

    private final int code;
    private final String description;

    TransferError(int code, String description){
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
