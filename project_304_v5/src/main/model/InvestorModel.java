package main.model;

public class InvestorModel {
    private final int investorID;
    private final String name;
    private int fid;

    public InvestorModel(int investorID, String name) {
        this.investorID = investorID;
        this.name = name;
    }

    public int getInvestorID() {
        return investorID;
    }

    public String getName() {
        return name;
    }

    public int getFid() {
        return fid;
    }
}
