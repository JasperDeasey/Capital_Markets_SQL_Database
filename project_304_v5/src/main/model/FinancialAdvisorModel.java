package main.model;

public class FinancialAdvisorModel {
    private final int fid;
    private final String name;

    public FinancialAdvisorModel(int fid, String name) {
        this.fid = fid;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public int getFid() {
        return fid;
    }
}
