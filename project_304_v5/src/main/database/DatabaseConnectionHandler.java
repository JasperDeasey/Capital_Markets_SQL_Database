package main.database;

import main.model.CompanyModel;
import main.model.DirectInvestmentModel;
import main.model.InvestorModel;
import main.model.TransactionModel;
import main.ui.Application;

import javax.swing.plaf.nimbus.State;
import java.io.File;
import java.io.FileNotFoundException;
import java.sql.*;
import java.util.ArrayList;
import java.util.Scanner;

public class DatabaseConnectionHandler {
    private static final String ORACLE_URL = "jdbc:oracle:thin:@localhost:1522:stu";
    private static final String EXCEPTION_TAG = "[EXCEPTION]";
    private static final String WARNING_TAG = "[WARNING]";

    private Connection connection = null;
    private Application application = null;

    // reference: some of this code was taken from Tutorial 6
    public DatabaseConnectionHandler() {
        try {
            // Load the Oracle JDBC driver
            // Note that the path could change for new drivers
            DriverManager.registerDriver(new oracle.jdbc.driver.OracleDriver());
        } catch (SQLException e) {
            System.out.println(EXCEPTION_TAG + " " + e.getMessage());
        }
    }

    public void setApp(Application a) {
        application = a;
    }


    public void insertTransaction(int transactionID, int companyID, String type, String exchangeName, String currency, int investorID, String date, double stockPrice, int quantity) {
        try {
            PreparedStatement transactionBasicData = connection.prepareStatement("INSERT INTO TransactionBasicData VALUES (?,?,?,?,?)");
            transactionBasicData.setInt(1, transactionID);
            transactionBasicData.setInt(2, companyID);
            transactionBasicData.setString(3, type);
            transactionBasicData.setString(4, exchangeName);
            transactionBasicData.setString(5, currency);

            PreparedStatement makesTransaction = connection.prepareStatement("INSERT INTO MakesTransaction VALUES (?,?)");
            makesTransaction.setInt(1, investorID);
            makesTransaction.setInt(2, transactionID);

            PreparedStatement dateOfPurchase = connection.prepareStatement("INSERT INTO DateOfPurchase VALUES (?,?)");
            dateOfPurchase.setString(1, date);
            dateOfPurchase.setInt(2, transactionID);

            PreparedStatement priceOfIndividualStock = connection.prepareStatement("INSERT INTO PriceOfIndividualStock VALUES (?,?)");
            priceOfIndividualStock.setDouble(1, stockPrice);
            priceOfIndividualStock.setInt(2, transactionID);

            PreparedStatement purchasePrice = connection.prepareStatement("INSERT INTO PurchasePrice VALUES (?,?,?)");

            purchasePrice.setInt(1, quantity);
            purchasePrice.setDouble(2, stockPrice);
            purchasePrice.setDouble(3, stockPrice * quantity);

            if (!currencyExists(currency)) {
                PreparedStatement currencyStmt = connection.prepareStatement("INSERT INTO Currency VALUES (?)");
                currencyStmt.setString(1, currency);
                currencyStmt.executeUpdate();
            }

            if (!exchangeExists(exchangeName)) {
                PreparedStatement exchangeStmt = connection.prepareStatement("INSERT INTO Exchange VALUES (?)");
                exchangeStmt.setString(1, exchangeName);
                exchangeStmt.executeUpdate();
            }

            transactionBasicData.executeUpdate();
            makesTransaction.executeUpdate();
            dateOfPurchase.executeUpdate();
            priceOfIndividualStock.executeUpdate();
            purchasePrice.executeUpdate();

            connection.commit();

            transactionBasicData.close();
            makesTransaction.close();
            dateOfPurchase.close();
            priceOfIndividualStock.close();
            purchasePrice.close();
        } catch (SQLException e) {
            System.out.println(EXCEPTION_TAG + " " + e.getMessage());
            rollbackConnection();
        }
    }

    public int createCompanyCID() {
        int companyCID = 5000;
        boolean isIDAvailable = false;
        while (isIDAvailable == false) {
            try {
                PreparedStatement ps = connection.prepareStatement("SELECT * FROM Company_Name WHERE CID = ?");
                ps.setInt(1, companyCID);

                int rowCount = ps.executeUpdate();
                if (rowCount == 0) {
                    isIDAvailable = true;
                } else {
                    companyCID++;
                }

                ps.close();
            } catch (SQLException e) {
                System.out.println(EXCEPTION_TAG + " " + e.getMessage());
                rollbackConnection();
            }
        }
        return companyCID;
    }

    public int createInvestorID() {
        int investorID = 1000;
        boolean isIDAvailable = false;
        while (isIDAvailable == false) {
            try {
                PreparedStatement ps = connection.prepareStatement("SELECT * FROM Investor WHERE investorID = ?");
                ps.setInt(1, investorID);

                int rowCount = ps.executeUpdate();
                if (rowCount == 0) {
                    isIDAvailable = true;
                } else {
                    investorID++;
                }

                ps.close();
            } catch (SQLException e) {
                System.out.println(EXCEPTION_TAG + " " + e.getMessage());
                rollbackConnection();
            }
        }
        return investorID;
    }

