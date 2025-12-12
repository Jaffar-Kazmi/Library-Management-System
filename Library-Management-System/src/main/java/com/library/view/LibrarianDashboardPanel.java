package com.library.view;

import com.library.model.Librarian;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class LibrarianDashboardPanel extends JPanel {

    private Librarian librarian;

    // Main layout components
    private JPanel sidebarPanel;
    private JPanel mainContentPanel;
    private CardLayout cardLayout;

    // Sidebar buttons
    private JButton dashboardBtn;
    private JButton booksBtn;
    private JButton usersBtn;
    private JButton logoutBtn;


    // Dashboard components
    private JPanel dashboardPanel;
    private DefaultTableModel requestsTableModel;
    private JLabel totalBooksLabel;
    private JLabel issuedBooksLabel;
    private JLabel totalUsersLabel;
    private JLabel availableBooksLabel;

    // Books panel components
    private JPanel booksPanel;
    private JTextField bookSearchField;
    private JButton addBookBtn;
    private JTable booksTable;
    private DefaultTableModel booksTableModel;
    private BookActionsListener bookActionsListener;

    // Users panel components
    private JPanel usersPanel;
    private JTextField userSearchField;
    private JButton addUserBtn;
    private JTable usersTable;
    private DefaultTableModel usersTableModel;
    private UserActionsListener userActionsListener;

    public LibrarianDashboardPanel(Librarian librarian) {
        this.librarian = librarian;
        setLayout(new BorderLayout());
        setBackground(Theme.AQUA);

        initializeComponents();
        setupLayout();
    }

    private void initializeComponents() {
        // Initialize card layout
        cardLayout = new CardLayout();
        mainContentPanel = new JPanel(cardLayout);
        mainContentPanel.setBackground(Theme.CYAN);

        // Create sidebar
        createSidebar();

        // Create panels
        dashboardPanel = createDashboardPanel();
        booksPanel = createBooksPanel();
        usersPanel = createUsersPanel();

        // Add panels to card layout
        mainContentPanel.add(dashboardPanel, "DASHBOARD");
        mainContentPanel.add(booksPanel, "BOOKS");
        mainContentPanel.add(usersPanel, "USERS");
    }

    private void createSidebar() {
        sidebarPanel = new GradientPanel();
        sidebarPanel.setLayout(new BoxLayout(sidebarPanel, BoxLayout.Y_AXIS));
        sidebarPanel.setPreferredSize(new Dimension(250, getHeight()));

        // Logo/Header
        JPanel logoPanel = new JPanel();
        logoPanel.setOpaque(false);
        logoPanel.setMaximumSize(new Dimension(250, 100));
        logoPanel.setLayout(new FlowLayout(FlowLayout.CENTER));

        JLabel logoLabel = new JLabel("📚 Good Books");
        logoLabel.setFont(Theme.HEADER_FONT);
        logoLabel.setForeground(Theme.VIOLET);
        logoPanel.add(logoLabel);

        sidebarPanel.add(Box.createRigidArea(new Dimension(0, 30)));
        sidebarPanel.add(logoPanel);
        sidebarPanel.add(Box.createRigidArea(new Dimension(0, 40)));

        // Menu buttons
        dashboardBtn = UIComponents.createSidebarButton("🏠  Dashboard", true, this::getSelectedButtonText);
        booksBtn = UIComponents.createSidebarButton("📖  Books", false, this::getSelectedButtonText);
        usersBtn = UIComponents.createSidebarButton("👥  Users", false, this::getSelectedButtonText);

        sidebarPanel.add(dashboardBtn);
        sidebarPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        sidebarPanel.add(booksBtn);
        sidebarPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        sidebarPanel.add(usersBtn);

        sidebarPanel.add(Box.createVerticalGlue());

        // Logout button at bottom
        logoutBtn = UIComponents.createSidebarButton("🚪  Logout", false, this::getSelectedButtonText);
        logoutBtn.setBackground(Theme.INDIGO);

        sidebarPanel.add(logoutBtn);
        sidebarPanel.add(Box.createRigidArea(new Dimension(0, 20)));
    }

    private String selectedButton = "🏠  Dashboard";

    private String getSelectedButtonText() {
        return selectedButton;
    }

    private void setSelectedButton(JButton button) {
        // Reset all buttons
        dashboardBtn.setOpaque(false);
        booksBtn.setOpaque(false);
        usersBtn.setOpaque(false);

        // Set selected button
        button.setOpaque(true);
        button.setBackground(Theme.INDIGO);
        selectedButton = button.getText();
    }

    private JPanel createDashboardPanel() {
        JPanel panel = new JPanel(new BorderLayout(20, 20));
        panel.setBackground(Theme.AQUA);
        panel.setBorder(new EmptyBorder(30, 30, 30, 30));

        // ===== Header =====
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);

        JLabel titleLabel = new JLabel("Librarian Dashboard");
        titleLabel.setFont(Theme.HEADER_FONT);
        titleLabel.setForeground(Theme.VIOLET);

        JLabel welcomeLabel = new JLabel("Welcome back, " + librarian.getFullName());
        welcomeLabel.setFont(Theme.SUB_HEADER_FONT);
        welcomeLabel.setForeground(new Color(120, 120, 120));

        JPanel headerText = new JPanel();
        headerText.setLayout(new BoxLayout(headerText, BoxLayout.Y_AXIS));
        headerText.setOpaque(false);
        headerText.add(titleLabel);
        headerText.add(welcomeLabel);

        headerPanel.add(headerText, BorderLayout.WEST);

        // ===== Stats cards =====
        JPanel statsPanel = new JPanel(new GridLayout(1, 4, 20, 0));
        statsPanel.setOpaque(false);

        JPanel totalBooksCard = UIComponents.createStatCard("Total Books", "0", "📚", new Color(106, 17, 203));
        totalBooksLabel = findNumberLabelInCard(totalBooksCard);

        JPanel issuedBooksCard = UIComponents.createStatCard("Issued Books", "0", "📤", new Color(37, 117, 252));
        issuedBooksLabel = findNumberLabelInCard(issuedBooksCard);

        JPanel totalUsersCard = UIComponents.createStatCard("Total Users", "0", "👥", new Color(52, 211, 153));
        totalUsersLabel = findNumberLabelInCard(totalUsersCard);

        JPanel availableBooksCard = UIComponents.createStatCard("Available", "0", "✅", new Color(251, 146, 60));
        availableBooksLabel = findNumberLabelInCard(availableBooksCard);

        statsPanel.add(totalBooksCard);
        statsPanel.add(issuedBooksCard);
        statsPanel.add(totalUsersCard);
        statsPanel.add(availableBooksCard);

        // ===== Requests table (replacing Recent Activity) =====
        JPanel requestsPanel = new JPanel(new BorderLayout(10, 10));
        requestsPanel.setBackground(Theme.AQUA);
        requestsPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel requestsTitle = new JLabel("📋 Book Requests Queue");
        requestsTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        requestsTitle.setForeground(Theme.VIOLET);

        String[] columns = {"ID", "Reader", "Book Title", "Type", "Requested", "Actions"};
        requestsTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        JTable requestsTable = createStyledTable(requestsTableModel);

        // Actions column popup
        requestsTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = requestsTable.rowAtPoint(e.getPoint());
                int col = requestsTable.columnAtPoint(e.getPoint());

                if (row >= 0 && col == requestsTable.getColumnCount() - 1) {
                    showRequestActionsMenu(requestsTable, row, e.getX(), e.getY());
                }
            }
        });

        JScrollPane requestsScroll = new JScrollPane(requestsTable);
        requestsScroll.setBorder(null);

        requestsPanel.add(requestsTitle, BorderLayout.NORTH);
        requestsPanel.add(requestsScroll, BorderLayout.CENTER);

        // ===== Layout content =====
        JPanel contentPanel = new JPanel(new BorderLayout(20, 20));
        contentPanel.setOpaque(false);
        contentPanel.add(statsPanel, BorderLayout.NORTH);
        contentPanel.add(requestsPanel, BorderLayout.CENTER);

        panel.add(headerPanel, BorderLayout.NORTH);
        panel.add(contentPanel, BorderLayout.CENTER);

        return panel;
    }

    private JLabel findNumberLabelInCard(JPanel card) {
        for (Component c : card.getComponents()) {
            if (c instanceof JLabel) {
                JLabel label = (JLabel) c;
                String text = label.getText();
                if (text != null && (text.matches("\\d+") || text.equals("0"))) {
                    return label;
                }
            } else if (c instanceof JPanel) {
                JLabel found = findNumberLabelInCard((JPanel) c);
                if (found != null) return found;
            }
        }
        return new JLabel("0"); // fallback
    }

    private JLabel extractLabelFromCard(JPanel card, int labelIndex) {
        // Helper to find label in the stat card
        Component[] components = card.getComponents();
        int labelCount = 0;
        for (Component c : components) {
            if (c instanceof JLabel) {
                if (labelCount == labelIndex) {
                    return (JLabel) c;
                }
                labelCount++;
            }
        }
        return new JLabel("0"); // fallback
    }

    private JPanel createBooksPanel() {
        JPanel panel = new JPanel(new BorderLayout(20, 20));
        panel.setBackground(Theme.AQUA);
        panel.setBorder(new EmptyBorder(30, 30, 30, 30));

        // Header with search
        JPanel headerPanel = new JPanel(new BorderLayout(20, 0));
        headerPanel.setOpaque(false);

        JLabel titleLabel = new JLabel("Books Management");
        titleLabel.setFont(Theme.HEADER_FONT);
        titleLabel.setForeground(Theme.VIOLET);

        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        searchPanel.setOpaque(false);

        bookSearchField = new JTextField(20);
        bookSearchField.setFont(Theme.SUB_HEADER_FONT);
        bookSearchField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
                new EmptyBorder(8, 12, 8, 12)
        ));

        addBookBtn = UIComponents.createActionButton("➕ Add Book", Theme.INDIGO);

        searchPanel.add(new JLabel("🔍"));
        searchPanel.add(bookSearchField);
        searchPanel.add(addBookBtn);

        headerPanel.add(titleLabel, BorderLayout.WEST);
        headerPanel.add(searchPanel, BorderLayout.EAST);

        // Books table
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setOpaque(false);
        tablePanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        String[] columns = {"ISBN", "Book Title", "Author", "Category", "Status", "Actions"};
        booksTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        booksTable = createStyledTable(booksTableModel);

        // Add mouse listener for actions column
        booksTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = booksTable.rowAtPoint(e.getPoint());
                int col = booksTable.columnAtPoint(e.getPoint());

                if (row >= 0 && col == booksTable.getColumnCount() - 1) {
                    showBookActionsMenu(booksTable, row, e.getX(), e.getY());
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(booksTable);
        scrollPane.setBorder(null);

        tablePanel.add(scrollPane, BorderLayout.CENTER);

        panel.add(headerPanel, BorderLayout.NORTH);
        panel.add(tablePanel, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createUsersPanel() {
        JPanel panel = new JPanel(new BorderLayout(20, 20));
        panel.setBackground(Theme.AQUA);
        panel.setBorder(new EmptyBorder(30, 30, 30, 30));

        // Header with search
        JPanel headerPanel = new JPanel(new BorderLayout(20, 0));
        headerPanel.setOpaque(false);

        JLabel titleLabel = new JLabel("Users Management");
        titleLabel.setFont(Theme.HEADER_FONT);
        titleLabel.setForeground(Theme.VIOLET);

        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        searchPanel.setOpaque(false);

        userSearchField = new JTextField(20);
        userSearchField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        userSearchField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
                new EmptyBorder(8, 12, 8, 12)
        ));

        addUserBtn = UIComponents.createActionButton("➕ Add User", Theme.INDIGO);

        searchPanel.add(new JLabel("🔍"));
        searchPanel.add(userSearchField);
        searchPanel.add(addUserBtn);

        headerPanel.add(titleLabel, BorderLayout.WEST);
        headerPanel.add(searchPanel, BorderLayout.EAST);

        // Users table
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setOpaque(false);
        tablePanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        String[] columns = {"ID", "Name", "Email", "Role", "Status", "Actions"};
        usersTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        usersTable = createStyledTable(usersTableModel);

        // Add mouse listener for actions column
        usersTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = usersTable.rowAtPoint(e.getPoint());
                int col = usersTable.columnAtPoint(e.getPoint());

                if (row >= 0 && col == usersTable.getColumnCount() - 1) {
                    showUserActionsMenu(usersTable, row, e.getX(), e.getY());
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(usersTable);
        scrollPane.setBorder(null);

        tablePanel.add(scrollPane, BorderLayout.CENTER);

        panel.add(headerPanel, BorderLayout.NORTH);
        panel.add(tablePanel, BorderLayout.CENTER);

        return panel;
    }

    private JTable createStyledTable(DefaultTableModel model) {
        JTable table = new JTable(model);
        table.setFont(Theme.SUB_HEADER_FONT);
        table.setRowHeight(40);
        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0, 0));
        table.setSelectionBackground(Theme.CYAN);
        table.setSelectionForeground(Theme.VIOLET);

        // Header styling
        table.getTableHeader().setFont(Theme.SUB_HEADER_FONT);
        table.getTableHeader().setBackground(Theme.INDIGO);
        table.getTableHeader().setForeground(Theme.AQUA);
        table.getTableHeader().setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, new Color(230, 230, 230)));
        table.getTableHeader().setPreferredSize(new Dimension(0, 45));

        // Alternating row colors
        DefaultTableCellRenderer renderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                                                           boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (!isSelected) {
                    c.setBackground(row % 2 == 0 ? Color.WHITE : Theme.AQUA);
                }
                setBorder(new EmptyBorder(5, 10, 5, 10));
                return c;
            }
        };

        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(renderer);
        }

        return table;
    }

    private void showBookActionsMenu(JTable table, int row, int x, int y) {
        JPopupMenu popup = new JPopupMenu();
        popup.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200), 1));

        String bookId = table.getValueAt(row, 0).toString();
        String bookTitle = table.getValueAt(row, 1).toString();
        String status = table.getValueAt(row, 4).toString();

        JMenuItem viewDetails = UIComponents.createMenuItem("👁️ View Details", new Color(37, 117, 252));
        JMenuItem editBook = UIComponents.createMenuItem("✏️ Edit Book", new Color(251, 146, 60));
        JMenuItem deleteBook = UIComponents.createMenuItem("🗑️ Delete Book", new Color(220, 53, 69));

        popup.add(viewDetails);
        popup.add(editBook);
        popup.addSeparator();

        viewDetails.addActionListener(e -> {
            if (bookActionsListener != null) bookActionsListener.onView(bookId, row);
        });
        editBook.addActionListener(e -> {
            if (bookActionsListener != null) bookActionsListener.onEdit(bookId, row);
        });
        deleteBook.addActionListener(e -> {
            if (bookActionsListener != null) bookActionsListener.onDelete(bookId, row);
        });

        if (status.equals("Available")) {
            JMenuItem issueBook = UIComponents.createMenuItem("\uD83D\uDCE4 Issue Book", new Color(52, 211, 153));
            popup.add(issueBook);
            issueBook.addActionListener(e -> {
                if (bookActionsListener != null) bookActionsListener.onIssue(bookId, row);
            });
        } else if (status.equals("Issued")) {
            JMenuItem returnBook = UIComponents.createMenuItem("📥 Return Book", new Color(106, 17, 203));
            popup.add(returnBook);
            returnBook.addActionListener(e -> {
                if (bookActionsListener != null) bookActionsListener.onReturn(bookId, row);
            });
        }

        popup.addSeparator();
        popup.add(deleteBook);

        popup.show(table, x, y);
    }

    private void showUserActionsMenu(JTable table, int row, int x, int y) {
        JPopupMenu popup = new JPopupMenu();
        popup.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200), 1));

        String userId = table.getValueAt(row, 0).toString();
        String userName = table.getValueAt(row, 1).toString();

        JMenuItem viewDetails = UIComponents.createMenuItem("👁️ View Details", new Color(37, 117, 252));
        JMenuItem editUser = UIComponents.createMenuItem("✏️ Edit User", new Color(251, 146, 60));
        JMenuItem deleteUser = UIComponents.createMenuItem("🗑️ Delete User", new Color(220, 53, 69));

        popup.add(viewDetails);
        popup.add(editUser);
        popup.addSeparator();
        popup.add(deleteUser);

        viewDetails.addActionListener(e -> {
            if (userActionsListener != null) userActionsListener.onView(userId, row);
        });

        editUser.addActionListener(e -> {
            if (userActionsListener != null) userActionsListener.onEdit(userId, row);
        });

        deleteUser.addActionListener(e -> {
            if (userActionsListener != null) userActionsListener.onDelete(userId, row);
        });

        popup.show(table, x, y);
    }

    private void setupLayout() {
        add(sidebarPanel, BorderLayout.WEST);
        add(mainContentPanel, BorderLayout.CENTER);

        // Set up navigation
        dashboardBtn.addActionListener(e -> {
            setSelectedButton(dashboardBtn);
            cardLayout.show(mainContentPanel, "DASHBOARD");
        });

        booksBtn.addActionListener(e -> {
            setSelectedButton(booksBtn);
            cardLayout.show(mainContentPanel, "BOOKS");
        });

        usersBtn.addActionListener(e -> {
            setSelectedButton(usersBtn);
            cardLayout.show(mainContentPanel, "USERS");
        });
    }

    private void showRequestActionsMenu(JTable table, int row, int x, int y) {
        JPopupMenu popup = new JPopupMenu();
        popup.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200), 1));

        String requestId = table.getValueAt(row, 0).toString();  // Adjust to match your row ID column

        JMenuItem approveRequest = UIComponents.createMenuItem("✅ Approve", new Color(52, 211, 153));
        JMenuItem rejectRequest = UIComponents.createMenuItem("❌ Reject", new Color(220, 53, 69));
        JMenuItem holdRequest = UIComponents.createMenuItem("⏸️ Put on Hold", new Color(251, 146, 60));

        approveRequest.addActionListener(e -> {
            if (requestActionsListener != null) requestActionsListener.onApprove(requestId, row);
        });

        rejectRequest.addActionListener(e -> {
            if (requestActionsListener != null) requestActionsListener.onReject(requestId, row);
        });

        holdRequest.addActionListener(e -> {
            if (requestActionsListener != null) requestActionsListener.onHold(requestId, row);
        });

        popup.add(approveRequest);
        popup.add(rejectRequest);
        popup.add(holdRequest);

        popup.show(table, x, y);
    }

    // Public methods for adding listeners
    public void addDashboardListener(ActionListener listener) {
        dashboardBtn.addActionListener(listener);
    }

    public void addAddBookButtonListener(ActionListener listener) {
        addBookBtn.addActionListener(listener);
    }

    public void addSearchBookButtonListener(ActionListener listener) {
        bookSearchField.addActionListener(listener);
    }

    public void addUserSearchListener(ActionListener listener) {
        userSearchField.addActionListener(listener);
    }

    public void addAddUserButtonListener(ActionListener listener) {
        addUserBtn.addActionListener(listener);
    }

    public void addBooksListener(ActionListener listener) {
        booksBtn.addActionListener(listener);
    }

    public void addUsersListener(ActionListener listener) {
        usersBtn.addActionListener(listener);
    }

    public void addLogoutListener(ActionListener listener) {
        logoutBtn.addActionListener(listener);
    }

    public Librarian getLibrarian() {
        return librarian;
    }

    public void setBooksData(Object[][] rows) {
        booksTableModel.setRowCount(0);
        for (Object[] row : rows) {
            booksTableModel.addRow(row);
        }
    }

    public void clearBooks() {
        booksTableModel.setRowCount(0);
    }

    public void addBookRow(Object[] row) {
        booksTableModel.addRow(row);
    }

    public void removeBookRow(int row) {
        if (row >= 0 && row < booksTableModel.getRowCount()) {
            booksTableModel.removeRow(row);
        }
    }

    public void markBookIssued(int row) {
        if (row >= 0 && row < booksTableModel.getRowCount()) {
            booksTableModel.setValueAt("Issued", row, 4); // column 4 = Status
        }
    }

    public void markBookAvailable(int row) {
        if (row >= 0 && row < booksTableModel.getRowCount()) {
            booksTableModel.setValueAt("Available", row, 4);
        }
    }

    public DefaultTableModel getBooksTableModel() {
        return booksTableModel;
    }

    public void setUsersData(Object[][] rows) {
        usersTableModel.setRowCount(0);
        for (Object[] row : rows) {
            usersTableModel.addRow(row);
        }
    }

    public void clearUsers() {
        usersTableModel.setRowCount(0);
    }

    public void addUserRow(Object[] row) {
        usersTableModel.addRow(row);
    }

    public void removeUserRow(int row) {
        if (row >= 0 && row < usersTableModel.getRowCount()) {
            usersTableModel.removeRow(row);
        }
    }

    public DefaultTableModel getUsersTableModel() {
        return usersTableModel;
    }

    public JTable getBooksTable() {
        return booksTable;
    }

    public JTable getUsersTable() {
        return usersTable;
    }

    public String getBooksSearchText() {
        return bookSearchField.getText();
    }

    public String getUserSearchText() {
        return userSearchField.getText();
    }

    public void setBookActionsListener(BookActionsListener listener) {
        this.bookActionsListener = listener;
    }

    public void setUserActionsListener(UserActionsListener listener) {
        this.userActionsListener = listener;
    }

    public void setStats(int totalBooks, int issuedBooks, int totalUsers, int availableBooks) {
        if (totalBooksLabel != null) totalBooksLabel.setText(String.valueOf(totalBooks));
        if (issuedBooksLabel != null) issuedBooksLabel.setText(String.valueOf(issuedBooks));
        if (totalUsersLabel != null) totalUsersLabel.setText(String.valueOf(totalUsers));
        if (availableBooksLabel != null) availableBooksLabel.setText(String.valueOf(availableBooks));
    }

    public DefaultTableModel getRequestsTableModel() {
        return requestsTableModel;
    }

    public void setRequestsData(Object[][] rows) {
        requestsTableModel.setRowCount(0);
        for (Object[] row : rows) {
            requestsTableModel.addRow(row);
        }
    }

    public interface BookActionsListener {
        void onView(String bookId, int row);
        void onEdit(String bookId, int row);
        void onDelete(String bookId, int row);
        void onIssue(String bookId, int row);
        void onReturn(String bookId, int row);
    }

    public interface UserActionsListener {
        void onView(String userId, int row);
        void onEdit(String userId, int row);
        void onDelete(String userId, int row);
    }

    private RequestActionsListener requestActionsListener;

    public interface RequestActionsListener {
        void onApprove(String requestId, int row);
        void onReject(String requestId, int row);
        void onHold(String requestId, int row);
    }

    public void setRequestActionsListener(RequestActionsListener listener) {
        this.requestActionsListener = listener;
    }
}