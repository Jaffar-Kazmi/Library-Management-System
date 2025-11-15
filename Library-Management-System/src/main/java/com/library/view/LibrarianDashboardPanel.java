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

    // Books panel components
    private JPanel booksPanel;
    private JTextField bookSearchField;
    private JTable booksTable;
    private DefaultTableModel booksTableModel;

    // Users panel components
    private JPanel usersPanel;
    private JTextField userSearchField;
    private JTable usersTable;
    private DefaultTableModel usersTableModel;

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
        dashboardBtn = createSidebarButton("🏠  Dashboard", true);
        booksBtn = createSidebarButton("📖  Books", false);
        usersBtn = createSidebarButton("👥  Users", false);

        sidebarPanel.add(dashboardBtn);
        sidebarPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        sidebarPanel.add(booksBtn);
        sidebarPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        sidebarPanel.add(usersBtn);

        sidebarPanel.add(Box.createVerticalGlue());

        // Logout button at bottom
        logoutBtn = createSidebarButton("🚪  Logout", false);
        logoutBtn.setBackground(Theme.INDIGO);

        sidebarPanel.add(logoutBtn);
        sidebarPanel.add(Box.createRigidArea(new Dimension(0, 20)));
    }

    private JButton createSidebarButton(String text, boolean selected) {
        JButton button = new JButton(text);
        button.setFont(Theme.NORMAL_FONT);
        button.setForeground(Theme.VIOLET);
        button.setMaximumSize(new Dimension(230, 50));
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setHorizontalAlignment(SwingConstants.LEFT);
        button.setBorder(new EmptyBorder(10, 30, 10, 10));

        if (selected) {
            button.setOpaque(true);
            button.setBackground(Theme.CYAN);
        }

        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setOpaque(true);
                button.setBackground(Theme.INDIGO);
                button.setForeground(Theme.AQUA);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                if (!button.getText().equals(getSelectedButtonText())) {
                    button.setOpaque(false);
                    button.setForeground(Theme.VIOLET);
                }
            }
        });

        return button;
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

        // Header
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

        headerPanel.add(headerText);

        // Statistics cards
        JPanel statsPanel = new JPanel(new GridLayout(1, 4, 20, 0));
        statsPanel.setOpaque(false);

        statsPanel.add(createStatCard("Total Books", "1,234", "📚", new Color(106, 17, 203)));
        statsPanel.add(createStatCard("Issued Books", "234", "📤", new Color(37, 117, 252)));
        statsPanel.add(createStatCard("Total Users", "456", "👥", new Color(52, 211, 153)));
        statsPanel.add(createStatCard("Available", "1,000", "✅", new Color(251, 146, 60)));

        // Recent activity
        JPanel activityPanel = new JPanel(new BorderLayout(10, 10));
        activityPanel.setBackground(Theme.AQUA);
        activityPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel activityTitle = new JLabel("Recent Activity");
        activityTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        activityTitle.setForeground(Theme.VIOLET);

        String[] columns = {"Time", "Activity", "User", "Status"};
        Object[][] data = {
                {"10:30 AM", "Book Issued", "John Doe", "✅ Completed"},
                {"10:15 AM", "Book Returned", "Jane Smith", "✅ Completed"},
                {"09:45 AM", "New User Added", "Bob Johnson", "✅ Completed"},
                {"09:30 AM", "Book Updated", "System", "✅ Completed"},
                {"10:30 AM", "Book Issued", "John Doe", "✅ Completed"},
                {"10:15 AM", "Book Returned", "Jane Smith", "✅ Completed"},
                {"09:45 AM", "New User Added", "Bob Johnson", "✅ Completed"},
                {"09:30 AM", "Book Updated", "System", "✅ Completed"},
                {"10:30 AM", "Book Issued", "John Doe", "✅ Completed"},
                {"10:15 AM", "Book Returned", "Jane Smith", "✅ Completed"},
                {"09:45 AM", "New User Added", "Bob Johnson", "✅ Completed"},
                {"09:30 AM", "Book Updated", "System", "✅ Completed"},
                {"10:30 AM", "Book Issued", "John Doe", "✅ Completed"},
                {"10:15 AM", "Book Returned", "Jane Smith", "✅ Completed"},
                {"09:45 AM", "New User Added", "Bob Johnson", "✅ Completed"},
                {"09:30 AM", "Book Updated", "System", "✅ Completed"},
                {"10:30 AM", "Book Issued", "John Doe", "✅ Completed"},
                {"10:15 AM", "Book Returned", "Jane Smith", "✅ Completed"},
                {"09:45 AM", "New User Added", "Bob Johnson", "✅ Completed"},
                {"09:30 AM", "Book Updated", "System", "✅ Completed"}
        };

        DefaultTableModel activityModel = new DefaultTableModel(data, columns) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        JTable activityTable = createStyledTable(activityModel);
        JScrollPane activityScroll = new JScrollPane(activityTable);
        activityScroll.setBorder(null);

        activityPanel.add(activityTitle, BorderLayout.NORTH);
        activityPanel.add(activityScroll, BorderLayout.CENTER);

        // Layout
        JPanel contentPanel = new JPanel(new BorderLayout(20, 20));
        contentPanel.setOpaque(false);
        contentPanel.add(statsPanel, BorderLayout.NORTH);
        contentPanel.add(activityPanel, BorderLayout.CENTER);

        panel.add(headerPanel, BorderLayout.NORTH);
        panel.add(contentPanel, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createStatCard(String title, String value, String icon, Color color) {
        JPanel card = new JPanel();
        card.setLayout(new BorderLayout(15, 10));
        card.setBackground(Theme.AQUA);
        card.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Icon
        JLabel iconLabel = new JLabel(icon);
        iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 40));
        iconLabel.setOpaque(true);
        iconLabel.setBackground(new Color(color.getRed(), color.getGreen(), color.getBlue(), 30));
        iconLabel.setPreferredSize(new Dimension(70, 70));
        iconLabel.setHorizontalAlignment(SwingConstants.CENTER);

        // Text
        JPanel textPanel = new JPanel();
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
        textPanel.setOpaque(false);

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(Theme.SUB_HEADER_FONT);
        titleLabel.setForeground(new Color(120, 120, 120));

        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        valueLabel.setForeground(Theme.VIOLET);

        textPanel.add(titleLabel);
        textPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        textPanel.add(valueLabel);

        card.add(iconLabel, BorderLayout.WEST);
        card.add(textPanel, BorderLayout.CENTER);

        return card;
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

        JButton addBookBtn = createActionButton("➕ Add Book", Theme.INDIGO);

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

        // Sample data
        booksTableModel.addRow(new Object[]{"BK001", "The Great Gatsby", "F. Scott Fitzgerald", "Fiction", "Available", "⋮"});
        booksTableModel.addRow(new Object[]{"BK002", "To Kill a Mockingbird", "Harper Lee", "Fiction", "Issued", "⋮"});
        booksTableModel.addRow(new Object[]{"BK003", "1984", "George Orwell", "Fiction", "Available", "⋮"});
        booksTableModel.addRow(new Object[]{"BK004", "Pride and Prejudice", "Jane Austen", "Romance", "Available", "⋮"});
        booksTableModel.addRow(new Object[]{"BK005", "The Catcher in the Rye", "J.D. Salinger", "Fiction", "Issued", "⋮"});

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

        JButton addUserBtn = createActionButton("➕ Add User", Theme.INDIGO);

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

        // Sample data
        usersTableModel.addRow(new Object[]{"U001", "John Doe", "john@email.com", "Member", "Active", "⋮"});
        usersTableModel.addRow(new Object[]{"U002", "Jane Smith", "jane@email.com", "Member", "Active", "⋮"});
        usersTableModel.addRow(new Object[]{"U003", "Bob Johnson", "bob@email.com", "Premium", "Active", "⋮"});
        usersTableModel.addRow(new Object[]{"U004", "Alice Williams", "alice@email.com", "Member", "Inactive", "⋮"});
        usersTableModel.addRow(new Object[]{"U005", "Charlie Brown", "charlie@email.com", "Premium", "Active", "⋮"});

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

        JMenuItem viewDetails = createMenuItem("👁️ View Details", new Color(37, 117, 252));
        JMenuItem editBook = createMenuItem("✏️ Edit Book", new Color(251, 146, 60));
        JMenuItem deleteBook = createMenuItem("🗑️ Delete Book", new Color(220, 53, 69));

        popup.add(viewDetails);
        popup.add(editBook);
        popup.addSeparator();

        if (status.equals("Available")) {
            JMenuItem issueBook = createMenuItem("📤 Issue Book", new Color(52, 211, 153));
            popup.add(issueBook);
            issueBook.addActionListener(e -> handleIssueBook(bookId, bookTitle, row));
        } else if (status.equals("Issued")) {
            JMenuItem returnBook = createMenuItem("📥 Return Book", new Color(106, 17, 203));
            popup.add(returnBook);
            returnBook.addActionListener(e -> handleReturnBook(bookId, bookTitle, row));
        }

        popup.addSeparator();
        popup.add(deleteBook);

        viewDetails.addActionListener(e -> handleViewBookDetails(bookId, bookTitle));
        editBook.addActionListener(e -> handleEditBook(bookId, bookTitle));
        deleteBook.addActionListener(e -> handleDeleteBook(bookId, bookTitle, row));

        popup.show(table, x, y);
    }

    private void showUserActionsMenu(JTable table, int row, int x, int y) {
        JPopupMenu popup = new JPopupMenu();
        popup.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200), 1));

        String userId = table.getValueAt(row, 0).toString();
        String userName = table.getValueAt(row, 1).toString();

        JMenuItem viewDetails = createMenuItem("👁️ View Details", new Color(37, 117, 252));
        JMenuItem editUser = createMenuItem("✏️ Edit User", new Color(251, 146, 60));
        JMenuItem deleteUser = createMenuItem("🗑️ Delete User", new Color(220, 53, 69));

        popup.add(viewDetails);
        popup.add(editUser);
        popup.addSeparator();
        popup.add(deleteUser);

        viewDetails.addActionListener(e -> handleViewUserDetails(userId, userName));
        editUser.addActionListener(e -> handleEditUser(userId, userName));
        deleteUser.addActionListener(e -> handleDeleteUser(userId, userName, row));

        popup.show(table, x, y);
    }

    private JMenuItem createMenuItem(String text, Color iconColor) {
        JMenuItem item = new JMenuItem(text);
        item.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        item.setBorder(new EmptyBorder(8, 10, 8, 10));
        item.setBackground(Color.WHITE);
        item.setCursor(new Cursor(Cursor.HAND_CURSOR));

        item.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                item.setBackground(new Color(245, 245, 250));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                item.setBackground(Color.WHITE);
            }
        });

        return item;
    }

    private void handleViewBookDetails(String bookId, String bookTitle) {
        JOptionPane.showMessageDialog(this,
                "Viewing details for:\n\nBook ID: " + bookId + "\nTitle: " + bookTitle,
                "Book Details",
                JOptionPane.INFORMATION_MESSAGE);
    }

    private void handleEditBook(String bookId, String bookTitle) {
        JOptionPane.showMessageDialog(this,
                "Opening edit dialog for:\n\nBook ID: " + bookId + "\nTitle: " + bookTitle,
                "Edit Book",
                JOptionPane.INFORMATION_MESSAGE);
    }

    private void handleDeleteBook(String bookId, String bookTitle, int row) {
        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete this book?\n\n" + bookTitle,
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            booksTableModel.removeRow(row);
            JOptionPane.showMessageDialog(this, "Book deleted successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void handleIssueBook(String bookId, String bookTitle, int row) {
        String userId = JOptionPane.showInputDialog(this,
                "Enter User ID to issue this book:\n\n" + bookTitle,
                "Issue Book",
                JOptionPane.QUESTION_MESSAGE);

        if (userId != null && !userId.trim().isEmpty()) {
            booksTableModel.setValueAt("Issued", row, 4);
            JOptionPane.showMessageDialog(this,
                    "Book issued successfully to User ID: " + userId,
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void handleReturnBook(String bookId, String bookTitle, int row) {
        int confirm = JOptionPane.showConfirmDialog(this,
                "Mark this book as returned?\n\n" + bookTitle,
                "Return Book",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            booksTableModel.setValueAt("Available", row, 4);
            JOptionPane.showMessageDialog(this, "Book returned successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void handleViewUserDetails(String userId, String userName) {
        JOptionPane.showMessageDialog(this,
                "Viewing details for:\n\nUser ID: " + userId + "\nName: " + userName,
                "User Details",
                JOptionPane.INFORMATION_MESSAGE);
    }

    private void handleEditUser(String userId, String userName) {
        JOptionPane.showMessageDialog(this,
                "Opening edit dialog for:\n\nUser ID: " + userId + "\nName: " + userName,
                "Edit User",
                JOptionPane.INFORMATION_MESSAGE);
    }

    private void handleDeleteUser(String userId, String userName, int row) {
        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete this user?\n\n" + userName,
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            usersTableModel.removeRow(row);
            JOptionPane.showMessageDialog(this, "User deleted successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private JButton createActionButton(String text, Color color) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 13));
        button.setForeground(Color.WHITE);
        button.setBackground(color);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setBorder(new EmptyBorder(10, 20, 10, 20));

        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(color.darker());
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(color);
            }
        });

        return button;
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

    // Public methods for adding listeners
    public void addDashboardListener(ActionListener listener) {
        dashboardBtn.addActionListener(listener);
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

    public void addBookSearchListener(ActionListener listener) {
        bookSearchField.addActionListener(listener);
    }

    public void addUserSearchListener(ActionListener listener) {
        userSearchField.addActionListener(listener);
    }

    public Librarian getLibrarian() {
        return librarian;
    }

    public DefaultTableModel getBooksTableModel() {
        return booksTableModel;
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
}