    public int createTransactionID() {
        int transactionID = 3000;
        boolean isIDAvailable = false;
        while (isIDAvailable == false) {
            try {
                PreparedStatement ps = connection.prepareStatement("SELECT * FROM TransactionBasicData WHERE transactionID = ?");
                ps.setInt(1, transactionID);

                int rowCount = ps.executeUpdate();
                if (rowCount == 0) {
                    isIDAvailable = true;
                } else {
                    transactionID++;
                }

                ps.close();
            } catch (SQLException e) {
                System.out.println(EXCEPTION_TAG + " " + e.getMessage());
                rollbackConnection();
            }
        }
        return transactionID;
    }

    public boolean fidExists(int fid) {
            try {
                PreparedStatement ps = connection.prepareStatement("SELECT * FROM Financial_Advisor WHERE FID = ?");
                ps.setInt(1, fid);

                int rowCount = ps.executeUpdate();
                ps.close();
                if (rowCount != 0) {
                    return true;
                } else {
                    return false;
                }
            } catch (SQLException e) {
                System.out.println(EXCEPTION_TAG + " " + e.getMessage());
                rollbackConnection();
                return false;
            }
    }

    public boolean cidExists(int cid) {
        try {
            PreparedStatement ps = connection.prepareStatement("SELECT * FROM Company_Name WHERE cid = ?");
            ps.setInt(1, cid);

            int rowCount = ps.executeUpdate();
            ps.close();
            if (rowCount != 0) {
                return true;
            } else {
                return false;
            }
        } catch (SQLException e) {
            System.out.println(EXCEPTION_TAG + " " + e.getMessage());
            rollbackConnection();
            return false;
        }
    }

    public boolean currencyExists(String currency) {
        try {
            PreparedStatement ps = connection.prepareStatement("SELECT * FROM Currency WHERE currencyName = ?");
            ps.setString(1, currency);

            int rowCount = ps.executeUpdate();
            ps.close();
            if (rowCount != 0) {
                return true;
            } else {
                return false;
            }
        } catch (SQLException e) {
            System.out.println(EXCEPTION_TAG + " " + e.getMessage());
            rollbackConnection();
            return false;
        }
    }

    public boolean exchangeExists(String exchangeName) {
        try {
            PreparedStatement ps = connection.prepareStatement("SELECT * FROM Exchange WHERE exchangeName = ?");
            ps.setString(1, exchangeName);

            int rowCount = ps.executeUpdate();
            ps.close();
            if (rowCount != 0) {
                return true;
            } else {
                return false;
            }
        } catch (SQLException e) {
            System.out.println(EXCEPTION_TAG + " " + e.getMessage());
            rollbackConnection();
            return false;
        }
    }

    public boolean financialInstrumentExists(int cid) {
        try {
            PreparedStatement ps = connection.prepareStatement("SELECT * FROM FinancialInstrument WHERE cid = ?");
            ps.setInt(1, cid);

            int rowCount = ps.executeUpdate();
            ps.close();
            if (rowCount != 0) {
                return true;
            } else {
                return false;
            }
        } catch (SQLException e) {
            System.out.println(EXCEPTION_TAG + " " + e.getMessage());
            rollbackConnection();
            return false;
        }
    }

    public boolean directInvestmentExists(int cid) {
        try {
            PreparedStatement ps = connection.prepareStatement("SELECT * FROM MakesDirectInvestment WHERE cid = ?");
            ps.setInt(1, cid);

            int rowCount = ps.executeUpdate();
            ps.close();
            if (rowCount != 0) {
                return true;
            } else {
                return false;
            }
        } catch (SQLException e) {
            System.out.println(EXCEPTION_TAG + " " + e.getMessage());
            rollbackConnection();
            return false;
        }
    }

    public boolean companyExists(int cid) {
        try {
            PreparedStatement ps = connection.prepareStatement("SELECT * FROM Company_Name WHERE cid = ?");
            ps.setInt(1, cid);

            int rowCount = ps.executeUpdate();
            ps.close();
            if (rowCount != 0) {
                return true;
            } else {
                return false;
            }
        } catch (SQLException e) {
            System.out.println(EXCEPTION_TAG + " " + e.getMessage());
            rollbackConnection();
            return false;
        }
    }

    //investorHasMadeDirectInvestmentInCompany
    public boolean investorHasMadeDirectInvestmentInCompany(int investorID, int cid) {
        try {
            PreparedStatement ps = connection.prepareStatement("SELECT * FROM MakesDirectInvestment WHERE cid = ? AND investorID = ?");
            ps.setInt(1, cid);
            ps.setInt(2, investorID);
            int rowCount = ps.executeUpdate();
            ps.close();
            if (rowCount != 0) {
                return true;
            } else {
                return false;
            }
        } catch (SQLException e) {
            System.out.println(EXCEPTION_TAG + " " + e.getMessage());
            rollbackConnection();
            return false;
        }
    }

    public boolean investorExists(int cid) {
        try {
            PreparedStatement ps = connection.prepareStatement("SELECT * FROM Investor WHERE investorID = ?");
            ps.setInt(1, cid);

            int rowCount = ps.executeUpdate();
            ps.close();
            if (rowCount != 0) {
                return true;
            } else {
                return false;
            }
        } catch (SQLException e) {
            System.out.println(EXCEPTION_TAG + " " + e.getMessage());
            rollbackConnection();
            return false;
        }
    }

