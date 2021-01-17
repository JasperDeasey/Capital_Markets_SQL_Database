package main.model;

public class CurrencyModel {
    private final String currencyName;

    public CurrencyModel(String currencyName) {
        this.currencyName = currencyName;
    }

    public String getCurrencyName() {
        return currencyName;
    }
}
