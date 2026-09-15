package onlineshop.cli;

import lombok.NonNull;

import java.util.InputMismatchException;
import java.util.Scanner;

public class DataReader {
    private final Scanner sc = new Scanner(System.in);
    @NonNull
    private final ConsolePrinter printer;

    public DataReader(ConsolePrinter printer) {
        this.printer = printer;
    }

    public String readLine(String message) {
        printer.print(message);
        return sc.nextLine().trim();
    }

    public int readInt(String message) {
        while (true) {
            printer.print(message);

            try {
                return Integer.parseInt(sc.nextLine());
            } catch (NumberFormatException e) {
                printer.print("Enter a valid number");
            }
        }
    }

    public int getOptionInt() {
        while (true) {
            try {
                printer.print("Enter option number: ");
                return sc.nextInt();
            } catch (InputMismatchException | IllegalArgumentException e) {
                printer.print("Enter a valid number");
            } finally {
                sc.nextLine();
            }
        }
    }

    public int getIntFromUser() {
        while (true) {
            try {
                int userChoice = sc.nextInt();
                sc.nextLine();
                return userChoice;
            } catch (InputMismatchException e) {
                System.err.println("Enter a valid number");
                sc.nextLine();
            }
        }
    }

    public String getStringFromUser() {
        return sc.nextLine();
    }
}