    public String getFinancialInstrumentType(int cid) {
        try {
            PreparedStatement isStock = connection.prepareStatement("SELECT * FROM FinancialInstrument WHERE cid = ? AND type = 'stock'");
            isStock.setInt(1, cid);

            int stockRowCount = isStock.executeUpdate();
            isStock.close();
            if (stockRowCount != 0) {
                return "stock";
            }
        } catch (SQLException e) {
            System.out.println(EXCEPTION_TAG + " " + e.getMessage());
        }
        try {
            PreparedStatement isBond = connection.prepareStatement("SELECT * FROM FinancialInstrument WHERE cid = ? AND type = 'bond'");
            isBond.setInt(1, cid);

            int bondRowCount = isBond.executeUpdate();
            isBond.close();
            if (bondRowCount != 0) {
                return "bond";
            }
        } catch (SQLException e) {
            System.out.println(EXCEPTION_TAG + " " + e.getMessage());
            rollbackConnection();
        }
        return "stock";
    }

    public void insertDirectInvestment(int investorID, int companyID, double equityPercent, double investmentValue) {
        try {
            PreparedStatement directInvestmentPS = connection.prepareStatement("INSERT INTO MakesDirectInvestment VALUES (?,?,?,?)");
            directInvestmentPS.setInt(1, investorID);
            directInvestmentPS.setDouble(2, equityPercent);
            directInvestmentPS.setDouble(3, investmentValue);
            directInvestmentPS.setInt(4, companyID);

            directInvestmentPS.executeUpdate();
            connection.commit();

            directInvestmentPS.close();

            try {
                if (!secondaryInvestorExists(investorID)){
                    String secondaryInvestorSql;
                    Statement stmt = connection.createStatement();
                    secondaryInvestorSql = "INSERT INTO SecondaryInvestor VALUES (" + investorID + "," + "NULL )";
                    stmt.executeUpdate(secondaryInvestorSql);
                    stmt.close();
                }
            } catch (SQLException e) {
                System.out.println(EXCEPTION_TAG + " " + e.getMessage());
                rollbackConnection();
            }

        } catch (SQLException e) {
            System.out.println(EXCEPTION_TAG + " " + e.getMessage());
            rollbackConnection();
        }
    }

    public void insertInvestor(int investorId, String name, int fid, String institution) {
        try {
            String investorSql;
            Statement stmt = connection.createStatement();
            if (fid == 0) {
                investorSql = "INSERT INTO Investor VALUES (" + investorId + "," + "NULL"+ ", '" + name + "')";
            } else {
                investorSql = "INSERT INTO Investor VALUES (" + investorId + "," + fid+ ", '" + name + "')";
            }


            if(!institutionExists(institution)) {
                String institutionSql;
                institutionSql = "INSERT INTO Institution VALUES ( '" + institution + "')";
                stmt.executeUpdate(institutionSql);
            }

            String educationSql = "INSERT INTO Education VALUES ( " + investorId + " , '" + institution + "')";



            //CREATE TABLE Institution (institutionName CHAR(20), PRIMARY KEY(institutionName))
            //CREATE TABLE Education (investorID INTEGER, institutionName CHAR(20), PRIMARY KEY(investorID,institutionName), FOREIGN KEY (institutionName) REFERENCES Institution, FOREIGN KEY (investorID) REFERENCES Investor)

            stmt.executeUpdate(investorSql);
            stmt.executeUpdate(educationSql);
            stmt.close();
        } catch (SQLException e) {
            System.out.println(EXCEPTION_TAG + " " + e.getMessage());
            rollbackConnection();
        }
    }

    public void insertCompany(int cid, String name, String address) {
            try {
                String companyNameSQL;
                String companyAddressSQL;
                Statement stmt = connection.createStatement();
                if (name.equals("NULL") && address.equals("NULL")) {
                    companyNameSQL = "INSERT INTO Company_Name VALUES (" + cid + ", " + name + ")";
                    companyAddressSQL = "INSERT INTO Company_Address VALUES (" + cid + ", " + address + ")";
                } else if (name.equals("NULL")) {
                    companyNameSQL = "INSERT INTO Company_Name VALUES (" + cid + ", "  + name + ")";
                    companyAddressSQL = "INSERT INTO Company_Address VALUES (" + cid + ", '" + address + "')";
                } else if (address.equals("NULL")) {
                    companyNameSQL = "INSERT INTO Company_Name VALUES (" + cid + ", '"  + name + "')";
                    companyAddressSQL = "INSERT INTO Company_Address VALUES (" + cid + ", " + address + ")";
                } else {
                    companyNameSQL = "INSERT INTO Company_Name VALUES (" + cid + ", '"  + name + "')";
                    companyAddressSQL = "INSERT INTO Company_Address VALUES (" + cid + ", '" + address + "')";
                }

                stmt.executeUpdate(companyNameSQL);
                stmt.executeUpdate(companyAddressSQL);
                stmt.close();
            } catch (SQLException e) {
                System.out.println(EXCEPTION_TAG + " " + e.getMessage());
                rollbackConnection();
            }
    }

