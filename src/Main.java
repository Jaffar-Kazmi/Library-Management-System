package src;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) throws IOException {
        System.out.println("\t<<<<<<<<<<<<< Library Management System >>>>>>>>>>>>>\t\n");
        Scanner sc = new Scanner(System.in);
        BufferedReader read = new BufferedReader(new InputStreamReader(System.in));

        Librarian librarian = new Librarian("admin", "admin@123", read, sc);
        Reader reader = new Reader(read, sc);
        int choice;
        do {
            System.out.println("1. Librarian");
            System.out.println("2. Reader");
            System.out.println("To exit, press 0");
            System.out.print("Enter your choice : ");
            try{
            String input = sc.nextLine();
            choice = Integer.parseInt(input);
            switch (choice) {
                case 0 ->
                    System.out.println("Exiting Menu");
                case 1 -> {
                    if (librarian.login()) {
                        librarian.librarianMenu();
                    } else {
                        System.out.println("Exiting the program");
                    }
                }
                case 2 -> {
                    reader.readerMenu();
                }
                default ->
                    System.out.println("Invalid choice");
            }
            }catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a number.");
                choice = -1; 
                } catch (IOException e) {
                System.out.println("An error occurred while reading input.");
                choice = -1; 
            }
        } while (choice != 0);
        
    }

}





