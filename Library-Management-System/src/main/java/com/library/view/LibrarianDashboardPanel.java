package com.library.view;

import com.library.model.Librarian;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class LibrarianDashboardPanel extends GradientPanel {

    private Librarian librarian;

    private JLabel welcomeLabel;
    private JLabel librarianNameLabel;

    private JPanel mainContentPanel;
    private CardLayout cardLayout;

    private JButton booksBtn;
    private JButton usersBtn;

    // Books Panel Components
    private JPanel booksPanel;
    private JTextField bookSearchField;
    private JTable booksTable;
    private DefaultTableModel booksTableModel;
    private JButton addBookFloatingBtn;
    private JButton[] bookCategoryButtons;

    // Users Panel Components
    private JPanel usersPanel;
    private JTextField userSearchField;
    private JTable usersTable;
    private DefaultTableModel usersTableModel;
    private JButton addUserFloatingBtn;

    public LibrarianDashboardPanel(Librarian librarian) {
        this.librarian = librarian;

        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        initializeComponents();
        setupLayout();
    }

    private void initializeComponents() {
        // Header components
        welcomeLabel = new JLabel("Librarian Dashboard");
        welcomeLabel.setFont(Theme.PRIMARY_FONT);
        welcomeLabel.setForeground(Theme.VIOLET);

        librarianNameLabel = new JLabel("Welcome, " + librarian.getFullName());
        librarianNameLabel.setFont(Theme.SECONDARY_FONT);
        librarianNameLabel.setForeground(Theme.VIOLET);

        // Sidebar buttons
        Dimension sidebarButtonSize = new Dimension(180, 50);
        Font buttonFont = new Font("Arial", Font.BOLD, 14);

        booksBtn = createStyledButton("📚 Books", sidebarButtonSize, buttonFont);
        usersBtn = createStyledButton("👥 Users", sidebarButtonSize, buttonFont);

        // Initialize panels
        cardLayout = new CardLayout();
        mainContentPanel = new JPanel(cardLayout);
        mainContentPanel.setOpaque(false);

        // Create Books and Users panels
        booksPanel = createBooksPanel();
        usersPanel = createUsersPanel();

        mainContentPanel.add(booksPanel, "BOOKS");
        mainContentPanel.add(usersPanel, "USERS");
    }

    private JButton createStyledButton(String text, Dimension size, Font font) {
        JButton button = new JButton(text);
        button.setPreferredSize(size);
        button.setMaximumSize(size);
        button.setBackground(Theme.CYAN);
        button.setForeground(Theme.VIOLET);
        button.setFont(font);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createRaisedBevelBorder());
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Hover effect
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(Theme.VIOLET);
                button.setForeground(Color.WHITE);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(Theme.CYAN);
                button.setForeground(Theme.VIOLET);
            }
        });

        return button;
    }

    private JPanel createBooksPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setOpaque(false);

        // Top: Search field
        JPanel searchPanel = new JPanel(new BorderLayout(10, 0));
        searchPanel.setOpaque(false);
        JLabel searchLabel = new JLabel("🔍 Search:");
        searchLabel.setForeground(Theme.VIOLET);
        searchLabel.setFont(new Font("Arial", Font.BOLD, 14));
        bookSearchField = new JTextField();
        bookSearchField.setPreferredSize(new Dimension(300, 35));
        bookSearchField.setFont(new Font("Arial", Font.PLAIN, 14));
        searchPanel.add(searchLabel, BorderLayout.WEST);
        searchPanel.add(bookSearchField, BorderLayout.CENTER);
        searchPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Middle: Books table
        String[] columnNames = {"Book Name", "Author", "Category", "Update", "Delete"};
        booksTableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column >= 3; // Only Update and Delete columns are editable
            }
        };
        booksTable = new JTable(booksTableModel);
        booksTable.setRowHeight(35);
        booksTable.setFont(new Font("Arial", Font.PLAIN, 12));
        booksTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        booksTable.getTableHeader().setBackground(Theme.CYAN);
        booksTable.getTableHeader().setForeground(Theme.VIOLET);

        // Add sample data
        addSampleBooks();

        JScrollPane tableScrollPane = new JScrollPane(booksTable);
        tableScrollPane.setOpaque(false);
        tableScrollPane.getViewport().setOpaque(false);

        // Bottom: Category filter buttons and floating add button
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setOpaque(false);

        JPanel categoryPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        categoryPanel.setOpaque(false);

        String[] categories = {"All", "Fiction", "Non-Fiction", "Science", "History", "Technology"};
        bookCategoryButtons = new JButton[categories.length];

        for (int i = 0; i < categories.length; i++) {
            bookCategoryButtons[i] = new JButton(categories[i]);
            bookCategoryButtons[i].setFont(new Font("Arial", Font.PLAIN, 12));
            bookCategoryButtons[i].setBackground(new Color(240, 240, 240));
            bookCategoryButtons[i].setForeground(Theme.VIOLET);
            bookCategoryButtons[i].setFocusPainted(false);
            bookCategoryButtons[i].setCursor(new Cursor(Cursor.HAND_CURSOR));
            bookCategoryButtons[i].setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(Theme.CYAN, 2),
                    BorderFactory.createEmptyBorder(5, 15, 5, 15)
            ));
            categoryPanel.add(bookCategoryButtons[i]);
        }

        // Floating add button
        JPanel floatingBtnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        floatingBtnPanel.setOpaque(false);
        addBookFloatingBtn = new JButton("➕");
        addBookFloatingBtn.setFont(new Font("Arial", Font.BOLD, 24));
        addBookFloatingBtn.setPreferredSize(new Dimension(60, 60));
        addBookFloatingBtn.setBackground(Theme.VIOLET);
        addBookFloatingBtn.setForeground(Color.WHITE);
        addBookFloatingBtn.setFocusPainted(false);
        addBookFloatingBtn.setBorder(BorderFactory.createEmptyBorder());
        addBookFloatingBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        addBookFloatingBtn.setToolTipText("Add New Book");
        floatingBtnPanel.add(addBookFloatingBtn);

        bottomPanel.add(categoryPanel, BorderLayout.WEST);
        bottomPanel.add(floatingBtnPanel, BorderLayout.EAST);

        panel.add(searchPanel, BorderLayout.NORTH);
        panel.add(tableScrollPane, BorderLayout.CENTER);
        panel.add(bottomPanel, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createUsersPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setOpaque(false);

        // Top: Search field
        JPanel searchPanel = new JPanel(new BorderLayout(10, 0));
        searchPanel.setOpaque(false);
        JLabel searchLabel = new JLabel("🔍 Search:");
        searchLabel.setForeground(Theme.VIOLET);
        searchLabel.setFont(new Font("Arial", Font.BOLD, 14));
        userSearchField = new JTextField();
        userSearchField.setPreferredSize(new Dimension(300, 35));
        userSearchField.setFont(new Font("Arial", Font.PLAIN, 14));
        searchPanel.add(searchLabel, BorderLayout.WEST);
        searchPanel.add(userSearchField, BorderLayout.CENTER);
        searchPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Middle: Users table
        String[] columnNames = {"User ID", "User Name", "Role", "Update", "Delete"};
        usersTableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column >= 3; // Only Update and Delete columns are editable
            }
        };
        usersTable = new JTable(usersTableModel);
        usersTable.setRowHeight(35);
        usersTable.setFont(new Font("Arial", Font.PLAIN, 12));
        usersTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        usersTable.getTableHeader().setBackground(Theme.CYAN);
        usersTable.getTableHeader().setForeground(Theme.VIOLET);

        // Add sample data
        addSampleUsers();

        JScrollPane tableScrollPane = new JScrollPane(usersTable);
        tableScrollPane.setOpaque(false);
        tableScrollPane.getViewport().setOpaque(false);

        // Bottom: Floating add button
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottomPanel.setOpaque(false);
        addUserFloatingBtn = new JButton("➕");
        addUserFloatingBtn.setFont(new Font("Arial", Font.BOLD, 24));
        addUserFloatingBtn.setPreferredSize(new Dimension(60, 60));
        addUserFloatingBtn.setBackground(Theme.VIOLET);
        addUserFloatingBtn.setForeground(Color.WHITE);
        addUserFloatingBtn.setFocusPainted(false);
        addUserFloatingBtn.setBorder(BorderFactory.createEmptyBorder());
        addUserFloatingBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        addUserFloatingBtn.setToolTipText("Add New User");
        bottomPanel.add(addUserFloatingBtn);

        panel.add(searchPanel, BorderLayout.NORTH);
        panel.add(tableScrollPane, BorderLayout.CENTER);
        panel.add(bottomPanel, BorderLayout.SOUTH);

        return panel;
    }

    private void addSampleBooks() {
        booksTableModel.addRow(new Object[]{"The Great Gatsby", "F. Scott Fitzgerald", "Fiction", "✏️", "🗑️"});
        booksTableModel.addRow(new Object[]{"To Kill a Mockingbird", "Harper Lee", "Fiction", "✏️", "🗑️"});
        booksTableModel.addRow(new Object[]{"1984", "George Orwell", "Fiction", "✏️", "🗑️"});
        booksTableModel.addRow(new Object[]{"A Brief History of Time", "Stephen Hawking", "Science", "✏️", "🗑️"});
        booksTableModel.addRow(new Object[]{"Sapiens", "Yuval Noah Harari", "History", "✏️", "🗑️"});
    }

    private void addSampleUsers() {
        usersTableModel.addRow(new Object[]{"U001", "John Doe", "Member", "✏️", "🗑️"});
        usersTableModel.addRow(new Object[]{"U002", "Jane Smith", "Member", "✏️", "🗑️"});
        usersTableModel.addRow(new Object[]{"U003", "Bob Johnson", "Member", "✏️", "🗑️"});
        usersTableModel.addRow(new Object[]{"U004", "Alice Williams", "Premium Member", "✏️", "🗑️"});
    }

    private void setupLayout() {
        // Header
        JPanel headerPanel = new JPanel();
        headerPanel.setOpaque(false);
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));
        headerPanel.add(welcomeLabel);
        headerPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        headerPanel.add(librarianNameLabel);
        add(headerPanel, BorderLayout.NORTH);

        // Sidebar
        JPanel sideBar = new JPanel();
        sideBar.setOpaque(false);
        sideBar.setLayout(new BoxLayout(sideBar, BoxLayout.Y_AXIS));
        sideBar.setPreferredSize(new Dimension(200, getHeight()));
        sideBar.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));

        sideBar.add(booksBtn);
        sideBar.add(Box.createRigidArea(new Dimension(0, 15)));
        sideBar.add(usersBtn);
        sideBar.add(Box.createVerticalGlue());

        add(sideBar, BorderLayout.WEST);

        // Main content panel
        add(mainContentPanel, BorderLayout.CENTER);

        // Set default view to Books
        cardLayout.show(mainContentPanel, "BOOKS");
    }

    // Action Listener methods for sidebar navigation
    public void addBooksListener(ActionListener listener) {
        booksBtn.addActionListener(e -> {
            cardLayout.show(mainContentPanel, "BOOKS");
            listener.actionPerformed(e);
        });
    }

    public void addUsersListener(ActionListener listener) {
        usersBtn.addActionListener(e -> {
            cardLayout.show(mainContentPanel, "USERS");
            listener.actionPerformed(e);
        });
    }

    // Books Panel Action Listeners
    public void addBookSearchListener(ActionListener listener) {
        bookSearchField.addActionListener(listener);
    }

    public void addAddBookListener(ActionListener listener) {
        addBookFloatingBtn.addActionListener(listener);
    }

    public void addCategoryFilterListener(String category, ActionListener listener) {
        for (JButton btn : bookCategoryButtons) {
            if (btn.getText().equals(category)) {
                btn.addActionListener(listener);
                break;
            }
        }
    }

    public void addBooksTableListener(MouseAdapter mouseAdapter) {
        booksTable.addMouseListener(mouseAdapter);
    }

    // Users Panel Action Listeners
    public void addUserSearchListener(ActionListener listener) {
        userSearchField.addActionListener(listener);
    }

    public void addAddUserListener(ActionListener listener) {
        addUserFloatingBtn.addActionListener(listener);
    }

    public void addUsersTableListener(MouseAdapter mouseAdapter) {
        usersTable.addMouseListener(mouseAdapter);
    }

    // Helper methods
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

    public String getBookSearchText() {
        return bookSearchField.getText();
    }

    public String getUserSearchText() {
        return userSearchField.getText();
    }
}