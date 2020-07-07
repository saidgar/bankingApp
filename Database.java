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

    private static boolean luhnAlgorithmCheck(ArrayList<Integer> cardNumbers) {
        ArrayList<Integer> checkNumber = new ArrayList<>();
        int checkDigit = 0;
        for (int i = 0; i < cardNumbers.size(); i++) {
            if (i % 2 == 0) {
                checkNumber.add(cardNumbers.get(i) * 2);
            } else {
                checkNumber.add(cardNumbers.get(i));
            }
            if (checkNumber.get(i) > 9) {
                checkNumber.set(i, checkNumber.get(i) - 9);
            }
            checkDigit += checkNumber.get(i);
        }
        return cardNumbers.get(cardNumbers.size() - 1) == (checkDigit * 9) % 10;
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
        String sql = "CREATE TABLE card (id INTEGER PRIMARY KEY, \n" +
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

    void insertIntoTable(String number, String pin) {
        String sql = "INSERT INTO card (number, pin) VALUES(?,?)";
        try (Connection conn = DriverManager.getConnection(this.dbURL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, number);
            pstmt.setString(2, pin);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

    }

    int getBalance(String cardNumber, String cardPin) {
        String sql = "SELECT balance from card WHERE number = ? AND pin = ?";
        try (Connection conn = DriverManager.getConnection(this.dbURL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, cardNumber);
            pstmt.setString(2, cardPin);
            ResultSet resultSet = pstmt.executeQuery();
            return resultSet.getInt("balance");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return 0;
        }
    }

    boolean closeAccount(String cardNumber, String cardPin) {
        String sql = "DELETE FROM card WHERE number = ? AND pin = ?";
        try (Connection conn = DriverManager.getConnection(this.dbURL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, cardNumber);
            pstmt.setString(2, cardPin);
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return false;
        }
    }

    boolean addIncome(String cardNumber, int amount) {
        String sql = "UPDATE card SET balance = balance + ? WHERE number = ?";
        try (Connection conn = DriverManager.getConnection(this.dbURL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, amount);
            pstmt.setString(2, cardNumber);
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return false;
        }
    }

    boolean removeIncome(String cardNumber, int amount) {
        String sql = "UPDATE card SET balance = balance - ? WHERE number = ?";
        try (Connection conn = DriverManager.getConnection(this.dbURL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, amount);
            pstmt.setString(2, cardNumber);
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return false;
        }
    }

    boolean checkIfCardExists(String cardNumber) {
        String sql = "SELECT * FROM card WHERE cardname = ?";
        try (Connection conn = DriverManager.getConnection(this.dbURL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, cardNumber);
            ResultSet resultSet = pstmt.executeQuery();
            if (resultSet.next()) {
                return true;
            }
            return false;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return false;
        }
    }

    boolean checkTransfer(String cardNumber, String cardPin, String destination) {
        if (destination.equals(cardNumber)) {
            System.out.println("You can't transfer money to the same account!");
            return false;
        }
        ArrayList<Integer> checkNumber = new ArrayList<>();
        for (int i = 0; i < destination.length(); i++) {
            checkNumber.add(Integer.valueOf(destination.charAt(i)));
        }
        if (!luhnAlgorithmCheck(checkNumber)) {
            if (checkIfCardExists(cardNumber)) {
                return true;
            }
        }
        return false;
    }

    boolean transfer(String cardNumber, String cardPin, int amount, String destination) {
        if (amount > getBalance(cardNumber, cardPin)) {
            System.out.println("Not enough money!");
            return false;
        } else {
            return  addIncome(destination, amount) && removeIncome(cardNumber, amount);
        }
    }
}
