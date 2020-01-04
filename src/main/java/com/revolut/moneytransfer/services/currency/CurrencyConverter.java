package com.revolut.moneytransfer.services.currency;

import com.revolut.moneytransfer.exception.TransferError;
import com.revolut.moneytransfer.exception.TransferException;
import com.revolut.moneytransfer.protocols.currency.Currency;
import com.revolut.moneytransfer.protocols.currency.CurrencyPair;

import java.util.HashMap;


public class CurrencyConverter {
    private static final HashMap<CurrencyPair, Double> EXCHANGE_RATE = new HashMap<CurrencyPair, Double>() {{
        put(new CurrencyPair(Currency.SGD, Currency.USD), 0.74);
        put(new CurrencyPair(Currency.SGD, Currency.EUR), 0.67);
        put(new CurrencyPair(Currency.SGD, Currency.GBP), 0.57);
        put(new CurrencyPair(Currency.EUR, Currency.SGD), 1.5);
        put(new CurrencyPair(Currency.EUR, Currency.GBP), 0.85);
        put(new CurrencyPair(Currency.EUR, Currency.USD), 1.11);
        put(new CurrencyPair(Currency.GBP, Currency.USD), 1.30);
        put(new CurrencyPair(Currency.GBP, Currency.SGD), 1.76);
        put(new CurrencyPair(Currency.GBP, Currency.EUR), 1.17);
        put(new CurrencyPair(Currency.USD, Currency.SGD), 1.35);
        put(new CurrencyPair(Currency.USD, Currency.EUR), 0.89);
        put(new CurrencyPair(Currency.USD, Currency.GBP), 0.76);
    }};

    public Double convertCurrency(Currency srcCurrency, Currency destCurrency, Double amount) {
        CurrencyPair cPair = new CurrencyPair(srcCurrency, destCurrency);
        if(srcCurrency == destCurrency)
            return amount;

        if (!EXCHANGE_RATE.containsKey(cPair))
            throw new TransferException(TransferError.INVALID_CURRENCY_CONVERSION);
        return amount * EXCHANGE_RATE.get(cPair);
    }
}
