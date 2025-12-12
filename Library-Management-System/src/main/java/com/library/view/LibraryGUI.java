package com.library.view;

import com.library.controller.LoginController;
import com.library.model.*;
import com.library.service.*;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
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
    private BookRequestService bookRequestService = new BookRequestService();

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
    public void onLoginError(String errorMessage) {
        JOptionPane.showMessageDialog(
                this,
                errorMessage,
                "Login Error",
                JOptionPane.ERROR_MESSAGE
        );
    }

    // ===================== LIBRARIAN DASHBOARD =====================

    private void showLibrarianDashboard(Librarian librarian) {
        if (librarianDashboard == null) {
            librarianDashboard = new LibrarianDashboardPanel(librarian);

            // Dashboard tab listener
            librarianDashboard.addDashboardListener(e -> {
                loadDashboardStats();
                loadPendingRequests();
            });

            // Books tab listener
            librarianDashboard.addBooksListener(e -> loadBooksIntoLibrarianDashboard());
            librarianDashboard.addAddBookButtonListener(e -> handleAddBook());
            librarianDashboard.addSearchBookButtonListener(e -> handleSearchBook());

            // Category filter for books
            librarianDashboard.setCategoryFilterListener(category -> {
                List<Book> filteredBooks;
                if (category.equals("All")) {
                    filteredBooks = bookService.findAll();
                } else {
                    filteredBooks = bookService.findByCategory(category);
                }

                // Update table
                Object[][] rows = new Object[filteredBooks.size()][6];
                for (int i = 0; i < filteredBooks.size(); i++) {
                    Book b = filteredBooks.get(i);
                    String status = b.getAvailableCopies() > 0 ? "Available" : "Issued";
                    rows[i][0] = b.getIsbn();
                    rows[i][1] = b.getTitle();
                    rows[i][2] = b.getAuthor();
                    rows[i][3] = b.getCategory();
                    rows[i][4] = status;
                    rows[i][5] = "Actions";
                }
                librarianDashboard.setBooksData(rows);
            });

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
                                        JOptionPane.ERROR_MESSAGE
                                );
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
                                        JOptionPane.ERROR_MESSAGE
                                );
                                return;
                            }
                            Book edited = BookDialog.showEditBookDialog(librarianDashboard, original);
                            if (edited == null) return;

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
                            loadBooksIntoLibrarianDashboard();
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
                                    "Delete book: " + bookIsbn + "?",
                                    "Confirm Delete",
                                    JOptionPane.YES_NO_OPTION
                            );
                            if (confirm == JOptionPane.YES_OPTION) {
                                bookService.deleteById(b.getBookId());
                                librarianDashboard.removeBookRow(row);
                                loadDashboardStats();
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
                    }
            );

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
                                        JOptionPane.ERROR_MESSAGE
                                );
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
                                        JOptionPane.ERROR_MESSAGE
                                );
                                return;
                            }
                            User edited = UserDialog.showEditDialog(librarianDashboard, original);
                            if (edited == null) return;

                            Boolean ok = userService.update(edited);
                            if (!ok) {
                                JOptionPane.showMessageDialog(
                                        librarianDashboard,
                                        "Failed to update user in database",
                                        "Error",
                                        JOptionPane.ERROR_MESSAGE
                                );
                                return;
                            }
                            loadUsersIntoLibrarianDashboard();
                            loadDashboardStats();
                            JOptionPane.showMessageDialog(
                                    librarianDashboard,
                                    "User record updated successfully.",
                                    "Success",
                                    JOptionPane.INFORMATION_MESSAGE
                            );
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
                                userService.deleteById(Integer.parseInt(userId));
                                librarianDashboard.removeUserRow(row);
                                loadDashboardStats();
                            }
                        }
                    }
            );

            // Logout
            librarianDashboard.addLogoutListener(e -> handleLogout());

            mainPanel.add(librarianDashboard, DASHBOARD_LIBRARIAN_PANEL);

            // Load initial dashboard data
            loadDashboardStats();
            loadPendingRequests();
            loadBooksIntoLibrarianDashboard();
            loadUsersIntoLibrarianDashboard();

            // Setup request actions (Approve / Reject / Hold)
            setupRequestActionsListener(librarian);
        }

        cardLayout.show(mainPanel, DASHBOARD_LIBRARIAN_PANEL);
    }

    private void loadDashboardStats() {
        new Thread(() -> {
            try {
                int totalBooks = bookService.countAll();
                int totalCopies = bookService.countTotalCopies();
                int availableCopies = bookService.countAvailable();
                int totalUsers = userService.countAll();
                int issuedCopies = totalCopies - availableCopies;

                SwingUtilities.invokeLater(() -> {
                    librarianDashboard.setStats(totalBooks, issuedCopies, totalUsers, availableCopies);
                });

            } catch (Exception ex) {
                SwingUtilities.invokeLater(() -> {
                    JOptionPane.showMessageDialog(librarianDashboard,
                            "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                });
            }
        }).start();
    }

    private void loadPendingRequests() {
        List<BookRequest> requests = bookRequestService.getPendingRequests();
        Object[][] rows = new Object[requests.size()][6];

        for (int i = 0; i < requests.size(); i++) {
            BookRequest req = requests.get(i);
            rows[i][0] = String.valueOf(req.getId());
            rows[i][1] = req.getReader().getFullName();
            rows[i][2] = req.getBook().getTitle();
            rows[i][3] = req.getRequestType();
            rows[i][4] = req.getCreatedAt().toLocalDate().toString();
            rows[i][5] = "⋯";
        }

        if (librarianDashboard != null) {
            librarianDashboard.setRequestsData(rows);
        }
    }

    private void setupRequestActionsListener(Librarian librarian) {
        if (librarianDashboard == null) return;

        librarianDashboard.setRequestActionsListener(new LibrarianDashboardPanel.RequestActionsListener() {
            @Override
            public void onApprove(String requestId, int row) {
                int librarianId = librarian.getId();

                // Get the request details
                BookRequest request = bookRequestService.getRequestById(Integer.parseInt(requestId));
                if (request == null) {
                    JOptionPane.showMessageDialog(
                            librarianDashboard,
                            "Request not found",
                            "Error",
                            JOptionPane.ERROR_MESSAGE
                    );
                    return;
                }

                // ✅ CHECK IF IT'S A RETURN REQUEST
                if ("RETURN".equals(request.getRequestType())) {
                    handleApproveReturnRequest(request, row, librarianId);
                    return;
                }

                // ✅ Show loan approval dialog (ask for duration and due date)
                LoanApprovalDialog.LoanApprovalResult loanResult = LoanApprovalDialog.showDialog(
                        (JFrame) SwingUtilities.getWindowAncestor(librarianDashboard)
                );

                if (loanResult == null) {
                    // User cancelled
                    return;
                }

                // Approve the request
                boolean approved = bookRequestService.approveRequest(Integer.parseInt(requestId), librarianId);
                if (!approved) {
                    JOptionPane.showMessageDialog(
                            librarianDashboard,
                            "Failed to approve request",
                            "Error",
                            JOptionPane.ERROR_MESSAGE
                    );
                    return;
                }

                // ✅ Create the loan with librarian-specified dates
                boolean loanCreated = loanService.issueBook(
                        request.getBookId(),
                        request.getReaderId(),
                        librarianId,
                        loanResult.getBorrowDate(),
                        loanResult.getDueDate(),
                        null
                );

                if (!loanCreated) {
                    JOptionPane.showMessageDialog(
                            librarianDashboard,
                            "Approved request but failed to create loan record",
                            "Error",
                            JOptionPane.ERROR_MESSAGE
                    );
                    return;
                }

                // ✅ Decrement available copies
                boolean copiesUpdated = bookService.decrementAvailableCopies(request.getBookId());
                if (!copiesUpdated) {
                    JOptionPane.showMessageDialog(
                            librarianDashboard,
                            "Approved request and created loan, but failed to update book inventory",
                            "Warning",
                            JOptionPane.WARNING_MESSAGE
                    );
                }

                JOptionPane.showMessageDialog(
                        librarianDashboard,
                        "✅ Request approved!\n\nBook issued for " + loanResult.getLoanDays() + " days\nDue Date: " + loanResult.getDueDate(),
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE
                );
                librarianDashboard.getRequestsTableModel().removeRow(row);

                // ✅ Refresh all dashboards
                loadDashboardStats();
                loadPendingRequests();
                loadBooksIntoLibrarianDashboard();

                if (readerDashboard != null && currentUser instanceof Reader) {
                    loadReaderDashboardData((Reader) currentUser);
                    loadReaderMyBooks((Reader) currentUser);
                }
            }

            @Override
            public void onReject(String requestId, int row) {
                int librarianId = librarian.getId();
                String reason = JOptionPane.showInputDialog(
                        librarianDashboard,
                        "Enter rejection reason (optional):"
                );

                boolean ok = bookRequestService.rejectRequest(
                        Integer.parseInt(requestId),
                        librarianId,
                        reason
                );

                if (ok) {
                    JOptionPane.showMessageDialog(
                            librarianDashboard,
                            "❌ Request rejected",
                            "Success",
                            JOptionPane.INFORMATION_MESSAGE
                    );
                    librarianDashboard.getRequestsTableModel().removeRow(row);
                    loadPendingRequests();
                } else {
                    JOptionPane.showMessageDialog(
                            librarianDashboard,
                            "Failed to reject request",
                            "Error",
                            JOptionPane.ERROR_MESSAGE
                    );
                }
            }

            @Override
            public void onHold(String requestId, int row) {
                int librarianId = librarian.getId();
                String input = JOptionPane.showInputDialog(
                        librarianDashboard,
                        "Enter hold-until date (yyyy-MM-dd):"
                );

                if (input == null || input.trim().isEmpty()) {
                    return;
                }

                try {
                    LocalDate holdDate = LocalDate.parse(input.trim());
                    boolean ok = bookRequestService.holdRequest(
                            Integer.parseInt(requestId),
                            librarianId,
                            holdDate
                    );

                    if (ok) {
                        JOptionPane.showMessageDialog(
                                librarianDashboard,
                                "⏸️ Request on hold until " + holdDate,
                                "Success",
                                JOptionPane.INFORMATION_MESSAGE
                        );
                        librarianDashboard.getRequestsTableModel().removeRow(row);
                        loadPendingRequests();
                    } else {
                        JOptionPane.showMessageDialog(
                                librarianDashboard,
                                "Failed to put request on hold",
                                "Error",
                                JOptionPane.ERROR_MESSAGE
                        );
                    }
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(
                            librarianDashboard,
                            "Invalid date format. Use yyyy-MM-dd.",
                            "Error",
                            JOptionPane.ERROR_MESSAGE
                    );
                }
            }
        });
    }

    private void handleApproveReturnRequest(BookRequest returnRequest, int row, int librarianId) {
        Loan activeLoan = loanService.findActiveLoanByBookId(returnRequest.getBookId());

        if (activeLoan == null) {
            JOptionPane.showMessageDialog(
                    librarianDashboard,
                    "No active loan found for this book",
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
            return;
        }

        // ✅ FIX: Load the book directly instead of relying on BookRequest.getBook()
        Book book = bookService.findById(returnRequest.getBookId());
        if (book == null) {
            JOptionPane.showMessageDialog(
                    librarianDashboard,
                    "Book not found",
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
            return;
        }

        double overdueFine = fineService.calculateFineForLoan(activeLoan);

        System.out.println("DEBUG: Return request ID=" + returnRequest.getId() +
                ", Book=" + book.getTitle() +  // ✅ NOW SAFE!
                ", LoanID=" + activeLoan.getLoanId() +
                ", OverdueFine=" + overdueFine);

        if (overdueFine > 0) {
            // ❌ SCENARIO 2: FINE EXISTS → PUT ON HOLD
            String message = String.format(
                    "📋 RETURN REQUEST WITH FINE\n\n" +
                            "Book: %s\n" +
                            "Overdue Fine: Rs %.2f\n\n" +
                            "Action: Put on HOLD until fine is paid\n" +
                            "Enter payment date (yyyy-MM-dd):",
                    book.getTitle(),  // ✅ NOW SAFE!
                    overdueFine
            );

            String paymentDateStr = JOptionPane.showInputDialog(librarianDashboard, message);

            if (paymentDateStr == null || paymentDateStr.isEmpty()) {
                return;
            }

            try {
                LocalDate paymentDate = LocalDate.parse(paymentDateStr);

                // Create fine record
                Fine fine = new Fine();
                fine.setLoanId(activeLoan.getLoanId());
                fine.setReaderId(returnRequest.getReaderId());
                fine.setAmount(overdueFine);
                fine.setStatus("UNPAID");
                boolean fineCreated = fineService.addFine(fine);

                if (!fineCreated) {
                    JOptionPane.showMessageDialog(
                            librarianDashboard,
                            "Failed to create fine record",
                            "Error",
                            JOptionPane.ERROR_MESSAGE
                    );
                    return;
                }

                // Put request on HOLD
                boolean held = bookRequestService.holdRequest(
                        returnRequest.getId(),
                        librarianId,
                        paymentDate
                );

                if (held) {
                    JOptionPane.showMessageDialog(
                            librarianDashboard,
                            "⏸ RETURN REQUEST ON HOLD\n\n" +
                                    "Fine: Rs " + String.format("%.2f", overdueFine) + "\n" +
                                    "Hold until: " + paymentDate + "\n\n" +
                                    "Notify reader about fine payment",
                            "Success",
                            JOptionPane.INFORMATION_MESSAGE
                    );
                    librarianDashboard.getRequestsTableModel().removeRow(row);
                    loadPendingRequests();
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(
                        librarianDashboard,
                        "Invalid date format. Use yyyy-MM-dd",
                        "Error",
                        JOptionPane.ERROR_MESSAGE
                );
            }
        } else {
            // ✅ SCENARIO 1: NO FINE → APPROVE IMMEDIATELY
            boolean approved = bookRequestService.approveRequest(returnRequest.getId(), librarianId);

            if (!approved) {
                JOptionPane.showMessageDialog(
                        librarianDashboard,
                        "Failed to approve return request",
                        "Error",
                        JOptionPane.ERROR_MESSAGE
                );
                return;
            }

            // Mark loan as returned
            boolean returned = loanService.returnBook(activeLoan.getLoanId());

            if (!returned) {
                JOptionPane.showMessageDialog(
                        librarianDashboard,
                        "Approved request but failed to mark loan as returned",
                        "Error",
                        JOptionPane.ERROR_MESSAGE
                );
                return;
            }

            // Increment available copies
            boolean copiesUpdated = bookService.incrementAvailableCopies(returnRequest.getBookId());

            if (!copiesUpdated) {
                JOptionPane.showMessageDialog(
                        librarianDashboard,
                        "Approved request but failed to update book inventory",
                        "Warning",
                        JOptionPane.WARNING_MESSAGE
                );
            }

            JOptionPane.showMessageDialog(
                    librarianDashboard,
                    "✅ RETURN APPROVED\n\n" +
                            "Book: " + book.getTitle() + "\n" +  // ✅ NOW SAFE!
                            "No overdue fine\n" +
                            "Book copies updated",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE
            );
            librarianDashboard.getRequestsTableModel().removeRow(row);

            // Refresh dashboards
            loadDashboardStats();
            loadPendingRequests();
            loadBooksIntoLibrarianDashboard();

            if (readerDashboard != null && currentUser instanceof Reader) {
                loadReaderDashboardData((Reader) currentUser);
                loadReaderMyBooks((Reader) currentUser);
            }
        }
    }

    private void loadBooksIntoLibrarianDashboard() {
        new Thread(() -> {
            try {
                List<Book> books = bookService.findAll();

                Object[][] rows = new Object[books.size()][6];
                for (int i = 0; i < books.size(); i++) {
                    Book b = books.get(i);
                    rows[i][0] = b.getIsbn();
                    rows[i][1] = b.getTitle();
                    rows[i][2] = b.getAuthor();
                    rows[i][3] = b.getCategory();
                    rows[i][4] = b.getAvailableCopies() > 0 ? "Available" : "Issued";
                    rows[i][5] = "Actions";
                }

                SwingUtilities.invokeLater(() -> {
                    librarianDashboard.setBooksData(rows);
                });

            } catch (Exception ex) {
                SwingUtilities.invokeLater(() -> {
                    JOptionPane.showMessageDialog(librarianDashboard,
                            "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                });
            }
        }).start();
    }

    private void loadUsersIntoLibrarianDashboard() {
        new Thread(() -> {
            try {
                List<User> users = userService.findAll();

                Object[][] rows = new Object[users.size()][6];
                for (int i = 0; i < users.size(); i++) {
                    User u = users.get(i);
                    rows[i][0] = u.getId();
                    rows[i][1] = u.getFullName();
                    rows[i][2] = u.getEmail() != null ? u.getEmail() : "-";
                    rows[i][3] = u.getRole();
                    rows[i][4] = u.getStatus();
                    rows[i][5] = "Actions";
                }

                SwingUtilities.invokeLater(() -> {
                    librarianDashboard.setUsersData(rows);
                });

            } catch (Exception ex) {
                SwingUtilities.invokeLater(() -> {
                    JOptionPane.showMessageDialog(librarianDashboard,
                            "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                });
            }
        }).start();
    }

    private void handleAddBook() {
        Book newBook = BookDialog.showAddDialog(librarianDashboard);
        if (newBook == null) return;

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

        loadBooksIntoLibrarianDashboard();
        loadDashboardStats();
        JOptionPane.showMessageDialog(
                librarianDashboard,
                "Book added successfully.",
                "Success",
                JOptionPane.INFORMATION_MESSAGE
        );
    }

    private void handleAddUser() {
        User newUser = UserDialog.showAddDialog(librarianDashboard);
        if (newUser == null) return;

        boolean ok = userService.add(newUser);
        if (!ok) {
            JOptionPane.showMessageDialog(
                    librarianDashboard,
                    "Failed to add user.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
            return;
        }

        loadUsersIntoLibrarianDashboard();
        loadDashboardStats();
        JOptionPane.showMessageDialog(
                librarianDashboard,
                "User added successfully.",
                "Success",
                JOptionPane.INFORMATION_MESSAGE
        );
    }

    private void handleSearchBook() {
        String query = librarianDashboard.getBooksSearchText().trim();

        new Thread(() -> {
            try {
                List<Book> books = query.isEmpty()
                        ? bookService.findAll()
                        : bookService.search(query);

                Object[][] rows = new Object[books.size()][6];
                for (int i = 0; i < books.size(); i++) {
                    Book b = books.get(i);
                    rows[i][0] = b.getIsbn();
                    rows[i][1] = b.getTitle();
                    rows[i][2] = b.getAuthor();
                    rows[i][3] = b.getCategory();
                    rows[i][4] = b.getAvailableCopies() > 0 ? "Available" : "Issued";
                    rows[i][5] = "Actions";
                }

                SwingUtilities.invokeLater(() -> {
                    librarianDashboard.setBooksData(rows);
                });

            } catch (Exception ex) {
                SwingUtilities.invokeLater(() -> {
                    JOptionPane.showMessageDialog(librarianDashboard,
                            "Search error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                });
            }
        }).start();
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
            rows[i][5] = "Actions";
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

    private void handleIssueBook(String bookIsbn, int row) {
        Book book = bookService.findByISBN(bookIsbn);
        if (book == null) {
            JOptionPane.showMessageDialog(
                    librarianDashboard,
                    "Book not found.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
            return;
        }

        if (book.getAvailableCopies() <= 0) {
            JOptionPane.showMessageDialog(
                    librarianDashboard,
                    "No copies available for this book.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
            return;
        }

        // Show issue dialog
        BookIssueDialog.IssueResult result = BookIssueDialog.showDialog(
                (Frame) SwingUtilities.getWindowAncestor(librarianDashboard),
                book
        );
        if (result == null) return;

        int librarianId = currentUser instanceof Librarian ? currentUser.getId() : 0;
        if (librarianId == 0) {
            JOptionPane.showMessageDialog(
                    librarianDashboard,
                    "Unable to identify librarian. Please logout and login again.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
            return;
        }

        boolean loanCreated = loanService.issueBook(
                book.getBookId(),
                result.getReader().getId(),
                librarianId,
                result.getBorrowDate(),
                result.getDueDate(),
                null
        );

        if (!loanCreated) {
            JOptionPane.showMessageDialog(
                    librarianDashboard,
                    "Failed to create loan record.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
            return;
        }

        boolean copiesUpdated = bookService.decrementAvailableCopies(book.getBookId());
        if (!copiesUpdated) {
            JOptionPane.showMessageDialog(
                    librarianDashboard,
                    "Failed to update book inventory.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
            return;
        }

        loadBooksIntoLibrarianDashboard();
        loadDashboardStats();

        JOptionPane.showMessageDialog(
                librarianDashboard,
                "Book issued successfully to " + result.getReader().getFullName() + "!\nDue Date: " + result.getDueDate(),
                "Success",
                JOptionPane.INFORMATION_MESSAGE
        );
    }

    private void handleReturnBook(String bookId, int row) {
        Book book = bookService.findById(Integer.parseInt(bookId));
        if (book == null) {
            JOptionPane.showMessageDialog(
                    librarianDashboard,
                    "Book not found.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
            return;
        }

        Loan activeLoan = loanService.findActiveLoanByBookId(book.getBookId());
        if (activeLoan == null) {
            JOptionPane.showMessageDialog(
                    librarianDashboard,
                    "No active loan found for this book.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
            return;
        }

        double fine = fineService.calculateFineForLoan(activeLoan);
        String message = "Return book: " + book.getTitle() + "?";
        if (fine > 0) {
            message = "This book is overdue! Fine amount: Rs " + String.format("%.2f", fine) + "\n\nReturn book?";
        }

        int confirm = JOptionPane.showConfirmDialog(
                librarianDashboard,
                message,
                "Return Book",
                JOptionPane.YES_NO_OPTION
        );
        if (confirm != JOptionPane.YES_OPTION) return;

        boolean loanReturned = loanService.returnBook(activeLoan.getLoanId());
        if (!loanReturned) {
            JOptionPane.showMessageDialog(
                    librarianDashboard,
                    "Failed to return book.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
            return;
        }

        bookService.incrementAvailableCopies(book.getBookId());

        if (fine > 0) {
            Fine fineRecord = new Fine(activeLoan.getLoanId(), activeLoan.getReaderId(), fine);
            fineService.addFine(fineRecord);
        }

        loadBooksIntoLibrarianDashboard();
        loadDashboardStats();

        String successMsg = "Book returned successfully!";
        if (fine > 0) {
            successMsg += "\nFine of Rs " + String.format("%.2f", fine) + " has been recorded.";
        }

        JOptionPane.showMessageDialog(
                librarianDashboard,
                successMsg,
                "Success",
                JOptionPane.INFORMATION_MESSAGE
        );
    }

    // ===================== READER DASHBOARD =====================

    private void showReaderDashboard(Reader reader) {
        if (readerDashboard == null) {
            readerDashboard = new ReaderDashboardPanel(reader);

            // Dashboard tab listener
            readerDashboard.addDashboardListener(e -> loadReaderDashboardData(reader));

            // Browse Books tab listener
            readerDashboard.addBrowseBooksListener(e -> loadBooksIntoReaderBrowse());

            // My Books tab listener
            readerDashboard.addMyBooksListener(e -> loadReaderMyBooks(reader));

            // History tab listener
            readerDashboard.addHistoryListener(e -> loadReaderHistory(reader));

            // Fines tab listener
            readerDashboard.addFinesListener(e -> loadReaderFines(reader));

            // Logout
            readerDashboard.addLogoutListener(e -> handleLogout());

            // Search in Browse Books
            readerDashboard.addBookSearchListener(e -> handleReaderBookSearch());

            readerDashboard.setCategoryFilterListener(category -> {
                List<Book> filteredBooks;
                if (category.equals("All")) {
                    filteredBooks = bookService.findAll();
                } else {
                    filteredBooks = bookService.findByCategory(category);
                }

                readerDashboard.clearBrowseBooks();
                for (Book book : filteredBooks) {
                    boolean available = book.getAvailableCopies() > 0;
                    readerDashboard.addBrowseBookCard(
                            book.getTitle(),
                            book.getAuthor(),
                            book.getCategory() != null ? book.getCategory() : "General",
                            available
                    );
                }
            });

            // Actions in "My Books" table
            readerDashboard.setMyBookActionsListener(new ReaderDashboardPanel.MyBookActionsListener() {
                @Override
                public void onView(String bookTitle, int row) {
                    Book book = bookService.findByTitle(bookTitle);
                    if (book == null) {
                        JOptionPane.showMessageDialog(
                                readerDashboard,
                                "Book not found",
                                "Error",
                                JOptionPane.ERROR_MESSAGE
                        );
                        return;
                    }
                    BookDialog.showDetailsDialog(readerDashboard, book);
                }

                @Override
                public void onRenew(String bookTitle, int row) {
                    handleRenewRequest(reader, bookTitle, row);
                }

                @Override
                public void onReturn(String bookTitle, int row) {
                    handleReaderReturn(reader, bookTitle, row);
                }

                @Override
                public void onRequestReturn(String bookTitle, int row, int loanId) {
                    Book book = bookService.findByTitle(bookTitle);
                    if (book == null) return;

                    boolean ok = bookRequestService.createRequest(
                            book.getBookId(),
                            reader.getId(),
                            "RETURN"
                    );

                    if (ok) {
                        JOptionPane.showMessageDialog(
                                readerDashboard,
                                "Return request submitted to librarian.",
                                "Success",
                                JOptionPane.INFORMATION_MESSAGE
                        );
                        loadReaderMyBooks(reader);
                    }
                }
            });

            // Borrow Books listener
            readerDashboard.setBrowseBookActionsListener(
                    new ReaderDashboardPanel.BrowseBookActionsListener() {
                        @Override
                        public void onView(String bookTitle) {
                            Book book = bookService.findByTitle(bookTitle);
                            if (book == null) {
                                JOptionPane.showMessageDialog(
                                        readerDashboard,
                                        "Book not found",
                                        "Error",
                                        JOptionPane.ERROR_MESSAGE
                                );
                                return;
                            }
                            BookDialog.showDetailsDialog(readerDashboard, book);
                        }

                        @Override
                        public void onBorrow(String bookTitle) {
                            handleBorrowRequest(reader, bookTitle);
                        }
                    }
            );

            mainPanel.add(readerDashboard, DASHBOARD_READER_PANEL);

            // Load initial data
            loadBooksIntoReaderBrowse();
            loadReaderDashboardData(reader);
            loadReaderMyBooks(reader);
            loadReaderHistory(reader);
            loadReaderFines(reader);
        }

        cardLayout.show(mainPanel, DASHBOARD_READER_PANEL);
    }

    private void loadReaderDashboardData(Reader reader) {
        int activeLoanCount = loanService.countActiveLoansByReaderId(reader.getId());
        int dueSoonCount = loanService.countDueSoonByReaderId(reader.getId(), 3);
        int overdueCount = loanService.countOverdueByReaderId(reader.getId());
        int totalBooksRead = loanService.countTotalReadByReaderId(reader.getId());
        double totalUnpaidFines = fineService.getTotalUnpaidFinesByReaderId(reader.getId());

        // ✅ Set dashboard stats (4 args + separate fines call)
        readerDashboard.setDashboardStats(activeLoanCount, dueSoonCount, overdueCount, totalBooksRead);
        readerDashboard.setUnpaidFines(totalUnpaidFines);

        // ✅ FIX: Use card-based API instead of rows array
        readerDashboard.clearBorrowedCards();
        List<Loan> activeLoans = loanService.findActiveLoansByReaderId(reader.getId());

        for (Loan loan : activeLoans) {
            double fine = fineService.calculateFineForLoan(loan);
            readerDashboard.addBorrowedCard(
                    loan.getBookTitle(),
                    loan.getBookAuthor(),
                    loan.getBorrowedDate(),
                    loan.getDueDate(),
                    fine,
                    loan.getLoanId()
            );
        }
    }

    private void loadBooksIntoReaderBrowse() {
        List<Book> books = bookService.findAll();
        readerDashboard.clearBrowseBooks();
        for (Book b : books) {
            boolean available = b.getAvailableCopies() > 0;
            readerDashboard.addBrowseBookCard(
                    b.getTitle(),
                    b.getAuthor(),
                    b.getCategory() != null ? b.getCategory() : "General",
                    available
            );
        }
    }

    private void loadReaderMyBooks(Reader reader) {
        List<Loan> loans = loanService.findActiveLoansByReaderId(reader.getId());
        Object[][] rows = new Object[loans.size()][7];

        for (int i = 0; i < loans.size(); i++) {
            Loan loan = loans.get(i);
            long daysLeft = java.time.temporal.ChronoUnit.DAYS.between(LocalDate.now(), loan.getDueDate());
            double fine = fineService.calculateFineForLoan(loan);

            rows[i][0] = loan.getBookTitle();
            rows[i][1] = loan.getBookAuthor();
            rows[i][2] = loan.getBorrowedDate().toString();
            rows[i][3] = loan.getDueDate().toString();
            rows[i][4] = daysLeft + " days";
            rows[i][5] = fine > 0 ? "Rs " + String.format("%.2f", fine) : "Rs 0";
            rows[i][6] = "Actions";
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
            rows[i][4] = "Returned";
        }

        readerDashboard.setHistoryData(rows);
    }

    private void loadReaderFines(Reader reader) {
        List<Fine> fines = fineService.findAllFinesByReaderId(reader.getId());
        Object[][] rows = new Object[fines.size()][5];

        for (int i = 0; i < fines.size(); i++) {
            Fine fine = fines.get(i);
            rows[i][0] = fine.getFineId();
            rows[i][1] = "Rs " + String.format("%.2f", fine.getAmount());
            rows[i][2] = fine.getStatus();
            rows[i][3] = fine.getCreatedDate().toString();
            rows[i][4] = fine.getPaidDate() != null ? fine.getPaidDate().toString() : "-";
        }

        readerDashboard.setFinesData(rows);
    }

    private void handleBorrowRequest(Reader reader, String bookTitle) {
        Book book = bookService.findByTitle(bookTitle);
        if (book == null) {
            JOptionPane.showMessageDialog(
                    readerDashboard,
                    "Book not found: " + bookTitle,
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
            return;
        }

        boolean ok = bookRequestService.createRequest(
                book.getBookId(),
                reader.getId(),
                "ISSUE"
        );

        if (ok) {
            JOptionPane.showMessageDialog(
                    readerDashboard,
                    "Request submitted to librarian for: " + bookTitle,
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE
            );
        } else {
            JOptionPane.showMessageDialog(
                    readerDashboard,
                    "Failed to submit request.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private void handleRenewRequest(Reader reader, String bookTitle, int row) {
        int confirm = JOptionPane.showConfirmDialog(
                readerDashboard,
                "Do you want to request renewal for:\n" + bookTitle + "?",
                "Renew Book",
                JOptionPane.YES_NO_OPTION
        );
        if (confirm != JOptionPane.YES_OPTION) return;

        Loan targetLoan = null;
        List<Loan> loans = loanService.findActiveLoansByReaderId(reader.getId());
        for (Loan loan : loans) {
            if (bookTitle.equals(loan.getBookTitle())) {
                targetLoan = loan;
                break;
            }
        }

        if (targetLoan == null) {
            JOptionPane.showMessageDialog(
                    readerDashboard,
                    "No active loan found for this book.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
            return;
        }

        boolean ok = bookRequestService.createRequest(
                targetLoan.getBookId(),
                reader.getId(),
                "RE_ISSUE"
        );

        if (ok) {
            JOptionPane.showMessageDialog(
                    readerDashboard,
                    "Renewal request submitted to librarian.",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE
            );
        } else {
            JOptionPane.showMessageDialog(
                    readerDashboard,
                    "Failed to submit renewal request.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private void handleReaderReturn(Reader reader, String bookTitle, int row) {
        int confirm = JOptionPane.showConfirmDialog(
                readerDashboard,
                "Are you sure you want to submit a return request for this book?\n" + bookTitle,
                "Return Book",
                JOptionPane.YES_NO_OPTION
        );
        if (confirm != JOptionPane.YES_OPTION) return;

        // Find the active loan
        Loan targetLoan = null;
        List<Loan> loans = loanService.findActiveLoansByReaderId(reader.getId());
        for (Loan loan : loans) {
            if (bookTitle.equals(loan.getBookTitle())) {
                targetLoan = loan;
                break;
            }
        }

        if (targetLoan == null) {
            JOptionPane.showMessageDialog(
                    readerDashboard,
                    "No active loan found for this book.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
            return;
        }

        // ✅ FIX: Create a RETURN request for the librarian to approve
        Book book = bookService.findByTitle(bookTitle);
        if (book == null) return;

        boolean ok = bookRequestService.createRequest(
                book.getBookId(),
                reader.getId(),
                "RETURN"  // ✅ This creates the request in librarian's dashboard!
        );

        if (ok) {
            JOptionPane.showMessageDialog(
                    readerDashboard,
                    "✅ Return request submitted to librarian.\n\n" +
                            "The librarian will review and approve your return.\n" +
                            "Any overdue fines will be calculated then.",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE
            );
            loadReaderMyBooks(reader);
        } else {
            JOptionPane.showMessageDialog(
                    readerDashboard,
                    "Failed to submit return request.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private void handleReaderBookSearch() {
        String query = readerDashboard.getBookSearchText().trim();
        List<Book> books;

        if (query.isEmpty()) {
            books = bookService.findAll();
        } else {
            books = bookService.search(query);
        }

        readerDashboard.clearBrowseBooks();
        for (Book b : books) {
            boolean available = b.getAvailableCopies() > 0;
            readerDashboard.addBrowseBookCard(
                    b.getTitle(),
                    b.getAuthor(),
                    b.getCategory() != null ? b.getCategory() : "General",
                    available
            );
        }
    }

    // ===================== SHARED =====================

    private void handleLogout() {
        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to logout?",
                "Confirm Logout",
                JOptionPane.YES_NO_OPTION
        );
        if (confirm != JOptionPane.YES_OPTION) return;

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

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new LibraryGUI().setVisible(true));
    }
}