    public void deleteCompany(int companyID) {
        try {
            String companyNameSQL;
            String companyAddressSQL;
            Statement stmt = connection.createStatement();

            if (financialInstrumentExists(companyID)) {
                deleteTransactionBasicData(companyID);
                setFinancialInstrumentTypeToNull(companyID);
            }

            if (directInvestmentExists(companyID)) {
                deleteCompanyFromDirectInvestmentTable(companyID);
            }

            // CREATE TABLE MakesDirectInvestment (investorID INTEGER, equity numeric, amountInvested numeric, cid INTEGER, PRIMARY KEY(investorID, cid), FOREIGN KEY(investorID) REFERENCES Investor, FOREIGN KEY(cid) REFERENCES Company_Name)

            companyNameSQL = "DELETE FROM Company_Name WHERE cid = " + companyID;
            companyAddressSQL = "DELETE FROM Company_Address WHERE cid = " + companyID;

            stmt.executeUpdate(companyNameSQL);
            stmt.executeUpdate(companyAddressSQL);
            stmt.close();
        } catch (SQLException e) {
            System.out.println(EXCEPTION_TAG + " " + e.getMessage());
            rollbackConnection();
        }
    }

    private void deleteCompanyFromDirectInvestmentTable(int companyID) {
        try {
            Statement directInvestmentStmt = connection.createStatement();
            String directInvestmentSQL = "DELETE FROM MakesDirectInvestment WHERE cid = " + companyID;
            directInvestmentStmt.executeUpdate(directInvestmentSQL);
            directInvestmentStmt.close();
        } catch (SQLException e) {
            System.out.println(EXCEPTION_TAG + " " + e.getMessage());
        }
    }

    private void setFinancialInstrumentTypeToNull(int companyID) {
        try {
            Statement financialInstrumentStmt = connection.createStatement();
            String financialInstrumentSQL = "DELETE FROM FinancialInstrument WHERE cid = " + companyID;
            financialInstrumentStmt.executeUpdate(financialInstrumentSQL);
            financialInstrumentStmt.close();
        } catch (SQLException e) {
            System.out.println(EXCEPTION_TAG + " " + e.getMessage());
        }
    }

    private void deleteTransactionBasicData(int companyID) {
        String type = getFinancialInstrumentType(companyID);
        try {
            Statement transactionStmt = connection.createStatement();
            String transactionBasicDataSQL = "UPDATE TransactionBasicData Set type = NULL WHERE cid = " + companyID + " AND type = '" + type + "'";
            transactionStmt.executeUpdate(transactionBasicDataSQL);
            transactionStmt.close();
        } catch (SQLException e) {
            System.out.println(EXCEPTION_TAG + " " + e.getMessage());
        }
    }

    public boolean institutionExists(String institutionName) {
        try {
            Statement stmt = connection.createStatement();
            String sql = "SELECT * FROM Institution WHERE institutionName = '" + institutionName + "'";
            int rowCount = stmt.executeUpdate(sql);
            stmt.close();
            if (rowCount != 0) {
                return true;
            } else {
                return false;
            }
        } catch (SQLException e) {
            System.out.println(EXCEPTION_TAG + " " + e.getMessage());
            rollbackConnection();
            return false;
        }
    }

    public boolean primaryInvestorExists(int investorID) {
        try {
            PreparedStatement ps = connection.prepareStatement("SELECT * FROM PrimaryInvestor WHERE investorID = ?");
            ps.setInt(1, investorID);

            int rowCount = ps.executeUpdate();
            ps.close();
            if (rowCount != 0) {
                return true;
            } else {
                return false;
            }
        } catch (SQLException e) {
            System.out.println(EXCEPTION_TAG + " " + e.getMessage());
            rollbackConnection();
            return false;
        }
    }

    public boolean secondaryInvestorExists(int investorID) {
        try {
            PreparedStatement ps = connection.prepareStatement("SELECT * FROM SecondaryInvestor WHERE investorID = ?");
            ps.setInt(1, investorID);

            int rowCount = ps.executeUpdate();
            ps.close();
            if (rowCount != 0) {
                return true;
            } else {
                return false;
            }
        } catch (SQLException e) {
            System.out.println(EXCEPTION_TAG + " " + e.getMessage());
            rollbackConnection();
            return false;
        }
    }

    public boolean investorEducationExists(int investorID) {
        try {
            PreparedStatement ps = connection.prepareStatement("SELECT * FROM Education WHERE investorID = ?");
            ps.setInt(1, investorID);

            int rowCount = ps.executeUpdate();
            ps.close();
            if (rowCount != 0) {
                return true;
            } else {
                return false;
            }
        } catch (SQLException e) {
            System.out.println(EXCEPTION_TAG + " " + e.getMessage());
            rollbackConnection();
            return false;
        }
    }

    public boolean investorIsInInvestingGroup(int investorID) {
        try {
            PreparedStatement ps = connection.prepareStatement("SELECT * FROM InvestorJoinsInvestingGroup WHERE investorID = ?");
            ps.setInt(1, investorID);

            int rowCount = ps.executeUpdate();
            ps.close();
            if (rowCount != 0) {
                return true;
            } else {
                return false;
            }
        } catch (SQLException e) {
            System.out.println(EXCEPTION_TAG + " " + e.getMessage());
            rollbackConnection();
            return false;
        }
    }

    public boolean investorHasMadeDirectInvestment(int investorID) {
        try {
            PreparedStatement ps = connection.prepareStatement("SELECT * FROM MakesDirectInvestment WHERE investorID = ?");
            ps.setInt(1, investorID);

            int rowCount = ps.executeUpdate();
            ps.close();
            if (rowCount != 0) {
                return true;
            } else {
                return false;
            }
        } catch (SQLException e) {
            System.out.println(EXCEPTION_TAG + " " + e.getMessage());
            rollbackConnection();
            return false;
        }
    }

