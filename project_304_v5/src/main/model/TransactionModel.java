package main.model;

public class TransactionModel {
    private final int transactionID;
    private final int quantity;
    private final double transactionPrice;
    private final double stockPrice;
    private final String date;
    private final String currency;
    private final int companyID;
    private final String exchangeName;
    private final String type;
    private final int investorID;

    public TransactionModel(int transactionID, int quantity, double transactionPrice, double stockPrice,
                            String date, String currency, int companyID, String exchangeName, String type,
                            int investorID) {
        this.transactionID = transactionID;
        this.quantity = quantity;
        this.transactionPrice = transactionPrice;
        this.stockPrice = stockPrice;
        this.date = date;
        this.currency = currency;
        this.companyID = companyID;
        this.exchangeName = exchangeName;
        this.type = type;
        this.investorID = investorID;
    }

    public int getTransactionID() {
        return transactionID;
    }

    public int getQuantity() {
        return quantity;
    }

    public double getTransactionPrice() {
        return transactionPrice;
    }

    public double getStockPrice() {
        return stockPrice;
    }

    public String getDate() {
        return date;
    }

    public String getCurrency() {
        return currency;
    }

    public int getCompanyID() {
        return companyID;
    }

    public String getExchangeName() {
        return exchangeName;
    }

    public String getType() {
        return type;
    }

    public int getInvestorID() {
        return investorID;
    }
}
