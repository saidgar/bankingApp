package banking;

import java.util.*;

public class Main {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        Database database = new Database();
        database.createDatabase(args[1]);
        UserInterface userInterface = new UserInterface(scanner, database);
        userInterface.start();
    }
}