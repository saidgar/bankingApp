package banking;

import java.sql.*;
import java.util.ArrayList;


public class Database {
    private String dbName;
    private String dbURL;

    private void setDb(String dbName) {
        this.dbName = dbName;
        this.dbURL = "jdbc:sqlite:.\\" + dbName;
    }

    void createDatabase(String dbName) {
        String url = "jdbc:sqlite:.\\" + dbName;
        try (Connection conn = DriverManager.getConnection(url)) {
            if (conn != null) {
                setDb(dbName);
                this.createTable();
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    private void createTable() {
        String sql = "CREATE TABLE IF NOT EXISTS card (id INTEGER PRIMARY KEY, \n" +
                "number TEXT,\n" +
                "pin TEXT,\n" +
                "balance INTEGER DEFAULT 0\n" +
                ");";
        try (Connection conn = DriverManager.getConnection(this.dbURL);
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

    }

    boolean createAccount(CreditCard creditCard) {
        String sql = "INSERT INTO card (number, pin) VALUES(?,?)";
        try (Connection conn = DriverManager.getConnection(this.dbURL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            prepareStatement(creditCard.getNumber(), creditCard.getPin(), pstmt);
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return false;
        }

    }

    int getBalance(CreditCard creditCard) {
        String sql = "SELECT balance from card WHERE number = ? AND pin = ?";
        try (Connection conn = DriverManager.getConnection(this.dbURL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            prepareStatement(creditCard.getNumber(), creditCard.getPin(), pstmt);
            ResultSet resultSet = pstmt.executeQuery();
            return resultSet.getInt("balance");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return 0;
        }
    }

    boolean closeAccount(CreditCard creditCard) {
        String sql = "DELETE FROM card WHERE number = ? AND pin = ?";
        try (Connection conn = DriverManager.getConnection(this.dbURL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            prepareStatement(creditCard.getNumber(), creditCard.getPin(), pstmt);
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return false;
        }
    }

    private void prepareStatement(String cardNumber, String cardPin, PreparedStatement pstmt) throws SQLException {
        pstmt.setString(1, cardNumber);
        pstmt.setString(2, cardPin);
    }

    boolean addIncome(String cardNumber, int amount) {
        if (amount <= 0) {
            return false;
        }
        String sql = "UPDATE card SET balance = balance + ? WHERE number = ?";
        return balanceOperation(cardNumber, amount, sql);
    }

    private boolean balanceOperation(String cardNumber, int amount, String sql) {
        try (Connection conn = DriverManager.getConnection(this.dbURL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            prepareStatement(cardNumber, amount, pstmt);
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return false;
        }
    }

    private void prepareStatement(String cardNumber, int amount, PreparedStatement pstmt) throws SQLException {
        pstmt.setInt(1, amount);
        pstmt.setString(2, cardNumber);
    }

    boolean removeIncome(String cardNumber, int amount) {
        String sql = "UPDATE card SET balance = balance - ? WHERE number = ?";
        return balanceOperation(cardNumber, amount, sql);
    }

    boolean checkIfCardExists(String cardNumber) {
        String sql = "SELECT * FROM card WHERE number = ?";
        try (Connection conn = DriverManager.getConnection(this.dbURL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, cardNumber);
            ResultSet resultSet = pstmt.executeQuery();
            return resultSet.next();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return false;
        }
    }

    boolean checkLogin(CreditCard creditCard) {
        String sql = "SELECT * FROM card WHERE number = ? AND pin = ?";
        try (Connection conn = DriverManager.getConnection(this.dbURL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            prepareStatement(creditCard.getNumber(), creditCard.getPin(), pstmt);
            ResultSet resultSet = pstmt.executeQuery();
            return resultSet.next();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return false;
        }
    }

    boolean checkTransfer(CreditCard creditCard, String destination) {
        if (destination.equals(creditCard.getNumber())) {
            System.out.println("You can't transfer money to the same account!");
            return false;
        }
        ArrayList<Integer> checkNumber = new ArrayList<>();
        for (int i = 0; i < destination.length(); i++) {
            checkNumber.add(Character.getNumericValue(destination.charAt(i)));
        }
        if (checkNumber.size() == 16) {
            if (creditCard.luhnAlgorithmCheck(checkNumber)) {
                return checkIfCardExists(creditCard.getNumber());
            } else {
                System.out.println("Card number is incorrect");
            }
        } else {
            System.out.println("Card number is too short");
        }
        return false;
    }

    boolean transfer(CreditCard creditCard, int amount, String destination) {
        if (amount > getBalance(creditCard)) {
            System.out.println("Not enough money!");
            return false;
        } else {
            return addIncome(destination, amount) && removeIncome(creditCard.getNumber(), amount);
        }
    }
}
