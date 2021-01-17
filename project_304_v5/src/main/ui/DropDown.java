package main.ui;

import main.database.DatabaseConnectionHandler;
import main.exception.EmptyInputException;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.Objects;

public class DropDown extends JPanel implements ItemListener {
    private Application app;
    private DatabaseConnectionHandler dbHandler = null;
    static JComboBox<String> s;

    //TODO: there's a bug where if you press 'ok' when there is no information
    //TODO: in a box, then the whole program dies

    final String s0 = "Select information you want here";
    final String s1 = "find names of investors of specific investing group";
    final String s2 = "find investorID of investors from a specific institution";
    final String s3 = "find primary investors with over a certain% equity in a certain company";
    final String s4 = "find average stock price of a company";
    final String s5 = "find list the sum of transaction prices of investors for each company";
    final String s6 = "find average number of stocks purchased by investor";
    final String s7 = "find all the investors that have made trades in all the currencies";

    public DropDown(Application app, DatabaseConnectionHandler a) {
        this.app = app;
        dbHandler = a;
        setLayout(new GridLayout(0, 1));
        JLabel infoTitle = new JLabel("Information", SwingConstants.CENTER);
        add(infoTitle);
        String[] options = {s0, s1, s2, s3, s4, s5, s6, s7};
        s = new JComboBox<>(options);
        s.addItemListener(this);
        add(s);
    }

    private void allCurrencies() {
        // sql statement
        System.out.println("do something");
        dbHandler.doSomething();
        dbHandler.displayAllCurrencies();
    }

    private void avgNumStocks() {
        // sql statement
        System.out.println("avgNumStocks");
        dbHandler.displayAvgNumStocks(); //TODO
    }

    private void companyInvestments() {
        JTextField cidField = new JTextField();
        JTextField minField = new JTextField();

        Object[] fields = {"Company ID", cidField, "minimum payment", minField};

        int pane = JOptionPane.OK_CANCEL_OPTION;
        int info = JOptionPane.INFORMATION_MESSAGE;
        int option = JOptionPane.showConfirmDialog(null, fields, "top investors", pane, info);

        if(option == JOptionPane.OK_OPTION) {
            try {
                int cid = Integer.parseInt(cidField.getText());
                double min = Double.parseDouble(minField.getText());
                dbHandler.displayCompanyInvestments(cid, min); //TODO eliminate cid
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, "ensure all inputs are filled in", "Invalid", JOptionPane.ERROR_MESSAGE);
                companyInvestments();
            }


        }
    }

    private void topInvestors() {
        JTextField cidField = new JTextField();
        JTextField minField = new JTextField();
        Object[] fields = {"Company ID", cidField, "minimum equity %", minField};

        int pane = JOptionPane.OK_CANCEL_OPTION;
        int info = JOptionPane.INFORMATION_MESSAGE;
        int option = JOptionPane.showConfirmDialog(null, fields, "top investors", pane, info);

        if(option == JOptionPane.OK_OPTION) {
            int cid = Integer.parseInt(cidField.getText());
            double min = Double.parseDouble(minField.getText());

            dbHandler.displayTopInvestors(cid, min); //TODO
        }
    }

    private void avgStockPriceOfCompany() {
        JTextField cidField = new JTextField();
        Object[] fields = {"Company ID", cidField};

        int pane = JOptionPane.OK_CANCEL_OPTION;
        int info = JOptionPane.INFORMATION_MESSAGE;
        int option = JOptionPane.showConfirmDialog(null, fields, "average price", pane, info);

        if(option == JOptionPane.OK_OPTION) {
            int cid = Integer.parseInt(cidField.getText());
            //load in

            dbHandler.displayAvgStockPriceOfCompany(cid); //TODO
        }
    }

    private void investorsFromInstitutionButton() {
        JButton institutionButton = new JButton("Find investor IDs of investors from a specific institution");
        institutionButton.addActionListener(e -> {
            investorsFromInstitution();
        });
        add(institutionButton);
    }

    private void investorsFromInstitution() {
        JTextField nameField = new JTextField();
        Object[] fields = {"Institution name ", nameField};

        int pane = JOptionPane.OK_CANCEL_OPTION;
        int info = JOptionPane.INFORMATION_MESSAGE;
        int option = JOptionPane.showConfirmDialog(null, fields, "Investing Group members", pane, info);

        if(option == JOptionPane.OK_OPTION) {
            try {
                String name = nameField.getText();
                //load in

                dbHandler.displayInvestorsFromInstitution(name); //TODO

                if (name.equals("")) {
                    throw new EmptyInputException();
                }
            } catch (EmptyInputException e) {
                JOptionPane.showMessageDialog(null, "ensure all inputs are filled in", "Invalid", JOptionPane.ERROR_MESSAGE);
                investorsFromInstitution();
            }


        }
    }

    private void investorsOfInvestingGroup() {
        JTextField nameField = new JTextField();
        Object[] fields = {"Investing Group name ", nameField};

        int pane = JOptionPane.OK_CANCEL_OPTION;
        int info = JOptionPane.INFORMATION_MESSAGE;
        int option = JOptionPane.showConfirmDialog(null, fields, "Investing Group members", pane, info);

        if(option == JOptionPane.OK_OPTION) {
            String name = nameField.getText();
            //load in
            dbHandler.displayInvestorsOfInvestingGroup(name);
        }
    }

    @Override
    public void itemStateChanged(ItemEvent e) {
        if (e.getSource() == s) {
            String selected = (String)s.getSelectedItem();
            switch(Objects.requireNonNull(selected)) {
                case s1:
                    investorsOfInvestingGroup();
                    break;
                case s2:
                    investorsFromInstitution();
                    break;
                case s3:
                    topInvestors();
                    break;
                case s4:
                    avgStockPriceOfCompany();
                    break;
                case s5:
                    companyInvestments();
                    break;
                case s6:
                    avgNumStocks();
                    break;
                case s7:
                    allCurrencies();
                    break;
            }
            app.dispose();
            new Application(dbHandler);

        }
    }
}
