package com.library.view;

import com.library.model.Reader;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class ReaderDashboardPanel extends JPanel {

    private Reader reader;

    // Main layout components
    private JPanel sidebarPanel;
    private JPanel mainContentPanel;
    private CardLayout cardLayout;

    // Sidebar buttons
    private JButton dashboardBtn;
    private JButton browseBooksBtn;
    private JButton myBooksBtn;
    private JButton historyBtn;
    private JButton logoutBtn;

    // Dashboard components
    private JPanel dashboardPanel;

    // Browse Books panel components
    private JPanel browseBooksPanel;
    private JTextField bookSearchField;
    private JPanel booksCardsContainer;
    private JButton[] categoryButtons;

    // My Books panel components
    private JPanel myBooksPanel;
    private JTable myBooksTable;
    private DefaultTableModel myBooksTableModel;

    // History panel components
    private JPanel historyPanel;
    private JTable historyTable;
    private DefaultTableModel historyTableModel;

    public ReaderDashboardPanel(Reader reader) {
        this.reader = reader;
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
        browseBooksPanel = createBrowseBooksPanel();
        myBooksPanel = createMyBooksPanel();
        historyPanel = createHistoryPanel();

        // Add panels to card layout
        mainContentPanel.add(dashboardPanel, "DASHBOARD");
        mainContentPanel.add(browseBooksPanel, "BROWSE");
        mainContentPanel.add(myBooksPanel, "MY_BOOKS");
        mainContentPanel.add(historyPanel, "HISTORY");
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
        browseBooksBtn = createSidebarButton("📚  Browse Books", false);
        myBooksBtn = createSidebarButton("📖  My Books", false);
        historyBtn = createSidebarButton("📜  History", false);

        sidebarPanel.add(dashboardBtn);
        sidebarPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        sidebarPanel.add(browseBooksBtn);
        sidebarPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        sidebarPanel.add(myBooksBtn);
        sidebarPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        sidebarPanel.add(historyBtn);

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
        dashboardBtn.setForeground(Theme.VIOLET);
        browseBooksBtn.setOpaque(false);
        browseBooksBtn.setForeground(Theme.VIOLET);
        myBooksBtn.setOpaque(false);
        myBooksBtn.setForeground(Theme.VIOLET);
        historyBtn.setOpaque(false);
        historyBtn.setForeground(Theme.VIOLET);

        // Set selected button
        button.setOpaque(true);
        button.setBackground(Theme.INDIGO);
        button.setForeground(Theme.AQUA);
        selectedButton = button.getText();
    }

    private JPanel createDashboardPanel() {
        JPanel panel = new JPanel(new BorderLayout(20, 20));
        panel.setBackground(Theme.AQUA);
        panel.setBorder(new EmptyBorder(30, 30, 30, 30));

        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);

        JLabel titleLabel = new JLabel("My Dashboard");
        titleLabel.setFont(Theme.HEADER_FONT);
        titleLabel.setForeground(Theme.VIOLET);

        JLabel welcomeLabel = new JLabel("Welcome back, " + reader.getFullName());
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

        statsPanel.add(createStatCard("Books Borrowed", "3", "📚", new Color(106, 17, 203)));
        statsPanel.add(createStatCard("Due Soon", "1", "⏰", new Color(251, 146, 60)));
        statsPanel.add(createStatCard("Overdue", "0", "⚠️", new Color(220, 53, 69)));
        statsPanel.add(createStatCard("Total Read", "24", "✅", new Color(52, 211, 153)));

        // Currently borrowed books section
        JPanel borrowedPanel = new JPanel(new BorderLayout(10, 10));
        borrowedPanel.setBackground(Theme.AQUA);
        borrowedPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel borrowedTitle = new JLabel("Currently Borrowed Books");
        borrowedTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        borrowedTitle.setForeground(Theme.VIOLET);

        // Horizontal scrollable cards container for borrowed books
        JPanel borrowedCardsPanel = new JPanel();
        borrowedCardsPanel.setLayout(new BoxLayout(borrowedCardsPanel, BoxLayout.X_AXIS));
        borrowedCardsPanel.setOpaque(false);

        borrowedCardsPanel.add(createBorrowedBookCard("The Great Gatsby", "F. Scott Fitzgerald", "2024-11-01", "2024-11-22", 7));
        borrowedCardsPanel.add(Box.createRigidArea(new Dimension(15, 0)));
        borrowedCardsPanel.add(createBorrowedBookCard("1984", "George Orwell", "2024-11-05", "2024-11-26", 11));
        borrowedCardsPanel.add(Box.createRigidArea(new Dimension(15, 0)));
        borrowedCardsPanel.add(createBorrowedBookCard("To Kill a Mockingbird", "Harper Lee", "2024-11-10", "2024-12-01", 16));
        borrowedCardsPanel.add(Box.createRigidArea(new Dimension(15, 0)));
        borrowedCardsPanel.add(createBorrowedBookCard("Pride and Prejudice", "Jane Austen", "2024-10-28", "2024-11-18", 3));

        JScrollPane borrowedScrollPane = new JScrollPane(borrowedCardsPanel);
        borrowedScrollPane.setOpaque(false);
        borrowedScrollPane.getViewport().setOpaque(false);
        borrowedScrollPane.setBorder(null);
        borrowedScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        borrowedScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
        borrowedScrollPane.getHorizontalScrollBar().setUnitIncrement(16);

        borrowedPanel.add(borrowedTitle, BorderLayout.NORTH);
        borrowedPanel.add(borrowedScrollPane, BorderLayout.CENTER);

        // Layout
        JPanel contentPanel = new JPanel(new BorderLayout(20, 20));
        contentPanel.setOpaque(false);
        contentPanel.add(statsPanel, BorderLayout.NORTH);
        contentPanel.add(borrowedPanel, BorderLayout.CENTER);

        panel.add(headerPanel, BorderLayout.NORTH);
        panel.add(contentPanel, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createBorrowedBookCard(String title, String author, String borrowedDate, String dueDate, int daysLeft) {
        JPanel card = new JPanel();
        card.setLayout(new BorderLayout(10, 10));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Theme.INDIGO, 2),
                new EmptyBorder(15, 15, 15, 15)
        ));

        // Book icon
        JLabel iconLabel = new JLabel("📖", SwingConstants.CENTER);
        iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 40));
        iconLabel.setForeground(Theme.VIOLET);

        // Book details
        JPanel detailsPanel = new JPanel();
        detailsPanel.setLayout(new BoxLayout(detailsPanel, BoxLayout.Y_AXIS));
        detailsPanel.setOpaque(false);

        JLabel titleLabel = new JLabel("<html><b>" + title + "</b></html>");
        titleLabel.setFont(Theme.SUB_HEADER_FONT);
        titleLabel.setForeground(Theme.VIOLET);

        JLabel authorLabel = new JLabel("by " + author);
        authorLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        authorLabel.setForeground(Color.DARK_GRAY);

        JLabel dueDateLabel = new JLabel("Due: " + dueDate);
        dueDateLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        dueDateLabel.setForeground(Color.GRAY);

        // Days left with color coding
        Color daysColor = daysLeft <= 3 ? new Color(220, 53, 69) :
                daysLeft <= 7 ? new Color(251, 146, 60) :
                        new Color(52, 211, 153);
        JLabel daysLeftLabel = new JLabel(daysLeft + " days left");
        daysLeftLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        daysLeftLabel.setForeground(daysColor);

        detailsPanel.add(titleLabel);
        detailsPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        detailsPanel.add(authorLabel);
        detailsPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        detailsPanel.add(dueDateLabel);
        detailsPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        detailsPanel.add(daysLeftLabel);

        // Return button
        JButton returnBtn = new JButton("Return Book");
        returnBtn.setFont(new Font("Segoe UI", Font.BOLD, 11));
        returnBtn.setForeground(Color.WHITE);
        returnBtn.setBackground(Theme.INDIGO);
        returnBtn.setFocusPainted(false);
        returnBtn.setBorderPainted(false);
        returnBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.setOpaque(false);
        buttonPanel.add(returnBtn);

        card.add(iconLabel, BorderLayout.NORTH);
        card.add(detailsPanel, BorderLayout.CENTER);
        card.add(buttonPanel, BorderLayout.SOUTH);

        return card;
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

    private JPanel createBrowseBooksPanel() {
        JPanel panel = new JPanel(new BorderLayout(20, 20));
        panel.setBackground(Theme.AQUA);
        panel.setBorder(new EmptyBorder(30, 30, 30, 30));

        // Header with search
        JPanel headerPanel = new JPanel(new BorderLayout(20, 0));
        headerPanel.setOpaque(false);

        JLabel titleLabel = new JLabel("Browse Books");
        titleLabel.setFont(Theme.HEADER_FONT);
        titleLabel.setForeground(Theme.VIOLET);

        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        searchPanel.setOpaque(false);

        bookSearchField = new JTextField(25);
        bookSearchField.setFont(Theme.SUB_HEADER_FONT);
        bookSearchField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
                new EmptyBorder(8, 12, 8, 12)
        ));

        searchPanel.add(new JLabel("🔍"));
        searchPanel.add(bookSearchField);

        headerPanel.add(titleLabel, BorderLayout.WEST);
        headerPanel.add(searchPanel, BorderLayout.EAST);

        // Category filters
        JPanel categoryPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        categoryPanel.setOpaque(false);

        String[] categories = {"All", "Fiction", "Non-Fiction", "Science", "History", "Technology", "Romance"};
        categoryButtons = new JButton[categories.length];

        for (int i = 0; i < categories.length; i++) {
            categoryButtons[i] = new JButton(categories[i]);
            categoryButtons[i].setFont(Theme.NORMAL_FONT);
            categoryButtons[i].setBackground(i == 0 ? Theme.INDIGO : Color.WHITE);
            categoryButtons[i].setForeground(i == 0 ? Theme.AQUA : Theme.VIOLET);
            categoryButtons[i].setFocusPainted(false);
            categoryButtons[i].setCursor(new Cursor(Cursor.HAND_CURSOR));
            categoryButtons[i].setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(Theme.INDIGO, 2),
                    new EmptyBorder(5, 15, 5, 15)
            ));
            categoryPanel.add(categoryButtons[i]);
        }

        // Books cards container
        booksCardsContainer = new JPanel(new GridLayout(0, 3, 15, 15));
        booksCardsContainer.setOpaque(false);

        // Add sample books
        addSampleBooksCards();

        JScrollPane scrollPane = new JScrollPane(booksCardsContainer);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        panel.add(headerPanel, BorderLayout.NORTH);
        panel.add(categoryPanel, BorderLayout.CENTER);

        JPanel scrollContainer = new JPanel(new BorderLayout());
        scrollContainer.setOpaque(false);
        scrollContainer.add(scrollPane, BorderLayout.CENTER);

        JPanel mainContent = new JPanel(new BorderLayout(10, 10));
        mainContent.setOpaque(false);
        mainContent.add(categoryPanel, BorderLayout.NORTH);
        mainContent.add(scrollContainer, BorderLayout.CENTER);

        panel.add(headerPanel, BorderLayout.NORTH);
        panel.add(mainContent, BorderLayout.CENTER);

        return panel;
    }

    private void addSampleBooksCards() {
        booksCardsContainer.add(createBookCard("The Great Gatsby", "F. Scott Fitzgerald", "Fiction", true));
        booksCardsContainer.add(createBookCard("To Kill a Mockingbird", "Harper Lee", "Fiction", false));
        booksCardsContainer.add(createBookCard("1984", "George Orwell", "Fiction", false));
        booksCardsContainer.add(createBookCard("Pride and Prejudice", "Jane Austen", "Romance", true));
        booksCardsContainer.add(createBookCard("The Catcher in the Rye", "J.D. Salinger", "Fiction", true));
        booksCardsContainer.add(createBookCard("Sapiens", "Yuval Noah Harari", "History", true));
        booksCardsContainer.add(createBookCard("The Great Gatsby", "F. Scott Fitzgerald", "Fiction", true));
        booksCardsContainer.add(createBookCard("To Kill a Mockingbird", "Harper Lee", "Fiction", false));
        booksCardsContainer.add(createBookCard("1984", "George Orwell", "Fiction", false));
        booksCardsContainer.add(createBookCard("Pride and Prejudice", "Jane Austen", "Romance", true));
        booksCardsContainer.add(createBookCard("The Catcher in the Rye", "J.D. Salinger", "Fiction", true));
        booksCardsContainer.add(createBookCard("Sapiens", "Yuval Noah Harari", "History", true));
        booksCardsContainer.add(createBookCard("The Great Gatsby", "F. Scott Fitzgerald", "Fiction", true));
        booksCardsContainer.add(createBookCard("To Kill a Mockingbird", "Harper Lee", "Fiction", false));
        booksCardsContainer.add(createBookCard("1984", "George Orwell", "Fiction", false));
        booksCardsContainer.add(createBookCard("Pride and Prejudice", "Jane Austen", "Romance", true));
        booksCardsContainer.add(createBookCard("The Catcher in the Rye", "J.D. Salinger", "Fiction", true));
        booksCardsContainer.add(createBookCard("Sapiens", "Yuval Noah Harari", "History", true));
    }

    private JPanel createBookCard(String title, String author, String category, boolean available) {
        JPanel card = new JPanel();
        card.setLayout(new BorderLayout(10, 10));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Theme.CYAN, 2),
                new EmptyBorder(15, 15, 15, 15)
        ));
        card.setPreferredSize(new Dimension(250, 220));

        // Book icon
        JLabel iconLabel = new JLabel("📖", SwingConstants.CENTER);
        iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 40));
        iconLabel.setForeground(Theme.VIOLET);

        // Book details
        JPanel detailsPanel = new JPanel();
        detailsPanel.setLayout(new BoxLayout(detailsPanel, BoxLayout.Y_AXIS));
        detailsPanel.setOpaque(false);

        JLabel titleLabel = new JLabel("<html><b>" + title + "</b></html>");
        titleLabel.setFont(Theme.SUB_HEADER_FONT);
        titleLabel.setForeground(Theme.VIOLET);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel authorLabel = new JLabel("by " + author);
        authorLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        authorLabel.setForeground(Color.DARK_GRAY);
        authorLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel categoryLabel = new JLabel(category);
        categoryLabel.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        categoryLabel.setForeground(Color.GRAY);
        categoryLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel statusLabel = new JLabel(available ? "✅ Available" : "❌ Issued");
        statusLabel.setFont(new Font("Segoe UI", Font.BOLD, 11));
        statusLabel.setForeground(available ? new Color(52, 211, 153) : new Color(220, 53, 69));
        statusLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        detailsPanel.add(titleLabel);
        detailsPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        detailsPanel.add(authorLabel);
        detailsPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        detailsPanel.add(categoryLabel);
        detailsPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        detailsPanel.add(statusLabel);

        // Action buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 0));
        buttonPanel.setOpaque(false);

        JButton viewBtn = new JButton("View");
        viewBtn.setFont(new Font("Segoe UI", Font.BOLD, 11));
        viewBtn.setForeground(Theme.VIOLET);
        viewBtn.setBackground(Theme.CYAN);
        viewBtn.setFocusPainted(false);
        viewBtn.setBorderPainted(false);
        viewBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        viewBtn.setPreferredSize(new Dimension(80, 28));

        JButton borrowBtn = new JButton("Borrow");
        borrowBtn.setFont(new Font("Segoe UI", Font.BOLD, 11));
        borrowBtn.setForeground(Color.WHITE);
        borrowBtn.setBackground(available ? Theme.INDIGO : Color.GRAY);
        borrowBtn.setFocusPainted(false);
        borrowBtn.setBorderPainted(false);
        borrowBtn.setCursor(new Cursor(available ? Cursor.HAND_CURSOR : Cursor.DEFAULT_CURSOR));
        borrowBtn.setEnabled(available);
        borrowBtn.setPreferredSize(new Dimension(80, 28));

        buttonPanel.add(viewBtn);
        buttonPanel.add(borrowBtn);

        card.add(iconLabel, BorderLayout.NORTH);
        card.add(detailsPanel, BorderLayout.CENTER);
        card.add(buttonPanel, BorderLayout.SOUTH);

        // Hover effect
        card.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                card.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(Theme.VIOLET, 3),
                        new EmptyBorder(15, 15, 15, 15)
                ));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                card.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(Theme.CYAN, 2),
                        new EmptyBorder(15, 15, 15, 15)
                ));
            }
        });

        return card;
    }

    private JPanel createMyBooksPanel() {
        JPanel panel = new JPanel(new BorderLayout(20, 20));
        panel.setBackground(Theme.AQUA);
        panel.setBorder(new EmptyBorder(30, 30, 30, 30));

        // Header
        JLabel titleLabel = new JLabel("My Borrowed Books");
        titleLabel.setFont(Theme.HEADER_FONT);
        titleLabel.setForeground(Theme.VIOLET);

        // Books table
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setOpaque(false);
        tablePanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        String[] columns = {"Book Title", "Author", "Borrowed Date", "Due Date", "Days Left", "Actions"};
        myBooksTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        // Sample data
        myBooksTableModel.addRow(new Object[]{"The Great Gatsby", "F. Scott Fitzgerald", "2024-11-01", "2024-11-22", "7 days", "⋮"});
        myBooksTableModel.addRow(new Object[]{"1984", "George Orwell", "2024-11-05", "2024-11-26", "11 days", "⋮"});
        myBooksTableModel.addRow(new Object[]{"To Kill a Mockingbird", "Harper Lee", "2024-11-10", "2024-12-01", "16 days", "⋮"});

        myBooksTable = createStyledTable(myBooksTableModel);

        // Add mouse listener for actions column
        myBooksTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = myBooksTable.rowAtPoint(e.getPoint());
                int col = myBooksTable.columnAtPoint(e.getPoint());

                if (row >= 0 && col == myBooksTable.getColumnCount() - 1) {
                    showMyBookActionsMenu(myBooksTable, row, e.getX(), e.getY());
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(myBooksTable);
        scrollPane.setBorder(null);

        tablePanel.add(scrollPane, BorderLayout.CENTER);

        panel.add(titleLabel, BorderLayout.NORTH);
        panel.add(tablePanel, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createHistoryPanel() {
        JPanel panel = new JPanel(new BorderLayout(20, 20));
        panel.setBackground(Theme.AQUA);
        panel.setBorder(new EmptyBorder(30, 30, 30, 30));

        // Header
        JLabel titleLabel = new JLabel("Borrowing History");
        titleLabel.setFont(Theme.HEADER_FONT);
        titleLabel.setForeground(Theme.VIOLET);

        // History table
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setOpaque(false);
        tablePanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        String[] columns = {"Book Title", "Author", "Borrowed Date", "Return Date", "Status"};
        historyTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        // Sample data
        historyTableModel.addRow(new Object[]{"Clean Code", "Robert C. Martin", "2024-10-01", "2024-10-15", "✅ Returned"});
        historyTableModel.addRow(new Object[]{"Sapiens", "Yuval Noah Harari", "2024-09-15", "2024-10-01", "✅ Returned"});
        historyTableModel.addRow(new Object[]{"The Hobbit", "J.R.R. Tolkien", "2024-09-01", "2024-09-20", "✅ Returned"});
        historyTableModel.addRow(new Object[]{"Atomic Habits", "James Clear", "2024-08-15", "2024-09-05", "✅ Returned"});
        historyTableModel.addRow(new Object[]{"Thinking, Fast and Slow", "Daniel Kahneman", "2024-08-01", "2024-08-25", "✅ Returned"});

        historyTable = createStyledTable(historyTableModel);
        JScrollPane scrollPane = new JScrollPane(historyTable);
        scrollPane.setBorder(null);

        tablePanel.add(scrollPane, BorderLayout.CENTER);

        panel.add(titleLabel, BorderLayout.NORTH);
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

    private void showMyBookActionsMenu(JTable table, int row, int x, int y) {
        JPopupMenu popup = new JPopupMenu();
        popup.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200), 1));

        String bookTitle = table.getValueAt(row, 0).toString();
        String dueDate = table.getValueAt(row, 3).toString();

        JMenuItem viewDetails = createMenuItem("👁️ View Details", new Color(37, 117, 252));
        JMenuItem renewBook = createMenuItem("🔄 Renew Book", new Color(52, 211, 153));
        JMenuItem returnBook = createMenuItem("📥 Return Book", new Color(106, 17, 203));

        popup.add(viewDetails);
        popup.add(renewBook);
        popup.addSeparator();
        popup.add(returnBook);

        viewDetails.addActionListener(e -> handleViewBookDetails(bookTitle));
        renewBook.addActionListener(e -> handleRenewBook(bookTitle, row));
        returnBook.addActionListener(e -> handleReturnBook(bookTitle, row));

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

    private void handleViewBookDetails(String bookTitle) {
        JOptionPane.showMessageDialog(this,
                "Viewing details for:\n\n" + bookTitle,
                "Book Details",
                JOptionPane.INFORMATION_MESSAGE);
    }

    private void handleRenewBook(String bookTitle, int row) {
        int confirm = JOptionPane.showConfirmDialog(this,
                "Do you want to renew this book?\n\n" + bookTitle + "\n\nThis will extend the due date by 14 days.",
                "Renew Book",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            // Update due date (simplified - you'd calculate actual new date)
            JOptionPane.showMessageDialog(this,
                    "Book renewed successfully!\nNew due date has been updated.",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void handleReturnBook(String bookTitle, int row) {
        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to return this book?\n\n" + bookTitle,
                "Return Book",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            myBooksTableModel.removeRow(row);
            JOptionPane.showMessageDialog(this,
                    "Book returned successfully!",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void setupLayout() {
        add(sidebarPanel, BorderLayout.WEST);
        add(mainContentPanel, BorderLayout.CENTER);

        // Set up navigation
        dashboardBtn.addActionListener(e -> {
            setSelectedButton(dashboardBtn);
            cardLayout.show(mainContentPanel, "DASHBOARD");
        });

        browseBooksBtn.addActionListener(e -> {
            setSelectedButton(browseBooksBtn);
            cardLayout.show(mainContentPanel, "BROWSE");
        });

        myBooksBtn.addActionListener(e -> {
            setSelectedButton(myBooksBtn);
            cardLayout.show(mainContentPanel, "MY_BOOKS");
        });

        historyBtn.addActionListener(e -> {
            setSelectedButton(historyBtn);
            cardLayout.show(mainContentPanel, "HISTORY");
        });
    }

    // Public methods for adding listeners
    public void addDashboardListener(ActionListener listener) {
        dashboardBtn.addActionListener(listener);
    }

    public void addBrowseBooksListener(ActionListener listener) {
        browseBooksBtn.addActionListener(listener);
    }

    public void addMyBooksListener(ActionListener listener) {
        myBooksBtn.addActionListener(listener);
    }

    public void addHistoryListener(ActionListener listener) {
        historyBtn.addActionListener(listener);
    }

    public void addLogoutListener(ActionListener listener) {
        logoutBtn.addActionListener(listener);
    }

    public void addBookSearchListener(ActionListener listener) {
        bookSearchField.addActionListener(listener);
    }

    public Reader getReader() {
        return reader;
    }

    public DefaultTableModel getMyBooksTableModel() {
        return myBooksTableModel;
    }

    public DefaultTableModel getHistoryTableModel() {
        return historyTableModel;
    }

    public JTable getMyBooksTable() {
        return myBooksTable;
    }

    public JTable getHistoryTable() {
        return historyTable;
    }

    public JPanel getBooksCardsContainer() {
        return booksCardsContainer;
    }

    public String getBookSearchText() {
        return bookSearchField.getText();
    }
}