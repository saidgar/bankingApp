package banking;

import java.util.ArrayList;
import java.util.Random;

public class CreditCard {
    private String number;
    private String pin;

    public CreditCard() {
        ArrayList<Integer> cardNumbers = Generate(9);
        cardNumbers.add(luhnAlgorithmCreate(cardNumbers));
        this.number = ConcatString("400000", cardNumbers);
        this.pin = ConcatString("", Generate(4));
    }

    public CreditCard(String cardNumber, String cardPin) {
        this.number = cardNumber;
        this.pin = cardPin;
    }

    private static int luhnAlgorithmCreate(ArrayList<Integer> cardNumbers) {
        ArrayList<Integer> checkNumber = new ArrayList<>();
        int checkDigit = 8;
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
        return (checkDigit * 9) % 10;
    }

    private static ArrayList<Integer> Generate(int limit) {
        ArrayList<Integer> number = new ArrayList<>();
        Random random = new Random();
        for (int i = 0; i < limit; i++) {
            number.add(random.nextInt(10));
        }
        return number;
    }

    private static String ConcatString(String input, ArrayList<Integer> numbers) {
        for (int i = 0; i < numbers.size(); i++) {
            input += String.valueOf(numbers.get(i));
        }
        return input;
    }

    public String getNumber() {
        return this.number;
    }

    public String getPin() {
        return this.pin;
    }

    public boolean equals(Object object) {
        if (object == this) {
            return true;
        }
        if (!(object instanceof CreditCard)) {
            return false;
        }
        CreditCard compared = (CreditCard) object;
        if (this.number.equals(compared.getNumber()) && this.pin.equals(compared.getPin())) {
            return true;
        }
        return false;
    }
}
