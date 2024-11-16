/*Library Management System
  3rd Semester
  OOP Project
  author: syed jaffar raza kazmi
 */

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Scanner;
import java.util.ArrayList;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class LibraryManagementSystem {

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
            System.out.println("3. Exit");
            System.out.print("\nEnter your choice : ");
            try {
                String input = sc.nextLine();
                choice = Integer.parseInt(input);
                switch (choice) {
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
                    case 3 -> {
                        System.out.println("Thanks");
                        System.exit(0);
                    }
                    default ->
                        System.out.println("Invalid choice");
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a number.");
                choice = -1;
            } catch (IOException e) {
                System.out.println("An error occurred while reading input.");
                choice = -1;
            }
        } while (choice != 0);

    }

}

class Book {

    String isbn, name, author;
    LocalDate publishDate;

    Book(String isbn, String name, String author, LocalDate publishDate) {
        this.isbn = isbn;
        this.name = name;
        this.author = author;
        this.publishDate = publishDate;
    }

    void setISBN(String isbn) {
        this.isbn = isbn;
    }

    String getISBN() {
        return isbn;
    }

    void setName(String name) {
        this.name = name;
    }

    String getName() {
        return name;
    }

    void setAuthor(String author) {
        this.author = author;
    }

    String getAuthor() {
        return author;
    }

    void setPublishDate(LocalDate publishDate) {
        this.publishDate = publishDate;
    }

    LocalDate getPublishDate() {
        return publishDate;
    }

    @Override
    public String toString() {
        return "| ISBN : " + isbn + " | Name : " + name + " | Author : " + author + " | Publish Date : " + publishDate
                + " |";
    }
}

class Order {

    Book book;
    String orderedBy;
    LocalDate orderedOn;

    Order(Book book, String orderedBy, LocalDate orderedOn) {
        this.book = book;
        this.orderedBy = orderedBy;
        this.orderedOn = orderedOn;
    }

    @Override
    public String toString() {
        return book + " | Ordered By " + orderedBy + " | on " + orderedOn + " |";
    }
}

class IssuedBook {

    Book book;
    String issuedTo;
    LocalDate dueDate;

    IssuedBook(Book book, String issuedTo, LocalDate dueDate) {
        this.book = book;
        this.issuedTo = issuedTo;
        this.dueDate = dueDate;
    }

    @Override
    public String toString() {
        return book + " | Issued to " + issuedTo + " | Due Date " + dueDate + " |";
    }
}

class BookManagement {

    BufferedReader read;
    Scanner sc;

    ArrayList<Book> book = new ArrayList<>();
    ArrayList<Order> order = new ArrayList<>();
    ArrayList<IssuedBook> issuedBook = new ArrayList<>();

    File bookFile = new File("books.txt");
    File orderFile = new File("orders.txt");
    File issuedBookFile = new File("issuedbooks.txt");

    BookManagement(BufferedReader read, Scanner sc) {
        this.read = read;
        this.sc = sc;
    }

