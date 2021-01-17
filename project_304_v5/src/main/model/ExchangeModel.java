package main.model;

public class ExchangeModel {
    private final String name;

    public ExchangeModel(int fid, String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
