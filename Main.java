package banking;

import java.awt.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;


public class Main {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        ArrayList<CreditCard> creditCards = new ArrayList<>();
        ArrayList<Integer> numberContainer = new ArrayList<>();
        Database database = new Database();
        database.createDatabase(args[1]);
        boolean exit = false;
        while (!exit) {
            boolean logOut = false;
            System.out.println("1. Create an account\n" +
                    "2. Log into account\n" +
                    "0. Exit\n");
            String input = scanner.nextLine();
            switch (input) {
                case "1":
                    CreditCard created = new CreditCard();
                    if (creditCards.add(created)) {
                        database.insertIntoTable(created.getNumber(), created.getPin());
                        System.out.println("Your card has been created\n" + "Your card number:");
                        System.out.println(created.getNumber());
                        System.out.println("Your card PIN:");
                        System.out.println(created.getPin());
                    }
                    break;
                case "2":
                    System.out.println("Enter your card number:");
                    String cardNumberInput = scanner.nextLine();
                    System.out.println("Enter your PIN:");
                    String cardPINInput = scanner.nextLine();
                    CreditCard toCompare = new CreditCard(cardNumberInput, cardPINInput);
                    if (creditCards.contains(toCompare)) {
                        System.out.println("You have successfully logged in!");
                        while (!logOut) {
                            System.out.println("1. Balance\n2. Add income\n3. Do transfer\n4. Close account\n5. Log out\n0.Exit");
                            String loggedInput = scanner.nextLine();
                            switch (loggedInput) {
                                case "1":
                                    System.out.println("Balance: " + database.getBalance(toCompare.getNumber(), toCompare.getPin()));
                                    break;
                                case "2":
                                    if (database.addIncome(toCompare.getNumber(), Integer.valueOf(scanner.nextLine()))) {
                                        System.out.println();
                                        System.out.println("Income was added!");
                                    }
                                    break;
                                case "3":
                                    System.out.println("Transfer");
                                    System.out.println("Enter card number:");
                                    String cardDestination = scanner.nextLine();
                                    if (database.checkTransfer(toCompare.getNumber(), toCompare.getPin(), cardDestination)) {
                                        System.out.println("Enter how much money you want to transfer:");
                                        if (database.transfer(toCompare.getNumber(), toCompare.getPin(), Integer.valueOf(scanner.nextLine()), cardDestination)) {
                                            System.out.println();
                                            System.out.println("Success!");
                                        }
                                    }
                                    break;
                                case "4":
                                    if (database.closeAccount(toCompare.getNumber(), toCompare.getPin())) {
                                        System.out.println("The account has been closed!");
                                    }
                                    break;
                                case "5":
                                    logOut = true;
                                    System.out.println("You have successfully logged out!");
                                    break;
                                case "0":
                                    exit = true;
                                    logOut = true;
                                    break;
                            }
                        }
                    }
                    break;
                case "0":
                    exit = true;
                    break;
            }
        }
    }
}