    private void saveBooksToFile(ArrayList<Book> book) {
        BufferedWriter writer = null;
        try {
            writer = new BufferedWriter(new FileWriter(bookFile, false));
            for (Book b : book) {
                writer.write(b.isbn + " , " + b.name + " , " + b.author + " , " + b.publishDate);
                writer.newLine();
            }
        } catch (IOException ex) {
            Logger.getLogger(BookManagement.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                writer.close();
            } catch (IOException ex) {
                Logger.getLogger(BookManagement.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    private ArrayList<Book> readBooksFromFile() {
        ArrayList<Book> book = new ArrayList<>();
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(bookFile));
            String line;
            try {
                while ((line = reader.readLine()) != null) {
                    String[] parts = line.split(" , ");
                    if (parts.length == 4) {
                        String isbn = parts[0];
                        String name = parts[1];
                        String author = parts[2];
                        String date = parts[3];
                        LocalDate publishDate = (LocalDate.parse(date, DateTimeFormatter.ofPattern("yyyy-MM-dd")));
                        book.add(new Book(isbn, name, author, publishDate));
                    }

                }
            } catch (IOException ex) {
                Logger.getLogger(BookManagement.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(BookManagement.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                reader.close();
            } catch (IOException ex) {
                Logger.getLogger(BookManagement.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return book;
    }

    private void saveOrdersToFile(ArrayList<Order> order) {
        BufferedWriter writer = null;
        try {
            writer = new BufferedWriter(new FileWriter(orderFile));
            for (Order o : order) {
                writer.write(o.book.isbn + " | " + o.book.name + " | " + o.book.author + " | " + o.book.publishDate
                        + " | " + o.orderedBy + " | " + o.orderedOn);
                writer.newLine();
            }
        } catch (IOException ex) {
            Logger.getLogger(BookManagement.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                writer.close();
            } catch (IOException ex) {
                Logger.getLogger(BookManagement.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    private ArrayList<Order> readOrdersFromFile() {
        ArrayList<Order> order = new ArrayList<>();
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(orderFile));
            String line;
            try {
                while ((line = reader.readLine()) != null) {
                    String[] parts = line.split(" \\| ");
                    if (parts.length == 6) {
                        String isbn = parts[0];
                        String name = parts[1];
                        String author = parts[2];
                        String pubDate = parts[3];
                        LocalDate publishDate = (LocalDate.parse(pubDate, DateTimeFormatter.ofPattern("yyyy-MM-dd")));
                        Book xbook = new Book(isbn, name, author, publishDate);
                        String orderedBy = parts[4];
                        String orderedOn = parts[5];
                        LocalDate orderDate = (LocalDate.parse(orderedOn, DateTimeFormatter.ofPattern("yyyy-MM-dd")));
                        order.add(new Order(xbook, orderedBy, orderDate));

                    }
                }
            } catch (IOException ex) {
                Logger.getLogger(BookManagement.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(BookManagement.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                reader.close();
            } catch (IOException ex) {
                Logger.getLogger(BookManagement.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return order;
    }

    private void saveIssuedBooksToFile(ArrayList<IssuedBook> issuedBook) {
        BufferedWriter writer = null;
        try {
            writer = new BufferedWriter(new FileWriter(issuedBookFile));
            for (IssuedBook ib : issuedBook) {
                writer.write(ib.book.isbn + " | " + ib.book.name + " | " + ib.book.author + " | " + ib.book.publishDate
                        + " | " + ib.issuedTo + " | " + ib.dueDate);
                writer.newLine();
            }
        } catch (IOException ex) {
            Logger.getLogger(BookManagement.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                writer.close();
            } catch (IOException ex) {
                Logger.getLogger(BookManagement.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    private ArrayList<IssuedBook> readIssuedBooksFromFile() {
        ArrayList<IssuedBook> issuedBook = new ArrayList<>();
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(issuedBookFile));
            String line;
            try {
                while ((line = reader.readLine()) != null) {
                    String[] parts = line.split(" \\| ");
                    if (parts.length == 6) {
                        String isbn = parts[0];
                        String name = parts[1];
                        String author = parts[2];
                        String pubDate = parts[3];
                        LocalDate publishDate = LocalDate.parse(pubDate, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                        Book book = new Book(isbn, name, author, publishDate);
                        String issuedTo = parts[4];
                        String date = parts[5];
                        LocalDate dueDate = LocalDate.parse(date, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                        issuedBook.add(new IssuedBook(book, issuedTo, dueDate));
                    }
                }
            } catch (IOException ex) {
                Logger.getLogger(BookManagement.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(BookManagement.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                reader.close();
            } catch (IOException ex) {
                Logger.getLogger(BookManagement.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return issuedBook;
    }

    void addBook() {
        try {
            System.out.print("\nEnter ISBN : ");
            String isbn = read.readLine();
            System.out.print("Enter name : ");
            String name = read.readLine();
            System.out.print("Enter Author's name : ");
            String author = read.readLine();
            boolean validDate = false;
            LocalDate publishDate = null;
            while (!validDate) {
                System.out.print("Enter publish date(yyyy-mm-dd) : ");
                String date = read.readLine();
                try {
                    publishDate = (LocalDate.parse(date, DateTimeFormatter.ofPattern("yyyy-MM-dd")));
                    validDate = true;
                } catch (DateTimeParseException e) {
                    System.out.println("Invalid date format. Please enter the date in yyyy-mm-dd format.");
                }
            }
            boolean isUnique = book.stream().noneMatch(bk -> bk.getISBN().equals(isbn));
            if (isUnique) {
                book.add(new Book(isbn, name, author, publishDate));
                saveBooksToFile(book);
                System.out.println("Book added successfully.\n");
            } else {
                System.out.println("Book with same ISBN already exists.");
            }
        } catch (IOException ex) {
            Logger.getLogger(BookManagement.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("An I/O error occurred. Please try again.");
        } catch (Exception ex) {
            Logger.getLogger(BookManagement.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("An unexpected error occurred. Please try again.");
        }
    }

    void searchBook() {
        book = readBooksFromFile();
        if (book.isEmpty()) {
            System.out.println("\nNo Books Available.");
            return;
        }
        try {
            boolean found = false;
            System.out.print("\nEnter ISBN of the book you want to search : ");
            String isbn = read.readLine();
            for (Book b : book) {
                if (b.getISBN().equals(isbn)) {
                    System.out.println("\nDetails of the book are as under : ");
                    System.out.println(b);
                    System.out.print("\n");
                    found = true;
                    break;
                }
            }
            if (!found) {
                System.out.println("Book not found\n");
            }
        } catch (IOException ex) {
            Logger.getLogger(BookManagement.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("An I/O error occurred. Please try again.");
        }
    }

    void updateBook() throws IOException {
        book = readBooksFromFile();
        if (book.isEmpty()) {
            System.out.println("\nNo Books Available.");
            return;
        }

        System.out.print("Enter ISBN of the book you want to update : ");
        String isbn = read.readLine();
        int choice;
        boolean found = false;

        for (Book b : book) {
            if (b.getISBN().equals(isbn)) {
                do {
                    System.out.println("To update isbn, press 1.");
                    System.out.println("To update Name, press 2.");
                    System.out.println("To update Author, press 3.");
                    System.out.println("To update Publish Date, press 4.");
                    System.out.println("To exit, press 0.");
                    choice = sc.nextInt();
                    switch (choice) {
                        case 1 -> {
                            System.out.print("Enter new isbn : ");
                            String newIsbn = read.readLine();
                            boolean isUnique = book.stream().noneMatch(bk -> bk.getISBN().equals(newIsbn));
                            if (isUnique) {
                                b.setISBN(newIsbn);
                                System.out.println("Isbn updated succesfully.");
                            } else {
                                System.out.println("Book with same ISBN already exists.");
                            }
                        }
                        case 2 -> {
                            System.out.print("Enter new name : ");
                            b.setName(read.readLine());
                            System.out.println("Name updated succesfully.");
                        }
                        case 3 -> {
                            System.out.print("Enter new Author : ");
                            b.setAuthor(read.readLine());
                            System.out.println("Author updated succesfully.");
                        }
                        case 4 -> {
                            boolean validDate = false;
                            while (!validDate) {
                                System.out.print("Enter new Publishing Date (yyyy-mm-dd): ");
                                String date = read.readLine();
                                try {
                                    b.setPublishDate(
                                            (LocalDate.parse(date, DateTimeFormatter.ofPattern("yyyy-MM-dd"))));
                                    System.out.println("Publishing date updated succesfully.");
                                    validDate = true;
                                } catch (DateTimeParseException e) {
                                    System.out.println(
                                            "Invalid date format. Please enter the date in yyyy-mm-dd format.");
                                }
                            }
                        }
                    }
                } while (choice != 0);
                saveBooksToFile(book);
                found = true;
                break;
            }
        }
        if (found == false) {
            System.out.println("Book not found");
        }
    }

    void deleteBook() {
        book = readBooksFromFile();
        if (book.isEmpty()) {
            System.out.println("No Books Available.");
            return;
        }

        try {
            boolean found = false;
            System.out.print("Enter ISBN of the book you want to delete : ");
            String isbn = read.readLine();
            for (Book b : book) {
                if (b.getISBN().equals(isbn)) {
                    book.remove(b);
                    saveBooksToFile(book);
                    System.out.println("Book deleted successfully");
                    found = true;
                    break;
                }
            }
            if (!found) {
                System.out.println("Book not found");
            }
        } catch (IOException ex) {
            Logger.getLogger(BookManagement.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    void viewAllBooks() {
        book = readBooksFromFile();
        if (book.isEmpty()) {
            System.out.println("No Books Available.");
            return;
        }

        System.out.println("Books in the library : ");
        for (Book b : book) {
            System.out.println(b);
        }
    }

    void placeOrder() {
        book = readBooksFromFile();
        if (book.isEmpty()) {
            System.out.println("No Books Available.");
            return;
        }
        try {
            System.out.print("Enter isbn of the book you want to borrow : ");
            String bookToOrder = read.readLine();
            boolean found = false;
            for (Book b : book) {
                if (b.getISBN().equals(bookToOrder)) {
                    System.out.print("Enter your name : ");
                    String orderName = read.readLine();
                    LocalDate orderDate = null;
                    boolean validDate = false;
                    while (!validDate) {
                        System.out.print("Enter date of Order (yyyy-mm-dd) : ");
                        String date = read.readLine();
                        try {
                            orderDate = ((LocalDate.parse(date, DateTimeFormatter.ofPattern("yyyy-MM-dd"))));
                            validDate = true;
                        } catch (DateTimeParseException e) {
                            System.out.println("Invalid date format. Please enter the date in yyyy-mm-dd format.");
                        }
                    }
                    order.add(new Order(b, orderName, orderDate));
                    saveOrdersToFile(order);
                    System.out.println("Order placed successfully.");
                    found = true;
                    break;
                }
            }
            if (!found) {
                System.out.println("Book not found...");
            }
        } catch (IOException ex) {
            Logger.getLogger(BookManagement.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    void viewOrders() {
        order = readOrdersFromFile();
        if (order.isEmpty()) {
            System.out.println("No Orders Placed.");
            return;
        }
        System.out.println("Orders : ");
        for (Order o : order) {
            System.out.println(o);
        }
    }

    void issueBook() {
        book = readBooksFromFile();
        if (book.isEmpty()) {
            System.out.println("No Books Available.");
            return;
        }

        try {
            System.out.print("Enter isbn of the book you want to issue: ");
            String bookToIssue = read.readLine();
            boolean found = false;
            for (Book b : book) {
                if (b.getISBN().equals(bookToIssue)) {
                    System.out.print("Enter name of the person to whom the book is issued: ");
                    String issuedTo = read.readLine();
                    LocalDate dueDate = null;
                    boolean validDate = false;
                    while (!validDate) {
                        System.out.print("Enter due date (yyyy-mm-dd): ");
                        String date = read.readLine();
                        try {
                            dueDate = LocalDate.parse(date, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                            validDate = true;
                        } catch (DateTimeParseException e) {
                            System.out.println("Invalid date format. Please enter the date in yyyy-mm-dd format.");
                        }
                    }
                    issuedBook.add(new IssuedBook(b, issuedTo, dueDate));
                    book.remove(b);
                    saveIssuedBooksToFile(issuedBook);
                    saveBooksToFile(book);
                    order.removeIf(o -> o.book.getISBN().equals(bookToIssue)); // Remove order if book is issued
                    saveOrdersToFile(order);
                    System.out.println("Book issued successfully...");
                    found = true;
                    break;
                }
            }
            if (!found) {
                System.out.println("Book not found...");
            }
        } catch (IOException ex) {
            Logger.getLogger(BookManagement.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    void viewIssuedBooks() {
        issuedBook = readIssuedBooksFromFile();
        if (issuedBook.isEmpty()) {
            System.out.println("No books are currently issued.");
            return;
        }

        System.out.println("Issued Books:");
        for (IssuedBook ib : issuedBook) {
            System.out.println(ib);
        }
    }

    void returnBook() {
        issuedBook = readIssuedBooksFromFile();
        book = readBooksFromFile(); // Read the current state of books
        if (issuedBook.isEmpty()) {
            System.out.println("No books are currently issued.");
            return;
        }

        try {
            System.out.print("Enter isbn of the book you want to return : ");
            String bookToReturn = read.readLine();
            boolean found = false;
            List<IssuedBook> toRemove = new ArrayList<>();

            for (IssuedBook ib : issuedBook) {
                if (ib.book.getISBN().equals(bookToReturn)) {
                    found = true;
                    book.add(ib.book); // Add the book back to the available books
                    toRemove.add(ib);
                    boolean validDate = false;
                    LocalDate returnDate;
                    while (!validDate) {
                        System.out.print("Enter date of return (yyyy-mm-dd) : ");
                        String date = read.readLine();
                        try {
                            returnDate = (LocalDate.parse(date, DateTimeFormatter.ofPattern("yyyy-MM-dd")));
                            if (returnDate.isAfter(ib.dueDate)) {
                                long days = ChronoUnit.DAYS.between(ib.dueDate, returnDate);
                                System.out.println("The book is returned " + days + " days late.");
                                System.out.println("You owe a fine of " + (days * 100) + " rupees.");
                            } else {
                                System.out.println("Book returned on time.");
                            }
                            validDate = true;
                        } catch (DateTimeParseException e) {
                            System.out.println("Invalid date format. Please enter the date in yyyy-mm-dd format.");
                        }
                    }
                }
            }
            if (found) {
                issuedBook.removeAll(toRemove); // Remove the returned book from the issued books
                saveBooksToFile(book); // Save updated book list
                saveIssuedBooksToFile(issuedBook); // Save updated issued books
                System.out.println("Book returned successfully.");
            } else {
                System.out.println("The book does not belong to us.");
            }
        } catch (IOException ex) {
            Logger.getLogger(BookManagement.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("An error occurred while processing the return. Please try again.");
        }
    }

}

class Librarian extends BookManagement {

    private final String username;
    private final String password;

    Librarian(String username, String password, BufferedReader read, Scanner sc) {
        super(read, sc);
        this.username = username;
        this.password = password;
    }

    public boolean login() {
        System.out.println("\n\t<<<<<<<<<<<<<<<<<<<<<< LogIn >>>>>>>>>>>>>>>>>>>>>>>>\t\n");

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
        System.out.println("\n\t<<<<<<<<<<<<<<<<<<<<<< Menu >>>>>>>>>>>>>>>>>>>>>>>>\t\n");

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
            System.out.println("9. Exit\n");
            System.out.print("Enter your choice : ");
            try {
                String input = sc.nextLine();
                choice = Integer.parseInt(input);
                switch (choice) {
                    case 1 -> addBook();
                    case 2 -> searchBook();
                    case 3 -> updateBook();
                    case 4 -> deleteBook();
                    case 5 -> issueBook();
                    case 6 -> viewAllBooks();
                    case 7 -> viewIssuedBooks();
                    case 8 -> viewOrders();
                    case 9 -> System.out.println("Exiting Menu");
                    default -> System.out.println("Invalid choice");
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a number.");
                choice = -1;
            } catch (IOException e) {
                System.out.println("An error occurred while reading input.");
                choice = -1;
            }
        } while (choice != 9);
    }
}

class Reader extends BookManagement {

    Reader(BufferedReader read, Scanner sc) {
        super(read, sc);
    }

    public void readerMenu() {
        System.out.println("\t<<<<<<<<<<<<<<<<<<<<<< Menu >>>>>>>>>>>>>>>>>>>>>>>>\t\n");

        int choice;
        do {
            System.out.println("1. Search a book");
            System.out.println("2. View all books");
            System.out.println("3. Place order");
            System.out.println("4. Return a book");
            System.out.println("5. Exit\n");
            System.out.print("Enter your choice : ");
            try {
                String input = sc.nextLine();
                choice = Integer.parseInt(input);
                switch (choice) {
                    case 1 -> searchBook();
                    case 2 -> viewAllBooks();
                    case 3 -> placeOrder();
                    case 4 -> returnBook();
                    case 5 -> System.out.println("Exiting Menu");
                    default -> System.out.println("Invalid choice");
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a number.");
                choice = -1;
            }
        } while (choice != 5);
    }
}
