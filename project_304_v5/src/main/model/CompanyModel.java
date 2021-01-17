package main.model;

public class CompanyModel {
    private final int companyID;
    private final String name;
    private final String address;

    public CompanyModel(int companyID, String name, String address) {
        this.companyID = companyID;
        this.name = name;
        this.address = address;
    }

    public int getCompanyID() {
        return companyID;
    }

    public String getAddress() {
        return address;
    }

    public String getName() {
        return name;
    }
}