    public boolean investorMadeTransaction(int investorID) {
        try {
            PreparedStatement ps = connection.prepareStatement("SELECT * FROM MakesTransaction WHERE investorID = ?");
            ps.setInt(1, investorID);

            int rowCount = ps.executeUpdate();
            ps.close();
            if (rowCount != 0) {
                return true;
            } else {
                return false;
            }
        } catch (SQLException e) {
            System.out.println(EXCEPTION_TAG + " " + e.getMessage());
            rollbackConnection();
            return false;
        }
    }

    private void deletePrimaryInvestor(int investorID) {
        try {
            Statement stmt = connection.createStatement();
            String sql = "DELETE FROM PrimaryInvestor WHERE investorID = " + investorID;
            stmt.executeUpdate(sql);
            stmt.close();
        } catch (SQLException e) {
            System.out.println(EXCEPTION_TAG + " " + e.getMessage());
        }
    }

    private void deleteSecondaryInvestor(int investorID) {
        try {
            Statement stmt = connection.createStatement();
            String sql = "DELETE FROM SecondaryInvestor WHERE investorID = " + investorID;
            stmt.executeUpdate(sql);
            stmt.close();
        } catch (SQLException e) {
            System.out.println(EXCEPTION_TAG + " " + e.getMessage());
        }
    }

    private void removeInvestorFromEducation(int investorID) {
        try {
            Statement stmt = connection.createStatement();
            String sql = "DELETE FROM Education WHERE investorID = " + investorID;
            stmt.executeUpdate(sql);
            stmt.close();
        } catch (SQLException e) {
            System.out.println(EXCEPTION_TAG + " " + e.getMessage());
        }
    }

    private void removeInvestorFromInvestingGroup(int investorID) {
        try {
            Statement stmt = connection.createStatement();
            String sql = "DELETE FROM InvestorJoinsInvestingGroup WHERE investorID = " + investorID;
            stmt.executeUpdate(sql);
            stmt.close();
        } catch (SQLException e) {
            System.out.println(EXCEPTION_TAG + " " + e.getMessage());
        }
    }

    private void removeDirectInvestment(int investorID) {
        try {
            Statement stmt = connection.createStatement();
            String sql = "DELETE FROM MakesDirectInvestment WHERE investorID = " + investorID;
            stmt.executeUpdate(sql);
            stmt.close();
        } catch (SQLException e) {
            System.out.println(EXCEPTION_TAG + " " + e.getMessage());
        }
    }

    private void removeInvestorTransaction(int investorID) {
        try {
            Statement stmt = connection.createStatement();
            String sql = "DELETE FROM MakesTransaction WHERE investorID = " + investorID;
            stmt.executeUpdate(sql);
            stmt.close();
        } catch (SQLException e) {
            System.out.println(EXCEPTION_TAG + " " + e.getMessage());
        }
    }

    public void deleteInvestor(int investorID) {
        try {
            if (primaryInvestorExists(investorID)) {
                deletePrimaryInvestor(investorID);
            }
            if (secondaryInvestorExists(investorID)) {
                deleteSecondaryInvestor(investorID);
            }
            if (investorEducationExists(investorID)) {
                removeInvestorFromEducation(investorID);
            }
            if (investorIsInInvestingGroup(investorID)) {
                removeInvestorFromInvestingGroup(investorID);
            }
            if (investorHasMadeDirectInvestment(investorID)) {
                removeDirectInvestment(investorID);
            }
            if (investorMadeTransaction(investorID)) {
                removeInvestorTransaction(investorID);
            }
            PreparedStatement ps = connection.prepareStatement("DELETE FROM Investor WHERE investorID = ?");
            ps.setInt(1, investorID);

            int rowCount = ps.executeUpdate();
            if (rowCount == 0) {
                System.out.println(WARNING_TAG + " Company " + investorID + " does not exist!");
            }

            connection.commit();

            ps.close();
        } catch (SQLException e) {
            System.out.println(EXCEPTION_TAG + " " + e.getMessage());
            rollbackConnection();
        }
    }

    public void updatePrimaryInvestorEquity(int investorID, double newEquityPercent, double totalInvestment, int companyID) {
        try {
            PreparedStatement ps = connection.prepareStatement("UPDATE MakesDirectInvestment SET equity = ?, amountInvested = ? WHERE investorID = ? AND cid = ?");
            ps.setDouble(1, newEquityPercent);
            ps.setDouble(2, totalInvestment);
            ps.setInt(3, investorID);
            ps.setInt(4, companyID);

            int rowCount = ps.executeUpdate();
            if (rowCount == 0) {
                System.out.println(WARNING_TAG + " Investor " + investorID + " paired with company " + companyID + " does not exist!");
       }

            connection.commit();

            ps.close();
        } catch (SQLException e) {
            System.out.println(EXCEPTION_TAG + " " + e.getMessage());
            rollbackConnection();
        }
    }

