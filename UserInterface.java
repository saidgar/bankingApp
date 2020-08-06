package banking;

import org.jetbrains.annotations.NotNull;

import java.util.Scanner;

public class UserInterface {
    private final Scanner scanner;
    private final Database database;

    public UserInterface(Scanner scanner, Database database) {
        this.scanner = scanner;
        this.database = database;
    }
    public void start() {
        boolean exit = false;
        while (!exit) {
            boolean logOut = false;
            System.out.println("1. Create an account" +
                    "\n2. Log into account" +
                    "\n0. Exit");
            String input = scanner.nextLine();
            switch (input) {
                case "1":
                    createAccount(database);
                    break;
                case "2":
                    CreditCard toCompare = inputCreditCard();
                    if (database.checkLogin(toCompare)) {
                        System.out.println("You have successfully logged in!");
                        while (!logOut) {
                            System.out.println("1. Balance" +
                                    "\n2. Add income" +
                                    "\n3. Do transfer" +
                                    "\n4. Close account" +
                                    "\n5. Log out" +
                                    "\n0. Exit");
                            String loggedInput = scanner.nextLine();
                            switch (loggedInput) {
                                case "1":
                                    getBalance(database, toCompare);
                                    break;
                                case "2":
                                    addMoney(scanner, database, toCompare);
                                    break;
                                case "3":
                                    transferMoney(scanner, database, toCompare);
                                    break;
                                case "4":
                                    closeAccount(database, toCompare);
                                    logOut = true;
                                    break;
                                case "5":
                                    logOut = logOut();
                                    break;
                                case "0":
                                    exit = true;
                                    logOut = true;
                                    break;
                                default:
                                    System.out.println("Unknown command!");
                                    break;
                            }
                        }
                    } else {
                        System.out.println("Incorrect login information");
                    }
                    break;
                case "0":
                    exit = true;
                    break;
                default:
                    System.out.println("Unknown command!");
                    break;
            }
        }
    }

    @NotNull
    private CreditCard inputCreditCard() {
        System.out.println("Enter your card number:");
        String cardNumberInput = scanner.nextLine();
        System.out.println("Enter your PIN:");
        String cardPINInput = scanner.nextLine();
        return new CreditCard(cardNumberInput, cardPINInput);
    }

    private static void createAccount(Database database) {
        CreditCard created = new CreditCard();
        if (database.createAccount(created)) {
            System.out.println("Your card has been created\n" + "Your card number:");
            System.out.println(created.getNumber());
            System.out.println("Your card PIN:");
            System.out.println(created.getPin());
        }
    }

    private static void getBalance(Database database, CreditCard toCompare) {
        System.out.println("Balance: " + database.getBalance(toCompare));
    }

    private static boolean logOut() {
        System.out.println("You have successfully logged out!");
        return true;
    }

    private static void closeAccount(Database database, CreditCard toCompare) {
        if (database.closeAccount(toCompare)) {
            System.out.println("The account has been closed!");
        }
    }

    private static void addMoney(Scanner scanner, Database database, CreditCard toCompare) {
        System.out.println("Enter income:");
        if (database.addIncome(toCompare.getNumber(), Integer.valueOf(scanner.nextLine()))) {
            System.out.println();
            System.out.println("Income was added!");
        }
    }

    private static void transferMoney(Scanner scanner, Database database, CreditCard toCompare) {
        System.out.println("Transfer" +
                "\nEnter card number:");
        String cardDestination = scanner.nextLine();
        if (database.checkTransfer(toCompare, cardDestination)) {
            System.out.println("Enter how much money you want to transfer:");
            if (database.transfer(toCompare, Integer.valueOf(scanner.nextLine()), cardDestination)) {
                System.out.println();
                System.out.println("Success!");
            }
        }
    }
}
