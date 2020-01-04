package com.revolut.moneytransfer.protocols.currency;

public class CurrencyPair {
    Currency srcCurrency;
    Currency destCurrency;

    public CurrencyPair(Currency src, Currency dest) {
        this.srcCurrency = src;
        this.destCurrency = dest;
    }

    @Override
    public int hashCode() {
        return (this.srcCurrency.getPath() + this.destCurrency.getPath()).hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if(obj==null)
            return false;
        if(!(obj instanceof CurrencyPair))
            return false;
        CurrencyPair that = (CurrencyPair)obj;
        return this.srcCurrency==that.srcCurrency && this.destCurrency==that.destCurrency;
    }
}