    public void databaseSetup() {
        clearData();
        try {
            int lineNum = 0;
            File sqlFile = new File("C:\\Users\\user\\Desktop\\UBC\\Year 2\\Cpsc 304\\Project\\project_new\\project_304_v5\\project_304_v5\\src\\main\\sql\\scripts\\databaseSetup.sql"); //!*!
            Scanner myReader = new Scanner(sqlFile);
            while (myReader.hasNext()) {
                lineNum += 1;
                try {
                    Statement stmt = connection.createStatement();
                    String sql = myReader.nextLine().trim();
                    stmt.executeUpdate(sql);
                    stmt.close();
                    System.out.println("SUCCESSFULLY INSERTED: " + sql);
                } catch (SQLException e) {
                    System.out.println(lineNum + EXCEPTION_TAG + " " + e.getMessage());
                    rollbackConnection();
                }
            }
        } catch (FileNotFoundException e) {
            System.out.println("FILE NOT FOUND!");
        }
        showTables();
    }

    public boolean login(String username, String password) {
        try {
            if (connection != null) {
                connection.close();
            }

            connection = DriverManager.getConnection(ORACLE_URL, username, password);
            connection.setAutoCommit(false);

            System.out.println("\nConnected to Oracle!");
            return true;
        } catch (SQLException e) {
            System.out.println(EXCEPTION_TAG + " " + e.getMessage());
            return false;
        }
    }

    private void rollbackConnection() {
        try  {
            connection.rollback();
        } catch (SQLException e) {
            System.out.println(EXCEPTION_TAG + " " + e.getMessage());
        }
    }

    public void clearData() {
        try {
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery("select table_name from user_tables");

            while(rs.next()) {
                try {
                    Statement stmt2 = connection.createStatement();
                    String tableName = rs.getString(1);
                    stmt2.execute("DROP TABLE " + tableName + " cascade constraints");
                    System.out.println("table deleted: " + tableName);
                    stmt2.close();
                } catch (SQLException e) {
                    System.out.println(EXCEPTION_TAG + " " + e.getMessage());
                }
            }
            rs.close();
        } catch (SQLException e) {
            System.out.println(EXCEPTION_TAG + " " + e.getMessage());
        }
        System.out.println("done deleting tables!");
    }

    public void showTables() {
        try {
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery("select table_name from user_tables");

            while(rs.next()) {
                String tableName = rs.getString(1);
                System.out.println("showing table: " + tableName);
            }

            rs.close();
            stmt.close();
        } catch (SQLException e) {
            System.out.println(EXCEPTION_TAG + " " + e.getMessage());
        }
        System.out.println("done getting tables!");
    }


    /////////////////////////////////////////////////////////////////////////////////
    // all of the below is kevin's work //
    //TODO !!! get rid of this after testing
    //TODO !!! get rid of this after testing
    public void doSomething() {
        System.out.println("doSomething was called!");

        try {
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM c");

            while(rs.next()) {

                System.out.println(rs.getInt("investorID"));
                System.out.println(rs.getInt("second"));
            }

            rs.close();
            stmt.close();
        } catch (SQLException e) {
            System.out.println(EXCEPTION_TAG + " " + e.getMessage());
        }
    }



    //-----------------------------------------------------------
    // ***************** ALL DISPLAY FUNCTIONS ******************
    //-----------------------------------------------------------





    //TODO: Do this function. probably will need to have this call application.
    public void displayResult(ArrayList<String> results) {
        System.out.println("Display Results was called!");
        for (int i = 0; i < results.size(); i++) {
            System.out.println(results.get(i));
        }

        application.displayResult(results);

    }


    /**
     *
     * @param cols the number of columns we plan to display
     * @param colName the names of the columns we're getting
     * @param sqlStatement the sql statement we're executing to get the results
     * @param colPattern the order of ints / strings we should get our stuff as,
     *                   with int = 0, string = 1, numeric = 2
     */
    public void displayMain(int cols, String sqlStatement, String[] colName, int[] colPattern) {
        System.out.println("************ Display Main was called! ************");

        ArrayList<String> results = new ArrayList<>();
        String[] temporary = new String[cols];
        String currentString;

        try {
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(sqlStatement);
            System.out.println("Sql statement executed!");

            //iterates over while loop, stores the values in an arraylist of strings
            while(rs.next()) {
                currentString = "";

                for (int i = 0; i < cols; i++) {
                    //TODO move temporary[i] up here.
                    if (colPattern[i] == 0) {
                        temporary[i] = ", " + rs.getInt(colName[i]);
                    } else if (colPattern[i] == 1) {
                        temporary[i] = ", " + rs.getString(colName[i]);
                    } else if (colPattern[i] == 2) {
                        temporary[i] = ", " + rs.getDouble(colName[i]);
                    }

                    //TODO maybe make this more efficient
                    for (int k = 0; k < temporary[i].length() - 1; k++) {
                        if (temporary[i].charAt(k) == ' ' && temporary[i].charAt(k + 1) == ' ') {
                            temporary[i] = temporary[i].substring(0, k);
                        }
                    }
                }

                temporary[0] = temporary[0].substring(1);

                for(int i = 0; i < temporary.length; i++) {
                    currentString += temporary[i];
                }
                results.add(currentString);
            }

            //use a single display function to display the strings. Pass in the arrayList
            displayResult(results);

            rs.close();
            stmt.close();
        } catch (SQLException e) {
            System.out.println(EXCEPTION_TAG + " " + e.getMessage());
        }
    }


