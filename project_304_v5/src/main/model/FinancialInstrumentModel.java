package main.model;

public class FinancialInstrumentModel {
    private final int companyID;
    private final String type;


    public FinancialInstrumentModel(int companyID, String type) {
        this.companyID = companyID;
        this.type = type;
    }

    public int getCompanyID() {
        return companyID;
    }

    public String getType() {
        return type;
    }
}
