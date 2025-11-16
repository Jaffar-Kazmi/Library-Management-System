package com.library.view;

import com.library.controller.LoginController;
import com.library.model.Book;
import com.library.model.Librarian;
import com.library.model.Reader;
import com.library.model.User;
import com.library.service.AuthenticationService;
import com.library.service.BookService;

import javax.swing.*;
import java.awt.*;
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

    public LibraryGUI() {
        setTitle("Good Books");
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

//        // dumy librarian
//        launchPanel.addLibrarianButtonListener(e -> showLibrarianDashboard());
//        launchPanel.addReaderButtonListener(e -> showReaderDashboard());

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
        } else if (user instanceof Reader){
            showReaderDashboard((Reader) user);
        }
    }

    @Override
    public void onLoginError(String errorMesaage) {
        JOptionPane.showMessageDialog(
                this,
                errorMesaage,
                "Login Error",
                JOptionPane.ERROR_MESSAGE
        );
    }

    private void showLibrarianDashboard(Librarian librarian) {
        if(librarianDashboard == null) {
            librarianDashboard = new LibrarianDashboardPanel(librarian);

            librarianDashboard.addBooksListener(e -> loadBooksintoLibrarianDashboard());

            librarianDashboard.addAddBookButtonListener(e -> handleAddBook());

            librarianDashboard.setBookActionsListener(
                    new LibrarianDashboardPanel.BookActionsListener() {
                        @Override
                        public void onView(String bookId, int row) {
                            JOptionPane.showMessageDialog(librarianDashboard,
                                    "Viewing details for book ID: " + bookId);
                        }

                        @Override
                        public void onEdit(String bookIsbn, int row) {
                            Book original = bookService.findByISBN(bookIsbn);
                            if (original == null) {
                                JOptionPane.showMessageDialog(
                                        librarianDashboard,
                                        "Book not found for ISBN: " + bookIsbn,
                                        "Error",
                                        JOptionPane.ERROR_MESSAGE
                                );
                                return;
                            }

                            Book edited = BookDialog.showEditBookDialog(librarianDashboard, original);
                            if (edited == null ) {
                                return;
                            }

                            boolean ok = bookService.update(edited);
                            if (!ok) {
                                JOptionPane.showMessageDialog(
                                        librarianDashboard,
                                        "Failed to update book in database.",
                                        "Error",
                                        JOptionPane.ERROR_MESSAGE
                                );
                                return;
                            }

                            loadBooksintoLibrarianDashboard();

                            JOptionPane.showMessageDialog(
                                        librarianDashboard,
                                        "Book record updated successfully",
                                        "Success",
                                        JOptionPane.INFORMATION_MESSAGE
                            );
                        }

                        @Override
                        public void onDelete(String bookIsbn, int row) {
                            Book b = bookService.findByISBN(bookIsbn);
                            int confirm = JOptionPane.showConfirmDialog(
                                    librarianDashboard,
                                    "Delete book " + bookIsbn + "?",
                                    "Confirm Delete",
                                    JOptionPane.YES_NO_OPTION
                            );
                            if (confirm == JOptionPane.YES_OPTION) {
                                bookService.deleteById(b.getBookId());
                                librarianDashboard.removeBookRow(row);
                            }
                        }

                        @Override
                        public void onIssue(String bookId, int row) {
                            String userId = JOptionPane.showInputDialog(
                                    librarianDashboard,
                                    "Enter User ID to issue this book:"
                            );
                            if (userId != null && !userId.trim().isEmpty()) {
                                // later: loanService.issueBook(...)
                                librarianDashboard.markBookIssued(row);
                            }
                        }

                        @Override
                        public void onReturn(String bookId, int row) {
                            int confirm = JOptionPane.showConfirmDialog(
                                    librarianDashboard,
                                    "Mark this book as returned?",
                                    "Return Book",
                                    JOptionPane.YES_NO_OPTION
                            );
                            if (confirm == JOptionPane.YES_OPTION) {
                                // later: loanService.returnBook(...)
                                librarianDashboard.markBookAvailable(row);
                            }
                        }
                    }
            );

            librarianDashboard.setUserActionsListener(
                    new LibrarianDashboardPanel.UserActionsListener() {
                        @Override
                        public void onView(String userId, int row) {
                            JOptionPane.showMessageDialog(librarianDashboard,
                                    "Viewing details for User ID: " + userId);
                        }

                        @Override
                        public void onEdit(String userId, int row) {
                            JOptionPane.showMessageDialog(librarianDashboard,
                                    "Editing details for User ID: " + userId);
                        }

                        @Override
                        public void onDelete(String userId, int row) {
                            int confirm = JOptionPane.showConfirmDialog(
                                    librarianDashboard,
                                    "Delete User " + userId + "?",
                                    "Confirm Delete",
                                    JOptionPane.YES_NO_OPTION
                            );
                            if (confirm == JOptionPane.YES_OPTION) {
                                // later: userService.deleteBook(...)
                                librarianDashboard.removeUserRow(row);
                            }
                        }
                    }
            );

            librarianDashboard.addLogoutListener(e -> handleLogout());

            mainPanel.add(librarianDashboard, DASHBOARD_LIBRARIAN_PANEL);
        }
        cardLayout.show(mainPanel, DASHBOARD_LIBRARIAN_PANEL);
    }

    private void showReaderDashboard(Reader reader) {
        if (readerDashboard == null) {
            readerDashboard = new ReaderDashboardPanel(reader);

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
                                    "Do you want to renew this book?\n\n" + bookTitle + "\n\nThis will extend the due date by 14 days.",
                                    "Renew Book",
                                    JOptionPane.YES_NO_OPTION,
                                    JOptionPane.QUESTION_MESSAGE);

                            if (confirm == JOptionPane.YES_OPTION) {
                                // Update due date
                                JOptionPane.showMessageDialog(readerDashboard,
                                        "Book renewed successfully!\nNew due date has been updated.",
                                        "Success",
                                        JOptionPane.INFORMATION_MESSAGE);
                            }
                        }

                        @Override
                        public void onReturn(String bookTitle, int row) {
                            int confirm = JOptionPane.showConfirmDialog(readerDashboard,
                                    "Are you sure you want to return this book?\n\n" + bookTitle,
                                    "Return Book",
                                    JOptionPane.YES_NO_OPTION,
                                    JOptionPane.QUESTION_MESSAGE);

                            if (confirm == JOptionPane.YES_OPTION) {
                                readerDashboard.removeMyBookRow(row);
                                JOptionPane.showMessageDialog(readerDashboard,
                                        "Book returned successfully!",
                                        "Success",
                                        JOptionPane.INFORMATION_MESSAGE);
                            }
                        }
                    }
            );

            readerDashboard.addLogoutListener(e -> handleLogout());

            mainPanel.add(readerDashboard, DASHBOARD_READER_PANEL);
        }
        cardLayout.show(mainPanel, DASHBOARD_READER_PANEL);
    }

    // Dumy Librarian to bypass login
    private void showLibrarianDashboard() {
        Librarian dummyLibrarian = new Librarian("admin", "123", "Syed Jaffar Raza Kazmi"); // dummy object
        LibrarianDashboardPanel dashboard = new LibrarianDashboardPanel(dummyLibrarian);

        mainPanel.add(dashboard, "librarianDashboard");
        CardLayout layout = (CardLayout) mainPanel.getLayout();
        layout.show(mainPanel, "librarianDashboard");
    }

    // Dumy Reader to bypass login
    private void  showReaderDashboard(){
        Reader dumyReader = new Reader("reader", "123", "Raza Kazmi");
        ReaderDashboardPanel dashboard = new ReaderDashboardPanel(dumyReader);

        mainPanel.add(dashboard, "readerDashboard");
        CardLayout layout = (CardLayout) mainPanel.getLayout();
        layout.show(mainPanel, "readerDashboard");
    }

    private void loadBooksintoLibrarianDashboard() {
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
                    JOptionPane.ERROR_MESSAGE
            );
            return;
        }

        loadBooksintoLibrarianDashboard();

        JOptionPane.showMessageDialog(
                librarianDashboard,
                "Book added successfully.",
                "Success",
                JOptionPane.INFORMATION_MESSAGE
        );
    }

    private void loadBooksIntoReaderBrowse(ReaderDashboardPanel readerDashboard) {
        List<Book> books = bookService.findAvailable();

        readerDashboard.clearBrowseBooks();
        for (Book b : books) {
            boolean available = b.getAvailableCopies() > 0;
            readerDashboard.addBrowseBookCard(
                    b.getTitle(),
                    b.getAuthor(),
                    b.getCategory(),
                    available
            );
        }
    }

    private void handleLogout() {
        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to logout",
                "Confirm Logout",
                JOptionPane.YES_NO_OPTION
        );

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