    public void displayMain(int cols, String sqlStatement, int[] colName, int[] colPattern) {
        System.out.println("************ Display Main 2 was called! ************");

        ArrayList<String> results = new ArrayList<>();
        String[] temporary = new String[cols];
        String currentString;

        try {
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(sqlStatement);
            System.out.println("Sql statement executed!");

            //iterates over while loop, stores the values in an arraylist of strings
            while(rs.next()) {
                currentString = "";

                for (int i = 0; i < cols; i++) {
                    //TODO move temporary[i] up here.
                    if (colPattern[i] == 0) {
                        temporary[i] = ", " + rs.getInt(colName[i]);
                    } else if (colPattern[i] == 1) {
                        temporary[i] = ", " + rs.getString(colName[i]);
                    } else if (colPattern[i] == 2) {
                        temporary[i] = ", " + rs.getDouble(colName[i]);
                    }

                    //TODO maybe make this more efficient
                    for (int k = 0; k < temporary[i].length() - 1; k++) {
                        if (temporary[i].charAt(k) == ' ' && temporary[i].charAt(k + 1) == ' ') {
                            temporary[i] = temporary[i].substring(0, k);
                        }
                    }
                }

                temporary[0] = temporary[0].substring(1);

                for(int i = 0; i < temporary.length; i++) {
                    currentString += temporary[i];
                }
                results.add(currentString);
            }

            //use a single display function to display the strings. Pass in the arrayList
            displayResult(results);

            rs.close();
            stmt.close();
        } catch (SQLException e) {
            System.out.println(EXCEPTION_TAG + " " + e.getMessage());
        }
    }


    public void displayInvestors() {
        System.out.println("displayInvestors:");
        int cols = 2; //update cols for specific function

        String[] colNames = new String[cols];
        int[] colPattern = new int[cols];

        //write out sql column names we want
        colNames[0] = "investorID";
        colNames[1] = "name";

        //in the order above, write out sql column variable types as:
        // int = 0, String = 1
        colPattern[0] = 0;
        colPattern[1] = 1;

        displayMain(cols, "SELECT * FROM Investor", colNames, colPattern);

    }

    public void displayPrimaryInvestors() {
        System.out.println("displayPrimaryInvestors:");
        int cols = 2; //update cols for specific function

        String[] colNames = new String[cols];
        int[] colPattern = new int[cols];

        //write out sql column names we want
        colNames[0] = "investorID";
        colNames[1] = "equity";

        //in the order above, write out sql column variable types as:
        // int = 0, String = 1
        colPattern[0] = 0;
        colPattern[1] = 2;

        displayMain(cols, "SELECT * FROM PrimaryInvestor P, " +
                "MakesDirectInvestment M " +
                "WHERE M.investorID = P.investorID", colNames, colPattern);

    }


    //tkk
    //displays all investors who've used every currency
    //This one works too.
    public void displayAllCurrencies() {
        System.out.println("displayAllCurrencies");
        int cols = 2; //update cols for specific function

        String[] colNames = new String[cols];
        int[] colPattern = new int[cols];

        //write out sql column names we want
        colNames[0] = "investorID";
        colNames[1] = "name";

        //in the order above, write out sql column variable types as:
        // int = 0, String = 1
        colPattern[0] = 0;
        colPattern[1] = 1;

        System.out.println("Printing Current Statement");


        String first = "SELECT DISTINCT I.InvestorID, TEMP.name FROM Investor TEMP, SecondaryInvestor I, " +
                "MakesTransaction M, TransactionBasicData T ";
        String second = "WHERE TEMP.InvestorID = I.InvestorID AND I.InvestorID = M.InvestorID AND T.transactionID = M.transactionID ";
        String third = "AND NOT EXISTS (SELECT currencyName FROM Currency MINUS ";
        String fourth = "(SELECT currencyName FROM TransactionBasicData A, MakesTransaction B " +
                "WHERE A.transactionID = B.transactionID AND B.InvestorID = I.InvestorID))";

        String statement =  first + second + third + fourth;

        System.out.println(statement);


        displayMain(cols, statement, colNames, colPattern);
        displayInvestors();
        displayPrimaryInvestors();
    }



    //TODO do this one last. Basically I have to modify all the values here.
    //TODO put in a correct sql statement
    public void displayAvgNumStocks() {
        System.out.println("displayAvgNumStocks");
        int cols = 2; //TODO update cols for specific function

        String[] colNames = new String[cols];
        int[] colPattern = new int[cols];

        //TODO write out sql column names we want
        colNames[0] = "investorID";
        colNames[1] = "second";

        //TODO in the order above, write out sql column variable types as:
        // int = 0, String = 1
        colPattern[0] = 0;
        colPattern[1] = 2;

        String statement = "SELECT Temp.investorID, avg(Temp.total) " +
                "FROM (SELECT M.investorID, T.cid, sum(Q.quantity) " +
                "AS total FROM TransactionBasicData T, QuantityOfPurchase Q, " +
                "MakesTransaction M, SecondaryInvestor I WHERE T.transactionID = M.transactionID " +
                "AND T.transactionID = Q.transactionID AND M.investorID = I.investorID " +
                "GROUP BY I.investorID, T.cid ) AS Temp " +
                "GROUP BY Temp.investorID";

        String old = "SELECT InvestorID, avg(smt.temp) FROM SecondaryInvestor I, " +
                "(SELECT cid, Sum(P.quantity) AS temp " +
                "FROM TransactionBasicData T, QuantityOfPurchase Q, " +
                "MakesTransaction M WHERE T.transactionID = M.transactionID " +
                "AND T.transactionID = Q.transactionID AND " +
                "M.investorID = I.investorID) AS smt " +
                "GROUP BY InvestorID";

        displayMain(cols, statement, colNames, colPattern);
    }


