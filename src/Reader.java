package src;

import java.util.Scanner;

public class Reader extends BookManagement {

    Reader(Scanner sc) {
        super(sc);
    }

    public void readerMenu() {
        System.out.println("\t<<<<<<<<<<<<<<<<<<<<<< Menu >>>>>>>>>>>>>>>>>>>>>>>>\t\n");

        int choice;
        do {
            System.out.println("1. Search a book");
            System.out.println("2. View all books");
            System.out.println("3. Place order");
            System.out.println("4. Return a book");
            System.out.println("To exit, press 0\n");
            System.out.print("Enter your choice : ");
            try {
                String input = sc.nextLine();
                choice = Integer.parseInt(input);
                switch (choice) {
                    case 0 -> System.out.println("Exiting Menu");
                    case 1 -> searchBook();
                    case 2 -> viewAllBooks();
                    case 3 -> placeOrder();
                    case 4 -> returnBook();
                    default -> System.out.println("Invalid choice");
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a number.");
                choice = -1;
            }
        } while (choice != 0);
    }
}
