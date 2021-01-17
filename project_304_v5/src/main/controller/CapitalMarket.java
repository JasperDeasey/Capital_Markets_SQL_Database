package main.controller;

import main.database.DatabaseConnectionHandler;
import main.ui.Application;

// this is the main controller class that will orchestrate everything.
public class CapitalMarket {
    private DatabaseConnectionHandler dbHandler = null;

    public CapitalMarket() {
        dbHandler = new DatabaseConnectionHandler();
    }

    public void login() {
        //!*!
        boolean didConnect = dbHandler.login("ora_kshaw031", "a25032863");

        if (didConnect) {
            // Once connected, start the application
            dbHandler.databaseSetup();
            new Application(dbHandler);
        } else {
            System.out.println("Error logging in. Please try again.");
            System.exit(-1);
        }
    }

    public static void main(String[] args) {
        CapitalMarket capitalMarket = new CapitalMarket();
        capitalMarket.login();
    }
}
