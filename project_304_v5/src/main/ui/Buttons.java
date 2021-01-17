package main.ui;
import main.database.DatabaseConnectionHandler;
import main.exception.*;

import javax.swing.*;
import java.awt.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;

public class Buttons extends JPanel {
    private Application app;
    private DatabaseConnectionHandler dbHandler;

    public Buttons(Application app, DatabaseConnectionHandler a) {
        this.app = app;
        dbHandler = a;
        setLayout(new GridLayout(0, 1));
        addButtons();

    }

    // EFFECTS: adds the buttons
    private void addButtons() {
        transactionButton();
        addInvestorButton();
        removeInvestorButton();
        addCompanyButton();
        deleteCompanyButton();
        updateEquityButton();
    }

    private void updateEquityButton() {
        JButton updateEquityButton = new JButton("Change primary investor's equity");
        updateEquityButton.addActionListener(e -> {
            updateEquity();
            refresh();
        });
        updateEquityButton.setPreferredSize(new Dimension(700, 40));
        add(updateEquityButton);
    }

    private void updateEquity() {
        JTextField investorIdField = new JTextField();
        JTextField equityField = new JTextField();
        JTextField valueField = new JTextField();
        JTextField companyIDField = new JTextField();
        Object[] fields = {"Investor ID", investorIdField, "new equity % ", equityField, "total investment", valueField, "Comapany ID", companyIDField};
        int pane = JOptionPane.OK_CANCEL_OPTION;
        int info = JOptionPane.INFORMATION_MESSAGE;
        int option = JOptionPane.showConfirmDialog(null, fields, "update equity", pane, info);

        if(option == JOptionPane.OK_OPTION) {
            try {
                int investorId = Integer.parseInt(investorIdField.getText());
                double equity = Double.parseDouble(equityField.getText());
                double value = Double.parseDouble(valueField.getText());
                int cid = Integer.parseInt(companyIDField.getText());
                if (equity > 100) {
                    throw new EquityOutOfBoundsException();
                }
                if (!dbHandler.investorExists(investorId)) {
                    throw new InvestorDoesNotExistException();
                }
                if (!dbHandler.companyExists(cid)) {
                    throw new CompanyDoesNotExistException();
                }
                if(!dbHandler.investorHasMadeDirectInvestmentInCompany(investorId, cid)) {
                    throw new NoPreviousInvestmentException();
                }
                dbHandler.updatePrimaryInvestorEquity(investorId, equity, value, cid);

            } catch(NumberFormatException e) {
                JOptionPane.showMessageDialog(null, "Invalid input.", "Invalid", JOptionPane.ERROR_MESSAGE);
            } catch (InvestorDoesNotExistException e) {
                JOptionPane.showMessageDialog(null, "Investor Does not Exist", "Invalid", JOptionPane.ERROR_MESSAGE);
            } catch (CompanyDoesNotExistException e) {
                JOptionPane.showMessageDialog(null, "Company Does not Exist", "Invalid", JOptionPane.ERROR_MESSAGE);
            } catch (EquityOutOfBoundsException e) {
                JOptionPane.showMessageDialog(null, "Equity must be between 0-100", "Invalid", JOptionPane.ERROR_MESSAGE);
            } catch (NoPreviousInvestmentException e) {
                JOptionPane.showMessageDialog(null, "Investor has not previously invested in that company", "Invalid", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void deleteCompanyButton() {
        JButton deleteCompanyButton = new JButton("Delete company");
        deleteCompanyButton.addActionListener(e -> {
            deleteCompany();
            refresh();
        });
        add(deleteCompanyButton);
    }

    //WORKS!
    private void deleteCompany() {
        JTextField companyIdField = new JTextField();
        Object[] fields = {"Company ID", companyIdField};
        int pane = JOptionPane.OK_CANCEL_OPTION;
        int info = JOptionPane.INFORMATION_MESSAGE;
        int option = JOptionPane.showConfirmDialog(null, fields, "Delete company", pane, info);

        if(option == JOptionPane.OK_OPTION) {
            try {
                int companyId = Integer.parseInt(companyIdField.getText());
                if (!dbHandler.companyExists(companyId)) {
                    throw new CompanyDoesNotExistException();
                }
                dbHandler.deleteCompany(companyId);
            } catch(NumberFormatException e) {
                JOptionPane.showMessageDialog(null, "Invalid input.", "Invalid", JOptionPane.ERROR_MESSAGE);
            } catch (CompanyDoesNotExistException e) {
                JOptionPane.showMessageDialog(null, "Company does not exist", "Invalid", JOptionPane.ERROR_MESSAGE);

            }
        }
    }

    private void addCompanyButton() {
        JButton addCompanyButton = new JButton("Add company");
        addCompanyButton.addActionListener(e -> {
            addCompany();
            refresh();
        });
        add(addCompanyButton);
    }

    private void addCompany() {
        JTextField nameField = new JTextField();
        JTextField addressField = new JTextField();
        Object[] fields = {"Name (enter NULL if empty)", nameField, "Address (enter NULL if empty)", addressField};

        int pane = JOptionPane.OK_CANCEL_OPTION;
        int info = JOptionPane.INFORMATION_MESSAGE;
        int option = JOptionPane.showConfirmDialog(null, fields, "add company", pane, info);

        if(option == JOptionPane.OK_OPTION) {
            try {
                String name = nameField.getText();
                String address = addressField.getText();
                if (name.equals("") || address.equals("")) {
                    throw new EmptyInputException();
                }

                int companyID = dbHandler.createCompanyCID();

                dbHandler.insertCompany(companyID, name, address);
                JOptionPane.showMessageDialog(null, "Your company is " + companyID);
            } catch (EmptyInputException e) {
                JOptionPane.showMessageDialog(null, "ensure all inputs are filled in", "Invalid", JOptionPane.ERROR_MESSAGE);
                addCompany();
            }
        }
    }

    private void removeInvestorButton() {
        JButton removeInvestorButton = new JButton("Delete account");
        removeInvestorButton.addActionListener(e -> {
            removeInvestor();
            refresh();
        });
        add(removeInvestorButton);
    }

    private void removeInvestor() {
        JTextField investorIdField = new JTextField();
        Object[] fields = {"Investor ID", investorIdField};
        int pane = JOptionPane.OK_CANCEL_OPTION;
        int info = JOptionPane.INFORMATION_MESSAGE;
        int option = JOptionPane.showConfirmDialog(null, fields, "Remove Investor", pane, info);

        if(option == JOptionPane.OK_OPTION) {
            try {
                int investorId = Integer.parseInt(investorIdField.getText());
                if (!dbHandler.investorExists(investorId)) {
                    throw new InvestorDoesNotExistException();
                }
                dbHandler.deleteInvestor(investorId);
            } catch(NumberFormatException e) {
                JOptionPane.showMessageDialog(null, "Invalid input.", "Invalid", JOptionPane.ERROR_MESSAGE);
            } catch (InvestorDoesNotExistException e) {
                JOptionPane.showMessageDialog(null, "Investor does not exist", "Invalid", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void addInvestorButton() {
        JButton addInvestorButton = new JButton("Create account");
        addInvestorButton.addActionListener(e -> {
            addInvestor();
            refresh();
        });
        add(addInvestorButton);
    }

    private void addInvestor() {
        JTextField nameField = new JTextField();
        JTextField fidField = new JTextField();
        JTextField educationField = new JTextField();
        Object[] fields = {"Name", nameField, "Financial Advisor ID (enter '0' if none)", fidField, "Education (enter 'NULL' if none)", educationField};

        int pane = JOptionPane.OK_CANCEL_OPTION;
        int info = JOptionPane.INFORMATION_MESSAGE;
        int option = JOptionPane.showConfirmDialog(null, fields, "Create account", pane, info);

        if(option == JOptionPane.OK_OPTION) {
            try {
                String name = nameField.getText();
                int fid = Integer.parseInt(fidField.getText());
                String education = educationField.getText();
                if(fid != 0 && !dbHandler.fidExists(fid)) {
                    throw new FIDDoesNotExistException();
                }

                int id = dbHandler.createInvestorID();

                if(name.equals("") || education.equals("")) {
                    throw new EmptyInputException();
                }

                dbHandler.insertInvestor(id, name, fid, education);

                JOptionPane.showMessageDialog(null, "Your new investor ID is " + id);
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(null, "Investor ID must be an integer", "Invalid", JOptionPane.ERROR_MESSAGE);
                addInvestor();
            } catch (EmptyInputException e) {
                JOptionPane.showMessageDialog(null, "ensure all inputs are filled in", "Invalid", JOptionPane.ERROR_MESSAGE);
                addInvestor();
            } catch (FIDDoesNotExistException e) {
                JOptionPane.showMessageDialog(null, "that FID does not exists", "Invalid", JOptionPane.ERROR_MESSAGE);
                addInvestor();
            }
        }
    }

    private void transactionButton() {
        JButton transactionButton = new JButton("Make transaction");
        transactionButton.addActionListener(e -> {
            makeTransaction();
            refresh();
        });
        add(transactionButton);
    }

    private void refresh() {
        app.dispose();
        new Application(dbHandler);
    }

    private void makeTransaction() {
        int pane = JOptionPane.YES_NO_CANCEL_OPTION;
        int option = JOptionPane.showConfirmDialog(null, "Are you registered?", "Are you registered?", pane);
        if(option == JOptionPane.YES_OPTION || option == JOptionPane.NO_OPTION) {
            try {
                if (option == JOptionPane.NO_OPTION) {
                    addInvestor();
                }
                Object[] possibleValues = {"Direct investment", "stocks"};
                Object option2 = JOptionPane.showInputDialog(null,
                        "Choose type", "Input",
                        JOptionPane.INFORMATION_MESSAGE, null,
                        possibleValues, possibleValues[0]);
                if (option2.equals("Direct investment")) {
                    directInvestment();
                } else {
                    stocks();
                }
            } catch (NullPointerException e) {
                // not sure why this was throwing. This seems to fix it though.
                makeTransaction();
            }
        }
    }

    private void stocks() {
        JTextField iidField= new JTextField();
        JTextField quantityField = new JTextField();
        JTextField priceOfStockField = new JTextField();
        JTextField typeField = new JTextField();
        JTextField cidField = new JTextField();
        JTextField dateField = new JTextField();
        JTextField currencyField = new JTextField();
        JTextField exchangeField = new JTextField();
        Object[] fields = {"Investor ID", iidField, "Quantity (make negative if selling) ", quantityField, "Price of Stock", priceOfStockField,
                "Type (stock or bond)", typeField, "Company ID", cidField, "date (MM/DD/YYYY)", dateField,
                "Currency", currencyField, "Exchange name", exchangeField};

        int pane = JOptionPane.OK_CANCEL_OPTION;
        int info = JOptionPane.INFORMATION_MESSAGE;
        int option = JOptionPane.showConfirmDialog(null, fields, "stock Investment", pane, info);
        if (option == JOptionPane.OK_OPTION) {
            try {
                int investor_id = Integer.parseInt(iidField.getText());
                int quantity = Integer.parseInt(quantityField.getText());

                double priceOfStock = Double.parseDouble(priceOfStockField.getText());
                if (priceOfStock < 0) {
                    throw new NegativeNumberException();
                }

                String type = typeField.getText();
                if (!type.equals("stock") && !type.equals("bond")) {
                    throw new InvalidTypeException();
                }

                int cid = Integer.parseInt(cidField.getText());
                if (!dbHandler.cidExists(cid)) {
                    throw new CIDDoesNotExistException();
                }

                int transactionID = dbHandler.createTransactionID();

                String date = dateField.getText();
                if (!isDateValid(date)) {
                    throw new InvalidDateException();
                }

                String currency = currencyField.getText();
                String exchange = exchangeField.getText();

                //make investment
                if (currency.trim() == "" || exchange.trim() == "") {
                    throw new EmptyInputException();
                }

                dbHandler.insertTransaction(transactionID, cid, type,exchange,currency,investor_id,date,priceOfStock,quantity);

            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(null, "Invalid input.", "Invalid", JOptionPane.ERROR_MESSAGE);
                stocks();
            } catch (NegativeNumberException e) {
                JOptionPane.showMessageDialog(null, "Stock price must not be negative!", "Invalid", JOptionPane.ERROR_MESSAGE);
                stocks();
            } catch (InvalidTypeException e) {
                JOptionPane.showMessageDialog(null, "type must be either \"stock\" or \"bond\"", "Invalid", JOptionPane.ERROR_MESSAGE);
                stocks();
            } catch (InvalidDateException e) {
                JOptionPane.showMessageDialog(null, "Date must be in the format MM/DD/YYYY", "Invalid", JOptionPane.ERROR_MESSAGE);
                stocks();
            } catch (EmptyInputException e) {
                JOptionPane.showMessageDialog(null, "ensure all inputs have values", "Invalid", JOptionPane.ERROR_MESSAGE);
                stocks();
            } catch (CIDDoesNotExistException e) {
                JOptionPane.showMessageDialog(null, "that company ID does not exist", "Invalid", JOptionPane.ERROR_MESSAGE);
                stocks();
            }
        }
    }

    private boolean isDateValid(String date) {
        SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy");
        try {
            format.parse(date);
            return true;
        } catch (ParseException e) {
            return false;
        }

    }

    private void directInvestment() {
        JTextField iidField= new JTextField();
        JTextField equityField = new JTextField();
        JTextField amountField = new JTextField();
        JTextField cidField = new JTextField();
        Object[] fields = {"Investor ID", iidField, "Equity %", equityField, "Amount", amountField, "Company ID", cidField};
        int pane = JOptionPane.OK_CANCEL_OPTION;
        int info = JOptionPane.INFORMATION_MESSAGE;
        int option = JOptionPane.showConfirmDialog(null, fields, "Direct Investment", pane, info);

        if (option == JOptionPane.OK_OPTION) {

            int investor_id = Integer.parseInt(iidField.getText());
            double equity = Double.parseDouble(equityField.getText());
            double amount = Double.parseDouble(amountField.getText());
            int cid = Integer.parseInt(cidField.getText());
            try {
                if (!dbHandler.companyExists(cid)) {
                    throw new CompanyDoesNotExistException();
                }
                if (!dbHandler.investorExists(investor_id)) {
                    throw new InvestorDoesNotExistException();
                }
                if (equity < 0) {
                    throw new NegativeNumberException();
                }
                if (equity > 100) {
                    throw new EquityOutOfBoundsException();
                }
                dbHandler.insertDirectInvestment(investor_id, cid, equity, amount);
            } catch (CompanyDoesNotExistException e) {
                JOptionPane.showMessageDialog(null, "Company Does not Exist", "Invalid", JOptionPane.ERROR_MESSAGE);
                directInvestment();
            } catch (InvestorDoesNotExistException e) {
                JOptionPane.showMessageDialog(null, "Investor Does not Exist", "Invalid", JOptionPane.ERROR_MESSAGE);
                directInvestment();
            } catch (NegativeNumberException e) {
                JOptionPane.showMessageDialog(null, "Equity % Must not be negative", "Invalid", JOptionPane.ERROR_MESSAGE);
                directInvestment();
            } catch (EquityOutOfBoundsException e) {
                JOptionPane.showMessageDialog(null, "Equity must be <= 100", "Invalid", JOptionPane.ERROR_MESSAGE);
                directInvestment();
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(null, "ensure all inputs are valid", "Invalid", JOptionPane.ERROR_MESSAGE);
                directInvestment();
            }

            //make investment
        }
    }

}
