package main.ui;
import main.database.DatabaseConnectionHandler;

import java.awt.*;
import java.util.ArrayList;
import javax.swing.*;

public class Application extends JFrame{

    private DatabaseConnectionHandler dbHandler = null;

    public Application(DatabaseConnectionHandler a) {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setTitle("Capital Market");
        setLayout(new BorderLayout());

        dbHandler = a;
        dbHandler.setApp(this);


        Buttons buttons = new Buttons(this, dbHandler);
        add(buttons, BorderLayout.PAGE_START);
        DropDown info = new DropDown(this, dbHandler);
        add(info);
        pack();
        setVisible(true);
    }

    public void displayResult(ArrayList<String> results) {
        System.out.println("displayResult in application was called!");
        String temp;
        for (int i = 0; i < results.size(); i += 5) {
            temp = "";
            for(int k = i; k < i + 5; k++) {
                if (k < results.size()) {
                    temp += results.get(k) + "\n";
                }
            }

            JOptionPane.showMessageDialog(null, temp);
        }
    }



}
