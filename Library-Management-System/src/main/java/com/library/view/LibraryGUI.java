package com.library.view;

import com.library.controller.LoginController;
import com.library.model.Book;
import com.library.model.Librarian;
import com.library.model.*;
import com.library.service.*;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class LibraryGUI extends JFrame implements LoginController.LoginCallBack {

    private static final String LAUNCH_PANEL = "Launch";
    private static final String LOGIN_LIBRARIAN_PANEL = "LoginLibrarian";
    private static final String LOGIN_READER_PANEL = "LoginReader";
    private static final String DASHBOARD_LIBRARIAN_PANEL = "DashboardLibrarian";
    private static final String DASHBOARD_READER_PANEL = "DashboardReader";

    private JPanel mainPanel;
    private CardLayout cardLayout;

    private LaunchPanel launchPanel;
    private LoginPanel librarianLoginPanel;
    private LoginPanel readerLoginPanel;
    private LibrarianDashboardPanel librarianDashboard;
    private ReaderDashboardPanel readerDashboard;

    private AuthenticationService authService;
    private LoginController librarianLoginController;
    private LoginController readerLoginController;

    private User currentUser;

    private final BookService bookService = new BookService();
    private final UserService userService = new UserService();
    private final LoanService loanService = new LoanService();
    private final FineService fineService = new FineService();

    public LibraryGUI() {
        setTitle("Good Book");
        setSize(1300, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setMinimumSize(new Dimension(700, 700));

        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);
        add(mainPanel);

        launchPanel = new LaunchPanel();
        launchPanel.addLibrarianButtonListener(e -> showLibrarianLogin());
        launchPanel.addReaderButtonListener(e -> showReaderLogin());

        librarianLoginPanel = new LoginPanel("Librarian");
        readerLoginPanel = new LoginPanel("Reader");

        // Create controllers
        authService = new AuthenticationService();
        librarianLoginController = new LoginController(librarianLoginPanel, authService, this);
        readerLoginController = new LoginController(readerLoginPanel, authService, this);

        librarianLoginPanel.setController(librarianLoginController);
        readerLoginPanel.setController(readerLoginController);

        librarianLoginPanel.addBackListener(e -> cardLayout.show(mainPanel, LAUNCH_PANEL));
        readerLoginPanel.addBackListener(e -> cardLayout.show(mainPanel, LAUNCH_PANEL));

        mainPanel.add(launchPanel, LAUNCH_PANEL);
        mainPanel.add(librarianLoginPanel, LOGIN_LIBRARIAN_PANEL);
        mainPanel.add(readerLoginPanel, LOGIN_READER_PANEL);

        // Start on launch
        CardLayout layout = (CardLayout) mainPanel.getLayout();
        layout.show(mainPanel, LAUNCH_PANEL);
    }

    private void showLibrarianLogin() {
        librarianLoginPanel.clearAll();
        cardLayout.show(mainPanel, LOGIN_LIBRARIAN_PANEL);
    }

    private void showReaderLogin() {
        readerLoginPanel.clearAll();
        cardLayout.show(mainPanel, LOGIN_READER_PANEL);
    }

    @Override
    public void onLoginSuccess(User user) {
        this.currentUser = user;

        if (user instanceof Librarian) {
            showLibrarianDashboard((Librarian) user);
        } else if (user instanceof Reader) {
            showReaderDashboard((Reader) user);
        }
    }

    @Override
    public void onLoginError(String errorMesaage) {
        JOptionPane.showMessageDialog(
                this,
                errorMesaage,
                "Login Error",
                JOptionPane.ERROR_MESSAGE);
    }

    private void showLibrarianDashboard(Librarian librarian) {
        if (librarianDashboard == null) {
            librarianDashboard = new LibrarianDashboardPanel(librarian);

            // Dashboard tab listener
            librarianDashboard.addDashboardListener(e -> {
                loadDashboardStats();
                loadRecentActivity();
            });

            // Books tab listener
            librarianDashboard.addBooksListener(e -> loadBooksIntoLibrarianDashboard());

            librarianDashboard.addAddBookButtonListener(e -> handleAddBook());

            librarianDashboard.addSearchBookButtonListener(e -> handleSearchBook());

            librarianDashboard.setBookActionsListener(
                    new LibrarianDashboardPanel.BookActionsListener() {
                        @Override
                        public void onView(String bookIsbn, int row) {
                            Book book = bookService.findByISBN(bookIsbn);
                            if (book == null) {
                                JOptionPane.showMessageDialog(
                                        librarianDashboard,
                                        "Book not found",
                                        "Error",
                                        JOptionPane.ERROR_MESSAGE);
                                return;
                            }
                            BookDialog.showDetailsDialog(librarianDashboard, book);
                        }

                        @Override
                        public void onEdit(String bookIsbn, int row) {
                            Book original = bookService.findByISBN(bookIsbn);
                            if (original == null) {
                                JOptionPane.showMessageDialog(
                                        librarianDashboard,
                                        "Book not found for ISBN: " + bookIsbn,
                                        "Error",
                                        JOptionPane.ERROR_MESSAGE);
                                return;
                            }

                            Book edited = BookDialog.showEditBookDialog(librarianDashboard, original);
                            if (edited == null) {
                                return;
                            }

                            boolean ok = bookService.update(edited);
                            if (!ok) {
                                JOptionPane.showMessageDialog(
                                        librarianDashboard,
                                        "Failed to update book in database.",
                                        "Error",
                                        JOptionPane.ERROR_MESSAGE);
                                return;
                            }

                            loadBooksIntoLibrarianDashboard();

                            JOptionPane.showMessageDialog(
                                    librarianDashboard,
                                    "Book record updated successfully",
                                    "Success",
                                    JOptionPane.INFORMATION_MESSAGE);
                        }

                        @Override
                        public void onDelete(String bookIsbn, int row) {
                            Book b = bookService.findByISBN(bookIsbn);
                            int confirm = JOptionPane.showConfirmDialog(
                                    librarianDashboard,
                                    "Delete book " + bookIsbn + "?",
                                    "Confirm Delete",
                                    JOptionPane.YES_NO_OPTION);
                            if (confirm == JOptionPane.YES_OPTION) {
                                bookService.deleteById(b.getBookId());
                                librarianDashboard.removeBookRow(row);
                                loadDashboardStats(); // Refresh stats
                                loadRecentActivity();
                            }
                        }

                        @Override
                        public void onIssue(String bookIsbn, int row) {
                            handleIssueBook(bookIsbn, row);
                        }

                        @Override
                        public void onReturn(String bookId, int row) {
                            handleReturnBook(bookId, row);
                        }
                    });

            // Users tab listener
            librarianDashboard.addUsersListener(e -> loadUsersIntoLibrarianDashboard());

            librarianDashboard.addAddUserButtonListener(e -> handleAddUser());

            librarianDashboard.addUserSearchListener(e -> handleUserSearch());

            librarianDashboard.setUserActionsListener(
                    new LibrarianDashboardPanel.UserActionsListener() {
                        @Override
                        public void onView(String userId, int row) {
                            User user = userService.findById(Integer.parseInt(userId));
                            if (user == null) {
                                JOptionPane.showMessageDialog(
                                        librarianDashboard,
                                        "User with Id " + userId + " not found",
                                        "Error",
                                        JOptionPane.ERROR_MESSAGE);
                                return;
                            }
                            UserDialog.showDetailsDialog(librarianDashboard, user);
                        }

                        @Override
                        public void onEdit(String userId, int row) {
                            User original = userService.findById(Integer.parseInt(userId));
                            if (original == null) {
                                JOptionPane.showMessageDialog(
                                        librarianDashboard,
                                        "User not found",
                                        "Error",
                                        JOptionPane.ERROR_MESSAGE);
                                return;
                            }
                            User edited = UserDialog.showEditDialog(librarianDashboard, original);
                            if (edited == null) {
                                return;
                            }

                            Boolean ok = userService.update(edited);
                            if (!ok) {
                                JOptionPane.showMessageDialog(
                                        librarianDashboard,
                                        "Failed to update user in database",
                                        "Error",
                                        JOptionPane.ERROR_MESSAGE);
                                return;
                            }

                            loadUsersIntoLibrarianDashboard();
                            loadDashboardStats(); // Refresh stats
                            loadRecentActivity();

                            JOptionPane.showMessageDialog(
                                    librarianDashboard,
                                    "User record updated successfully.",
                                    "Success",
                                    JOptionPane.INFORMATION_MESSAGE);
                        }

                        @Override
                        public void onDelete(String userId, int row) {
                            int confirm = JOptionPane.showConfirmDialog(
                                    librarianDashboard,
                                    "Delete User " + userId + "?",
                                    "Confirm Delete",
                                    JOptionPane.YES_NO_OPTION);
                            if (confirm == JOptionPane.YES_OPTION) {
                                userService.deleteById(Integer.parseInt(userId));
                                librarianDashboard.removeUserRow(row);
                                loadDashboardStats(); // Refresh stats
                                loadRecentActivity();
                            }
                        }
                    });

            librarianDashboard.addLogoutListener(e -> handleLogout());

            mainPanel.add(librarianDashboard, DASHBOARD_LIBRARIAN_PANEL);
        }

        // Load initial dashboard data
        loadDashboardStats();
        loadRecentActivity();
        loadBooksIntoLibrarianDashboard();
        loadUsersIntoLibrarianDashboard();

        cardLayout.show(mainPanel, DASHBOARD_LIBRARIAN_PANEL);
    }

    private void showReaderDashboard(Reader reader) {
        if (readerDashboard == null) {
            readerDashboard = new ReaderDashboardPanel(reader);

            // Dashboard tab listener
            readerDashboard.addDashboardListener(e -> {
                loadReaderDashboardData(reader);
            });

            // Browse Books tab listener
            readerDashboard.addBrowseBooksListener(e -> {
                loadBooksIntoReaderBrowse();
            });

            // Add search listener for Reader dashboard
            readerDashboard.addBookSearchListener(e -> {
                loadBooksIntoReaderBrowse();
            });

            // Set Browse Book Actions Listener
            readerDashboard.setBrowseBookActionsListener(new ReaderDashboardPanel.BrowseBookActionsListener() {
                @Override
                public void onView(String isbn) {
                    Book book = bookService.findByISBN(isbn);
                    if (book != null) {
                        JOptionPane.showMessageDialog(readerDashboard,
                                "Title: " + book.getTitle() + "\n" +
                                        "Author: " + book.getAuthor() + "\n" +
                                        "Category: " + book.getCategory() + "\n" +
                                        "ISBN: " + book.getIsbn() + "\n" +
                                        "Available Copies: " + book.getAvailableCopies(),
                                "Book Details",
                                JOptionPane.INFORMATION_MESSAGE);
                    }
                }

                @Override
                public void onBorrow(String isbn) {
                    Book book = bookService.findByISBN(isbn);
                    if (book != null) {
                        int confirm = JOptionPane.showConfirmDialog(readerDashboard,
                                "Do you want to borrow: " + book.getTitle() + "?",
                                "Confirm Borrow",
                                JOptionPane.YES_NO_OPTION);

                        if (confirm == JOptionPane.YES_OPTION) {
                            performReaderBorrow(book, reader);
                        }
                    }
                }
            });

            // My Books tab listener
            readerDashboard.addMyBooksListener(e -> {
                loadReaderMyBooks(reader);
            });

            // History tab listener
            readerDashboard.addHistoryListener(e -> {
                loadReaderHistory(reader);
            });

            // Fines tab listener
            readerDashboard.addFinesListener(e -> {
                loadReaderFines(reader);
            });

            readerDashboard.setMyBookActionsListener(
                    new ReaderDashboardPanel.MyBookActionsListener() {
                        @Override
                        public void onView(String bookTitle, int row) {
                            JOptionPane.showMessageDialog(readerDashboard,
                                    "Viewing details for:\n\n" + bookTitle,
                                    "Book Details",
                                    JOptionPane.INFORMATION_MESSAGE);
                        }

                        @Override
                        public void onRenew(String bookTitle, int row) {
                            int confirm = JOptionPane.showConfirmDialog(readerDashboard,
                                    "Do you want to renew this book?\n\n" + bookTitle,
                                    "Renew Book",
                                    JOptionPane.YES_NO_OPTION);

                            if (confirm == JOptionPane.YES_OPTION) {
                                JOptionPane.showMessageDialog(readerDashboard,
                                        "Book renewed successfully!",
                                        "Success",
                                        JOptionPane.INFORMATION_MESSAGE);
                            }
                        }

                        @Override
                        public void onReturn(String bookTitle, int row) {
                            int confirm = JOptionPane.showConfirmDialog(readerDashboard,
                                    "Are you sure you want to return this book?\n\n" + bookTitle,
                                    "Return Book",
                                    JOptionPane.YES_NO_OPTION);

                            if (confirm == JOptionPane.YES_OPTION) {
                                readerDashboard.removeMyBookRow(row);
                                loadReaderDashboardData(reader);
                                loadReaderMyBooks(reader);
                                JOptionPane.showMessageDialog(readerDashboard,
                                        "Book returned successfully!",
                                        "Success",
                                        JOptionPane.INFORMATION_MESSAGE);
                            }
                        }
                    });

            readerDashboard.addLogoutListener(e -> handleLogout());

            mainPanel.add(readerDashboard, DASHBOARD_READER_PANEL);
        }

        // Load initial data for ALL tabs
        loadReaderDashboardData(reader);
        loadBooksIntoReaderBrowse();
        loadReaderMyBooks(reader);
        loadReaderHistory(reader);
        loadReaderFines(reader);

        cardLayout.show(mainPanel, DASHBOARD_READER_PANEL);
    }

    private void loadDashboardStats() {
        // Generate fines for overdue loans
        loanService.generateFinesForOverdueLoans();

        int totalBooks = bookService.countAll(); // COUNT(*) = 3 books
        int totalCopies = bookService.countTotalCopies(); // SUM(total_copies)
        int availableCopies = bookService.countAvailable(); // SUM(available_copies) = 25
        int totalUsers = userService.countAll();
        int issuedCopies = totalCopies - availableCopies; // Issued = total - available

        System.out.println("DEBUG stats -> books=" + totalBooks +
                ", totalCopies=" + totalCopies +
                ", availCopies=" + availableCopies +
                ", users=" + totalUsers +
                ", issuedCopies=" + issuedCopies);

        librarianDashboard.setStats(totalBooks, issuedCopies, totalUsers, availableCopies);
    }

    // Load recent activity from real data
    private void loadRecentActivity() {
        List<Object[]> rows = new ArrayList<>();

        // Last 5 books as "Book Added"
        List<Book> books = bookService.findAll();
        for (int i = 0; i < Math.min(5, books.size()); i++) {
            Book b = books.get(i);
            rows.add(new Object[] {
                    "-",
                    "Book Added: " + b.getTitle(),
                    b.getAuthor(),
                    "✅ Completed"
            });
        }

        // Last 5 users as "User Added"
        List<User> users = userService.findAll();
        for (int i = 0; i < Math.min(5, users.size()); i++) {
            User u = users.get(i);
            rows.add(new Object[] {
                    "-",
                    "User Added: " + u.getFullName(),
                    u.getRole(),
                    "✅ Completed"
            });
        }

        // Convert to Object[][]
        Object[][] data = new Object[rows.size()][4];
        for (int i = 0; i < rows.size(); i++) {
            data[i] = rows.get(i);
        }

        librarianDashboard.setActivityData(data);
    }

    private void loadBooksIntoLibrarianDashboard() {
        List<Book> books = bookService.findAll();

        Object[][] rows = new Object[books.size()][6];
        for (int i = 0; i < books.size(); i++) {
            Book b = books.get(i);
            String status = b.getAvailableCopies() > 0 ? "Available" : "Issued";
            rows[i][0] = b.getIsbn();
            rows[i][1] = b.getTitle();
            rows[i][2] = b.getAuthor();
            rows[i][3] = b.getCategory();
            rows[i][4] = status;
            rows[i][5] = "⋮";
        }

        librarianDashboard.setBooksData(rows);
    }

    private void loadUsersIntoLibrarianDashboard() {
        List<User> users = userService.findAll();

        Object[][] rows = new Object[users.size()][6];
        for (int i = 0; i < users.size(); i++) {
            User u = users.get(i);
            rows[i][0] = u.getId();
            rows[i][1] = u.getFullName();
            rows[i][2] = u.getEmail() != null ? u.getEmail() : "-";
            rows[i][3] = u.getRole();
            rows[i][4] = u.getStatus();
            rows[i][5] = "⋮";
        }

        librarianDashboard.setUsersData(rows);
    }

    private void handleAddBook() {
        Book newBook = BookDialog.showAddDialog(librarianDashboard);
        if (newBook == null) {
            return;
        }

        boolean ok = bookService.add(newBook);
        if (!ok) {
            JOptionPane.showMessageDialog(
                    librarianDashboard,
                    "Failed to add book to database.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        loadBooksIntoLibrarianDashboard();
        loadDashboardStats();
        loadRecentActivity();

        JOptionPane.showMessageDialog(
                librarianDashboard,
                "Book added successfully.",
                "Success",
                JOptionPane.INFORMATION_MESSAGE);
    }

    private void handleAddUser() {
        User newUser = UserDialog.showAddDialog(librarianDashboard);
        if (newUser == null)
            return;

        boolean ok = userService.add(newUser);
        if (!ok) {
            JOptionPane.showMessageDialog(librarianDashboard,
                    "Failed to add user.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        loadUsersIntoLibrarianDashboard();
        loadDashboardStats();
        loadRecentActivity();

        JOptionPane.showMessageDialog(librarianDashboard,
                "User added successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
    }

    private void handleSearchBook() {
        String query = librarianDashboard.getBooksSearchText().trim();

        List<Book> books;
        if (query.isEmpty()) {
            books = bookService.findAll();
        } else {
            books = bookService.search(query);
        }

        Object[][] rows = new Object[books.size()][6];
        for (int i = 0; i < books.size(); i++) {
            Book b = books.get(i);
            String status = b.getAvailableCopies() > 0 ? "Available" : "Issued";

            rows[i][0] = b.getIsbn();
            rows[i][1] = b.getTitle();
            rows[i][2] = b.getAuthor();
            rows[i][3] = b.getCategory();
            rows[i][4] = status;
            rows[i][5] = "⋮";
        }
        librarianDashboard.setBooksData(rows);
    }

    private void handleIssueBook(String bookIsbn, int row) {
        Book book = bookService.findByISBN(bookIsbn);
        if (book == null) {
            JOptionPane.showMessageDialog(
                    librarianDashboard,
                    "Book not found.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Check if book is available
        if (book.getAvailableCopies() <= 0) {
            JOptionPane.showMessageDialog(
                    librarianDashboard,
                    "No copies available for this book.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Show issue dialog
        BookIssueDialog.IssueResult result = BookIssueDialog.showDialog(
                (Frame) SwingUtilities.getWindowAncestor(librarianDashboard),
                book);

        if (result == null) {
            return; // User cancelled
        }

        System.out.println("DEBUG: currentUser = " + currentUser);
        System.out.println(
                "DEBUG: currentUser class = " + (currentUser != null ? currentUser.getClass().getName() : "null"));
        System.out.println("DEBUG: currentUser ID = " + (currentUser != null ? currentUser.getId() : "null"));

        // Get librarian ID if available
        int librarianId = 0;
        if (currentUser != null && currentUser instanceof Librarian) {
            librarianId = currentUser.getId();
            System.out.println("DEBUG: Setting librarian ID to " + librarianId);
        }

        if (librarianId == 0) {
            System.err.println("WARNING: librarian_id is 0, this will fail!");
            // We proceed anyway, LoanService handles 0 by setting NULL
        }

        // Create loan with librarian ID
        boolean loanCreated = loanService.issueBook(
                book.getBookId(),
                result.getReader().getId(),
                librarianId,
                result.getBorrowDate(),
                result.getDueDate(),
                null);

        if (!loanCreated) {
            JOptionPane.showMessageDialog(
                    librarianDashboard,
                    "Failed to create loan record.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Decrement available copies
        boolean copiesUpdated = bookService.decrementAvailableCopies(book.getBookId());
        if (!copiesUpdated) {
            JOptionPane.showMessageDialog(
                    librarianDashboard,
                    "Failed to update book inventory.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Update UI
        loadBooksIntoLibrarianDashboard();
        loadDashboardStats();
        loadRecentActivity();

        JOptionPane.showMessageDialog(
                librarianDashboard,
                "Book issued successfully to " + result.getReader().getFullName() + "!\n\n" +
                        "Due Date: " + result.getDueDate(),
                "Success",
                JOptionPane.INFORMATION_MESSAGE);
    }

    private void handleReturnBook(String bookIsbn, int row) {
        Book book = bookService.findByISBN(bookIsbn);
        if (book == null) {
            JOptionPane.showMessageDialog(
                    librarianDashboard,
                    "Book not found.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Find active loan for this book
        Loan activeLoan = loanService.findActiveLoanByBookId(book.getBookId());
        if (activeLoan == null) {
            JOptionPane.showMessageDialog(
                    librarianDashboard,
                    "No active loan found for this book.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Calculate fine if overdue
        double fine = fineService.calculateFineForLoan(activeLoan);

        String message = "Return book: " + book.getTitle() + "?";
        if (fine > 0) {
            message += "\n\n⚠️ This book is overdue!\nFine amount: Rs " + String.format("%.2f", fine);
        }

        int confirm = JOptionPane.showConfirmDialog(
                librarianDashboard,
                message,
                "Return Book",
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            // Mark loan as returned
            boolean loanReturned = loanService.returnBook(activeLoan.getLoanId());
            if (!loanReturned) {
                JOptionPane.showMessageDialog(
                        librarianDashboard,
                        "Failed to return book.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Increment available copies
            bookService.incrementAvailableCopies(book.getBookId());

            // Create fine record if overdue
            if (fine > 0) {
                Fine fineRecord = new Fine(activeLoan.getLoanId(), activeLoan.getReaderId(), fine);
                fineService.addFine(fineRecord);
            }

            // Update UI
            loadBooksIntoLibrarianDashboard();
            loadDashboardStats();
            loadRecentActivity();

            String successMsg = "Book returned successfully!";
            if (fine > 0) {
                successMsg += "\n\nFine of Rs " + String.format("%.2f", fine) + " has been recorded.";
            }

            JOptionPane.showMessageDialog(
                    librarianDashboard,
                    successMsg,
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void handleUserSearch() {
        String query = librarianDashboard.getUserSearchText().trim();

        List<User> users;
        if (query.isEmpty()) {
            users = userService.findAll();
        } else {
            users = searchUsers(query);
        }

        Object[][] rows = new Object[users.size()][6];
        for (int i = 0; i < users.size(); i++) {
            User u = users.get(i);
            rows[i][0] = u.getId();
            rows[i][1] = u.getFullName();
            rows[i][2] = u.getEmail() != null ? u.getEmail() : "-";
            rows[i][3] = u.getRole();
            rows[i][4] = u.getStatus();
            rows[i][5] = "⋮";
        }

        librarianDashboard.setUsersData(rows);
    }

    private List<User> searchUsers(String query) {
        List<User> allUsers = userService.findAll();
        List<User> results = new ArrayList<>();
        String lowerQuery = query.toLowerCase();

        for (User u : allUsers) {
            if (u.getUsername().toLowerCase().contains(lowerQuery) ||
                    u.getFullName().toLowerCase().contains(lowerQuery) ||
                    u.getRole().toLowerCase().contains(lowerQuery)) {
                results.add(u);
            }
        }

        return results;
    }

    private void loadReaderDashboardData(Reader reader) {
        // Generate fines for overdue loans
        loanService.generateFinesForOverdueLoans();

        int borrowed = loanService.countActiveLoansByReaderId(reader.getId());
        int dueSoon = loanService.countDueSoonByReaderId(reader.getId(), 7);
        int overdue = loanService.countOverdueByReaderId(reader.getId());
        int totalRead = loanService.countTotalReadByReaderId(reader.getId());

        System.out.println("DEBUG: Reader stats - borrowed=" + borrowed + ", dueSoon=" + dueSoon +
                ", overdue=" + overdue + ", totalRead=" + totalRead);

        readerDashboard.setDashboardStats(borrowed, dueSoon, overdue, totalRead);

        // Load unpaid fines amount
        double unpaidFines = fineService.getTotalUnpaidFinesByReaderId(reader.getId());
        readerDashboard.setUnpaidFines(unpaidFines);

        // Load borrowed cards
        readerDashboard.clearBorrowedCards();
        List<Loan> activeLoans = loanService.findActiveLoansByReaderId(reader.getId());
        for (Loan loan : activeLoans) {
            double fine = fineService.calculateFineForLoan(loan);
            readerDashboard.addBorrowedCard(
                    loan.getBookTitle(),
                    loan.getBookAuthor(),
                    loan.getBorrowedDate(),
                    loan.getDueDate(),
                    fine);
        }
    }

    private void loadBooksIntoReaderBrowse() {
        String query = readerDashboard.getBookSearchText().trim();
        List<Book> books;
        if (query.isEmpty()) {
            books = bookService.findAll();
        } else {
            books = bookService.search(query);
        }

        System.out.println("DEBUG: Loading " + books.size() + " books into reader browse");

        readerDashboard.clearBrowseBooks();
        for (Book b : books) {
            boolean available = b.getAvailableCopies() > 0;
            readerDashboard.addBrowseBookCard(
                    b.getIsbn(), // Pass ISBN
                    b.getTitle(),
                    b.getAuthor(),
                    b.getCategory() != null ? b.getCategory() : "General",
                    available);
        }
    }

    private void performReaderBorrow(Book book, Reader reader) {
        if (book.getAvailableCopies() <= 0) {
            JOptionPane.showMessageDialog(readerDashboard,
                    "Sorry, this book is currently unavailable.",
                    "Unavailable",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Default loan period of 14 days
        java.time.LocalDate borrowDate = java.time.LocalDate.now();
        java.time.LocalDate dueDate = borrowDate.plusDays(14);

        boolean success = loanService.issueBook(book.getBookId(), reader.getId(), borrowDate, dueDate);

        if (success) {
            bookService.decrementAvailableCopies(book.getBookId());
            JOptionPane.showMessageDialog(readerDashboard,
                    "You have successfully borrowed: " + book.getTitle() + "\nDue Date: " + dueDate,
                    "Borrow Success",
                    JOptionPane.INFORMATION_MESSAGE);
            loadBooksIntoReaderBrowse(); // Refresh availability
            loadReaderDashboardData(reader); // Refresh stats
        } else {
            JOptionPane.showMessageDialog(readerDashboard,
                    "Failed to borrow book. Please try again or contact a librarian.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadReaderMyBooks(Reader reader) {
        List<Loan> loans = loanService.findActiveLoansByReaderId(reader.getId());

        Object[][] rows = new Object[loans.size()][7]; // 7 columns (with Fine)
        for (int i = 0; i < loans.size(); i++) {
            Loan loan = loans.get(i);
            long daysLeft = java.time.temporal.ChronoUnit.DAYS.between(java.time.LocalDate.now(), loan.getDueDate());
            double fine = fineService.calculateFineForLoan(loan);

            rows[i][0] = loan.getBookTitle();
            rows[i][1] = loan.getBookAuthor();
            rows[i][2] = loan.getBorrowedDate().toString();
            rows[i][3] = loan.getDueDate().toString();
            rows[i][4] = daysLeft + " days";
            rows[i][5] = fine > 0 ? "Rs " + String.format("%.2f", fine) : "Rs 0";
            rows[i][6] = "⋮";
        }

        readerDashboard.setMyBooksData(rows);
    }

    private void loadReaderHistory(Reader reader) {
        List<Loan> history = loanService.findLoanHistoryByReaderId(reader.getId());

        Object[][] rows = new Object[history.size()][5];
        for (int i = 0; i < history.size(); i++) {
            Loan loan = history.get(i);
            rows[i][0] = loan.getBookTitle();
            rows[i][1] = loan.getBookAuthor();
            rows[i][2] = loan.getBorrowedDate().toString();
            rows[i][3] = loan.getReturnDate() != null ? loan.getReturnDate().toString() : "-";
            rows[i][4] = "✅ Returned";
        }

        readerDashboard.setHistoryData(rows);
    }

    private void loadReaderFines(Reader reader) {
        List<Fine> fines = fineService.findAllFinesByReaderId(reader.getId());

        Object[][] rows = new Object[fines.size()][5];
        for (int i = 0; i < fines.size(); i++) {
            Fine fine = fines.get(i);
            rows[i][0] = fine.getLoanId();
            rows[i][1] = "Rs " + String.format("%.2f", fine.getAmount());
            rows[i][2] = fine.getStatus();
            rows[i][3] = fine.getCreatedDate().toString();
            rows[i][4] = fine.getPaidDate() != null ? fine.getPaidDate().toString() : "-";
        }

        readerDashboard.setFinesData(rows);
    }

    private void handleLogout() {
        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to logout",
                "Confirm Logout",
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {

            if (librarianDashboard != null) {
                mainPanel.remove(librarianDashboard);
                librarianDashboard = null;
            }
            if (readerDashboard != null) {
                mainPanel.remove(readerDashboard);
                readerDashboard = null;
            }
            cardLayout.show(mainPanel, LAUNCH_PANEL);
            currentUser = null;
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new LibraryGUI().setVisible(true));
    }
}