    //TODO change how our normalization goes for the Transaction Purchase Price


    //TODO fix the sql
    public void displayCompanyInvestments(int cid, double min) {
        System.out.println("displayCompanyInvestments");
        int cols = 3; // update cols for specific function

        String[] colNames = new String[cols];
        int[] colPattern = new int[cols];

        // write out sql column names we want
        colNames[0] = "cid";
        colNames[1] = "cName";
        colNames[2] = "Sum(TransactionPrice)";

        // in the order above, write out sql column variable types as:
        // int = 0, String = 1
        colPattern[0] = 0;
        colPattern[1] = 1;
        colPattern[2] = 2;

        String statement = "SELECT C.cid, C.cName, Sum(TransactionPrice) " +
                "FROM Company_name C, TransactionBasicData TBD, QuantityOfPurchase Q, " +
                "PriceOfIndividualStock PO, PurchasePrice P " +
                "WHERE TBD.transactionID = Q.transactionID AND PO.transactionID = Q.transactionID AND " +
                "P.Quantity = Q.Quantity AND P.SharePrice = PO.SharePrice AND C.cid = TBD.cid " +
                "GROUP BY C.cid, C.cName HAVING Sum(TransactionPrice) > " + min + "";

        displayMain(cols, statement, colNames, colPattern);

    }



    //tkk
    //THIS ONE WORKS TOO YES YES YES
    public void displayTopInvestors(int cid, double min) {
        System.out.println("displayTopInvestors");
        int cols = 4; //update cols for specific function

        String[] colNames = new String[cols];
        int[] colPattern = new int[cols];

        //write out sql column names we want
        colNames[0] = "investorID";
        colNames[1] = "name";
        colNames[2] = "Equity";
        colNames[3] = "AmountInvested";

        //in the same order as above, write out sql column variable types as:
        // int = 0, String = 1, Numeric = 3
        colPattern[0] = 0;
        colPattern[1] = 1;
        colPattern[2] = 2;
        colPattern[3] = 2;

        //SELECT InvestorID, InvestorName, Equity, AmountInvested FROM MakesDirectInvestment WHERE CompanyID = userInput x AND Equity% > y
        displayMain(cols,
                "SELECT M.InvestorID, name, M.Equity, M.AmountInvested " +
                        "FROM MakesDirectInvestment M, Investor I " +
                        "WHERE M.investorID = I.investorID AND M.cid = " + cid +
                        " AND Equity > " + min + ""
                , colNames, colPattern);

    }

    //tkk
    //YEAH BOY
    public void displayAvgStockPriceOfCompany(int cid) {
        System.out.println("displayAvgStockPriceOfCompany");
        int cols = 3; // update cols for specific function

        int[] colDigits = new int[cols];
        int[] colPattern = new int[cols];

        // write out sql column names we want
        colDigits[0] = 1;
        colDigits[1] = 2;
        colDigits[2] = 3;

        // in the order above, write out sql column variable types as:
        // int = 0, String = 1
        colPattern[0] = 1;
        colPattern[1] = 0;
        colPattern[2] = 2;


        String statement = "SELECT C.cName, C.cid, avg(P.SharePrice) " +
                "FROM Company_Name C, FinancialInstrument F, TransactionBasicData T, PriceOfIndividualStock P " +
                "WHERE F.type = 'stock' AND C.cid = F.cid AND F.cid = T.cid AND " +
                "T.transactionID = P.transactionID AND C.cid = " + cid + " GROUP BY C.cName, C.cid";

        displayMain(cols, statement, colDigits, colPattern);
    }



    //YEAHHHHHH this one works too lets get it smh.
    public void displayInvestorsFromInstitution(String name) {
        System.out.println("displayInvestorsFromInstitution");
        int cols = 2; // update cols for specific function

        String[] colNames = new String[cols];
        int[] colPattern = new int[cols];

        // write out sql column names we want
        colNames[0] = "institutionName";
        colNames[1] = "investorID";

        // in the order above, write out sql column variable types as:
        // int = 0, String = 1
        colPattern[0] = 1;
        colPattern[1] = 0;

        String statement = "SELECT E.institutionName, I.InvestorID " +
                "FROM Investor I, Education E, Institution N " +
                "WHERE I.investorID = E.investorID AND E.institutionName = N.institutionName " +
                "AND N.institutionName = '" + name + "'";

        displayMain(cols, statement, colNames, colPattern);
    }

    //This one works too I Think
    public void displayInvestorsOfInvestingGroup(String name) {
        System.out.println("displayInvestorsOfInvestingGroup");
        int cols = 1; //update cols for specific function

        String[] colNames = new String[cols];
        int[] colPattern = new int[cols];

        // write out sql column names we want
        colNames[0] = "investorID";

        // in the order above, write out sql column variable types as:
        // int = 0, String = 1
        colPattern[0] = 0;

        String statement = "SELECT I.investorID " +
                "FROM InvestingGroup G, InvestorJoinsInvestingGroup IG, Investor I " +
                "WHERE I.investorID = IG.investorID AND G.igName = IG.igName " +
                "AND G.igName = '" + name + "'"; //sus single quotes.

        displayMain(cols, statement, colNames, colPattern);
    }
}
