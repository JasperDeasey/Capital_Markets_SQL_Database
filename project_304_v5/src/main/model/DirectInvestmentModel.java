package main.model;

public class DirectInvestmentModel {
    private final int investorID;
    private final int companyID;
    private final int equityPercent;
    private final int investmentValue;

    public DirectInvestmentModel(int investorID, int companyID, int equityPercent, int investmentValue) {
        this.investorID = investorID;
        this.companyID = companyID;
        this.equityPercent = equityPercent;
        this.investmentValue = investmentValue;
    }

    public int getCompanyID() {
        return companyID;
    }

    public int getEquityPercent() {
        return equityPercent;
    }

    public double getInvestmentValue() {
        return investmentValue;
    }

    public int getInvestorID() {
        return investorID;
    }
}
