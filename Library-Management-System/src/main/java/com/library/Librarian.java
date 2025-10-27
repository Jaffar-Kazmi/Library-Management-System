package com.library;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Librarian extends BookManagement {

        private final String username;
        private final String password;

        Librarian(String username, String password, BufferedReader read, Scanner sc) {
            super(read, sc);
            this.username = username;
            this.password = password;
        }

        public boolean login() {
            System.out.println("\t<<<<<<<<<<<<<<<<<<<<<< LogIn >>>>>>>>>>>>>>>>>>>>>>>>\t\n");

            boolean successful = false;
            for (int i = 1; i <= 3; i++) {
                try {
                    System.out.print("Enter your username : ");
                    String enteredName = read.readLine();
                    System.out.print("Enter your password : ");
                    String pass = read.readLine();
                    if (pass.equals(password) && enteredName.equals(username)) {
                        System.out.println("LogIn Successful");
                        successful = true;
                        break;
                    } else {
                        System.out.println("Incorrect username or password.");
                        System.out.println("Number of tries remaining : " + (3 - i));
                    }
                } catch (IOException ex) {
                    Logger.getLogger(Librarian.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            return successful;
        }

        public void librarianMenu() throws IOException {
            System.out.println("\t<<<<<<<<<<<<<<<<<<<<<< Menu >>>>>>>>>>>>>>>>>>>>>>>>\t\n");

            int choice;
            do {
                System.out.println("1. Add book");
                System.out.println("2. Search a book");
                System.out.println("3. Update book");
                System.out.println("4. Delete book");
                System.out.println("5. Issue a book");
                System.out.println("6. View all books");
                System.out.println("7. View issued Books");
                System.out.println("8. View orders");
                System.out.println("To exit, press 0\n");
                System.out.print("Enter your choice : ");
                try{
                    String input = sc.nextLine();
                    choice = Integer.parseInt(input);
                    switch (choice) {
                        case 0 ->   System.out.println("Exiting Menu");
                        case 1 ->   addBook();
                        case 2 ->   searchBook();
                        case 3 ->   updateBook();
                        case 4 ->   deleteBook();
                        case 5 ->   issueBook();
                        case 6 ->   viewAllBooks();
                        case 7 ->   viewIssuedBooks();
                        case 8 ->   viewOrders();
                        default ->  System.out.println("Invalid choice");
                    }
                } catch(NumberFormatException e){
                    System.out.println("Invalid input. Please enter a number.");
                    choice = -1;
                }catch (IOException e) {
                    System.out.println("An error occurred while reading input.");
                    choice = -1;
                }
            } while (choice != 0);
        }
}
