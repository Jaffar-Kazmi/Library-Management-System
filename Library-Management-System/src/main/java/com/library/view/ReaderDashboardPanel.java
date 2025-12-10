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
    private JButton finesBtn;
    private JButton logoutBtn;

    // Dashboard components
    private JPanel dashboardPanel;
    private JLabel booksBorrowedLabel;
    private JLabel dueSoonLabel;
    private JLabel overdueLabel;
    private JLabel totalReadLabel;
    private JLabel unpaidFinesLabel;
    private JPanel borrowedCardsPanel;

    // Browse Books panel components
    private JPanel browseBooksPanel;
    private JTextField bookSearchField;
    private JPanel booksCardsContainer;
    private JButton[] categoryButtons;

    // My Books panel components
    private JPanel myBooksPanel;
    private JTable myBooksTable;
    private DefaultTableModel myBooksTableModel;
    private MyBookActionsListener myBookActionsListener;

    // History panel components
    private JPanel historyPanel;
    private JTable historyTable;
    private DefaultTableModel historyTableModel;

    // Fines panel components
    private JPanel finesPanel;
    private JTable finesTable;
    private DefaultTableModel finesTableModel;

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
        finesPanel = createFinesPanel();

        // Add panels to card layout
        mainContentPanel.add(dashboardPanel, "DASHBOARD");
        mainContentPanel.add(browseBooksPanel, "BROWSE");
        mainContentPanel.add(myBooksPanel, "MY_BOOKS");
        mainContentPanel.add(historyPanel, "HISTORY");
        mainContentPanel.add(finesPanel, "FINES");
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
        browseBooksBtn = UIComponents.createSidebarButton("📚  Browse Books", false, this::getSelectedButtonText);
        myBooksBtn = UIComponents.createSidebarButton("📖  My Books", false, this::getSelectedButtonText);
        historyBtn = UIComponents.createSidebarButton("📜  History", false, this::getSelectedButtonText);
        finesBtn = UIComponents.createSidebarButton("💰  Fines", false, this::getSelectedButtonText);

        sidebarPanel.add(dashboardBtn);
        sidebarPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        sidebarPanel.add(browseBooksBtn);
        sidebarPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        sidebarPanel.add(myBooksBtn);
        sidebarPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        sidebarPanel.add(historyBtn);
        sidebarPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        sidebarPanel.add(finesBtn);

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
        dashboardBtn.setForeground(Theme.VIOLET);
        browseBooksBtn.setOpaque(false);
        browseBooksBtn.setForeground(Theme.VIOLET);
        myBooksBtn.setOpaque(false);
        myBooksBtn.setForeground(Theme.VIOLET);
        historyBtn.setOpaque(false);
        historyBtn.setForeground(Theme.VIOLET);
        finesBtn.setOpaque(false);
        finesBtn.setForeground(Theme.VIOLET);

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
        headerText.add(Box.createRigidArea(new Dimension(0, 5)));
        headerText.add(welcomeLabel);

        headerPanel.add(headerText);

        // Statistics cards (5 cards now with fines)
        JPanel statsPanel = new JPanel(new GridLayout(1, 5, 15, 0));
        statsPanel.setOpaque(false);
        statsPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 120));

        JPanel booksBorrowedCard = UIComponents.createStatCard("Books Borrowed", "0", "📚", new Color(106, 17, 203));
        booksBorrowedLabel = findNumberLabelInCard(booksBorrowedCard);

        JPanel dueSoonCard = UIComponents.createStatCard("Due Soon", "0", "⏰", new Color(251, 146, 60));
        dueSoonLabel = findNumberLabelInCard(dueSoonCard);

        JPanel overdueCard = UIComponents.createStatCard("Overdue", "0", "⚠️", new Color(220, 53, 69));
        overdueLabel = findNumberLabelInCard(overdueCard);

        JPanel totalReadCard = UIComponents.createStatCard("Total Read", "0", "✅", new Color(52, 211, 153));
        totalReadLabel = findNumberLabelInCard(totalReadCard);

        JPanel unpaidFinesCard = UIComponents.createStatCard("Unpaid Fines", "Rs 0", "💰", new Color(220, 53, 69));
        unpaidFinesLabel = findNumberLabelInCard(unpaidFinesCard);

        statsPanel.add(booksBorrowedCard);
        statsPanel.add(dueSoonCard);
        statsPanel.add(overdueCard);
        statsPanel.add(totalReadCard);
        statsPanel.add(unpaidFinesCard);

        // Currently borrowed books section with proper styling
        JPanel borrowedSectionPanel = new JPanel(new BorderLayout(10, 10));
        borrowedSectionPanel.setOpaque(false);
        borrowedSectionPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));

        JLabel borrowedTitle = new JLabel("Currently Borrowed Books");
        borrowedTitle.setFont(new Font("Segoe UI", Font.BOLD, 20));
        borrowedTitle.setForeground(Theme.VIOLET);

        // Create a container with proper alignment
        borrowedCardsPanel = new JPanel();
        borrowedCardsPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 15, 10));
        borrowedCardsPanel.setOpaque(false);

        // Wrap in a panel to control alignment
        JPanel cardsWrapper = new JPanel(new BorderLayout());
        cardsWrapper.setOpaque(false);
        cardsWrapper.add(borrowedCardsPanel, BorderLayout.WEST);

        JScrollPane borrowedScrollPane = new JScrollPane(cardsWrapper);
        borrowedScrollPane.setOpaque(false);
        borrowedScrollPane.getViewport().setOpaque(false);
        borrowedScrollPane.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220), 1),
                new EmptyBorder(15, 15, 15, 15)
        ));
        borrowedScrollPane.setBackground(Color.WHITE);
        borrowedScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        borrowedScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
        borrowedScrollPane.getHorizontalScrollBar().setUnitIncrement(16);

        borrowedSectionPanel.add(borrowedTitle, BorderLayout.NORTH);
        borrowedSectionPanel.add(borrowedScrollPane, BorderLayout.CENTER);

        // Layout
        JPanel contentPanel = new JPanel(new BorderLayout(20, 20));
        contentPanel.setOpaque(false);
        contentPanel.add(statsPanel, BorderLayout.NORTH);
        contentPanel.add(borrowedSectionPanel, BorderLayout.CENTER);

        panel.add(headerPanel, BorderLayout.NORTH);
        panel.add(contentPanel, BorderLayout.CENTER);

        return panel;
    }

    private JLabel findNumberLabelInCard(JPanel card) {
        for (Component c : card.getComponents()) {
            if (c instanceof JLabel) {
                JLabel label = (JLabel) c;
                String text = label.getText();
                if (text != null && (text.matches("\\d+") || text.equals("0") || text.startsWith("Rs"))) {
                    return label;
                }
            } else if (c instanceof JPanel) {
                JLabel found = findNumberLabelInCard((JPanel) c);
                if (found != null) return found;
            }
        }
        return new JLabel("0");
    }

    private JPanel createBorrowedBookCard(String title, String author, String borrowedDate, String dueDate, int daysLeft, double fine) {
        JPanel card = new JPanel();
        card.setLayout(new BorderLayout(0, 10));
        card.setBackground(Color.WHITE);

        // Enhanced shadow effect with multiple borders
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(new Color(230, 230, 230), 1),
                        BorderFactory.createEmptyBorder(2, 2, 4, 4)
                ),
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(Theme.INDIGO, 2, true),
                        new EmptyBorder(15, 15, 15, 15)
                )
        ));

        Dimension cardSize = new Dimension(240, 280);
        card.setPreferredSize(cardSize);
        card.setMaximumSize(cardSize);
        card.setMinimumSize(cardSize);

        // Top panel with icon and title combined
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BorderLayout(0, 8));
        topPanel.setBackground(new Color(245, 245, 255));
        topPanel.setBorder(BorderFactory.createEmptyBorder(15, 10, 15, 10));

        JLabel iconLabel = new JLabel("📖", SwingConstants.CENTER);
        iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 48));
        iconLabel.setForeground(Theme.VIOLET);

        String displayTitle = title.length() > 28 ? title.substring(0, 25) + "..." : title;
        JLabel titleLabel = new JLabel("<html><div style='text-align: center; width: 200px;'><b>" + displayTitle + "</b></div></html>");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        titleLabel.setForeground(Theme.VIOLET);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);

        topPanel.add(iconLabel, BorderLayout.NORTH);
        topPanel.add(titleLabel, BorderLayout.CENTER);

        // Book details panel (author, dates, fine)
        JPanel detailsPanel = new JPanel();
        detailsPanel.setLayout(new BoxLayout(detailsPanel, BoxLayout.Y_AXIS));
        detailsPanel.setOpaque(false);
        detailsPanel.setBorder(new EmptyBorder(10, 0, 10, 0));

        String displayAuthor = author.length() > 25 ? author.substring(0, 22) + "..." : author;
        JLabel authorLabel = new JLabel(displayAuthor);
        authorLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        authorLabel.setForeground(new Color(100, 100, 100));
        authorLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Date info panel with icons
        JPanel dateInfoPanel = new JPanel();
        dateInfoPanel.setLayout(new BoxLayout(dateInfoPanel, BoxLayout.Y_AXIS));
        dateInfoPanel.setOpaque(false);
        dateInfoPanel.setBorder(new EmptyBorder(8, 0, 8, 0));

        JLabel dueDateLabel = new JLabel("📅 Due: " + dueDate);
        dueDateLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        dueDateLabel.setForeground(Color.GRAY);
        dueDateLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Days left with enhanced styling
        Color daysColor = daysLeft < 0 ? new Color(220, 53, 69) :
                daysLeft <= 3 ? new Color(220, 53, 69) :
                        daysLeft <= 7 ? new Color(251, 146, 60) :
                                new Color(52, 211, 153);

        String daysText = daysLeft < 0 ? "⚠️ " + Math.abs(daysLeft) + " days overdue" : "⏰ " + daysLeft + " days left";
        JLabel daysLeftLabel = new JLabel(daysText);
        daysLeftLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        daysLeftLabel.setForeground(daysColor);
        daysLeftLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        dateInfoPanel.add(dueDateLabel);
        dateInfoPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        dateInfoPanel.add(daysLeftLabel);

        // Fine label with badge style
        JPanel finePanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        finePanel.setOpaque(false);

        JLabel fineLabel = new JLabel();
        if (fine > 0) {
            fineLabel.setText("  💳 Fine: Rs " + String.format("%.0f", fine) + "  ");
            fineLabel.setFont(new Font("Segoe UI", Font.BOLD, 11));
            fineLabel.setForeground(Color.WHITE);
            fineLabel.setOpaque(true);
            fineLabel.setBackground(new Color(220, 53, 69));
            fineLabel.setBorder(new EmptyBorder(5, 10, 5, 10));
        } else {
            fineLabel.setText("  ✅ No Fine  ");
            fineLabel.setFont(new Font("Segoe UI", Font.BOLD, 11));
            fineLabel.setForeground(Color.WHITE);
            fineLabel.setOpaque(true);
            fineLabel.setBackground(new Color(52, 211, 153));
            fineLabel.setBorder(new EmptyBorder(5, 10, 5, 10));
        }
        finePanel.add(fineLabel);

        detailsPanel.add(authorLabel);
        detailsPanel.add(Box.createRigidArea(new Dimension(0, 8)));
        detailsPanel.add(dateInfoPanel);
        detailsPanel.add(Box.createRigidArea(new Dimension(0, 8)));
        detailsPanel.add(finePanel);

        // Enhanced return button
        JButton returnBtn = new JButton("📥 Return Book");
        returnBtn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        returnBtn.setForeground(Color.WHITE);
        returnBtn.setBackground(Theme.INDIGO);
        returnBtn.setFocusPainted(false);
        returnBtn.setBorderPainted(false);
        returnBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        returnBtn.setPreferredSize(new Dimension(180, 35));

        returnBtn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                returnBtn.setBackground(Theme.VIOLET);
            }
            public void mouseExited(MouseEvent e) {
                returnBtn.setBackground(Theme.INDIGO);
            }
        });

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        buttonPanel.setOpaque(false);
        buttonPanel.add(returnBtn);

        card.add(topPanel, BorderLayout.NORTH);
        card.add(detailsPanel, BorderLayout.CENTER);
        card.add(buttonPanel, BorderLayout.SOUTH);

        // Hover effect for entire card
        card.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                card.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createCompoundBorder(
                                BorderFactory.createLineBorder(new Color(230, 230, 230), 1),
                                BorderFactory.createEmptyBorder(2, 2, 4, 4)
                        ),
                        BorderFactory.createCompoundBorder(
                                BorderFactory.createLineBorder(Theme.VIOLET, 3, true),
                                new EmptyBorder(15, 15, 15, 15)
                        )
                ));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                card.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createCompoundBorder(
                                BorderFactory.createLineBorder(new Color(230, 230, 230), 1),
                                BorderFactory.createEmptyBorder(2, 2, 4, 4)
                        ),
                        BorderFactory.createCompoundBorder(
                                BorderFactory.createLineBorder(Theme.INDIGO, 2, true),
                                new EmptyBorder(15, 15, 15, 15)
                        )
                ));
            }
        });

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

        // Books cards container with proper layout
        booksCardsContainer = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 15));
        booksCardsContainer.setOpaque(false);

        JScrollPane scrollPane = new JScrollPane(booksCardsContainer);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

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

    private JPanel createBookCard(String title, String author, String category, boolean available) {
        JPanel card = new JPanel();
        card.setLayout(new BorderLayout(8, 8));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Theme.CYAN, 2),
                new EmptyBorder(12, 12, 12, 12)
        ));

        Dimension cardSize = new Dimension(200, 180);
        card.setPreferredSize(cardSize);
        card.setMaximumSize(cardSize);
        card.setMinimumSize(cardSize);

        JLabel iconLabel = new JLabel("📖", SwingConstants.CENTER);
        iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 28));
        iconLabel.setForeground(Theme.VIOLET);

        JPanel detailsPanel = new JPanel();
        detailsPanel.setLayout(new BoxLayout(detailsPanel, BoxLayout.Y_AXIS));
        detailsPanel.setOpaque(false);

        String displayTitle = title.length() > 22 ? title.substring(0, 19) + "..." : title;
        JLabel titleLabel = new JLabel("<html><b>" + displayTitle + "</b></html>");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        titleLabel.setForeground(Theme.VIOLET);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        String displayAuthor = author.length() > 18 ? author.substring(0, 15) + "..." : author;
        JLabel authorLabel = new JLabel(displayAuthor);
        authorLabel.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        authorLabel.setForeground(Color.DARK_GRAY);
        authorLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel categoryLabel = new JLabel(category);
        categoryLabel.setFont(new Font("Segoe UI", Font.ITALIC, 9));
        categoryLabel.setForeground(Color.GRAY);
        categoryLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel statusLabel = new JLabel(available ? "✅ Available" : "❌ Issued");
        statusLabel.setFont(new Font("Segoe UI", Font.BOLD, 10));
        statusLabel.setForeground(available ? new Color(52, 211, 153) : new Color(220, 53, 69));
        statusLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        detailsPanel.add(titleLabel);
        detailsPanel.add(Box.createRigidArea(new Dimension(0, 3)));
        detailsPanel.add(authorLabel);
        detailsPanel.add(Box.createRigidArea(new Dimension(0, 3)));
        detailsPanel.add(categoryLabel);
        detailsPanel.add(Box.createRigidArea(new Dimension(0, 3)));
        detailsPanel.add(statusLabel);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 4, 0));
        buttonPanel.setOpaque(false);

        JButton viewBtn = new JButton("View");
        viewBtn.setFont(new Font("Segoe UI", Font.BOLD, 10));
        viewBtn.setForeground(Theme.VIOLET);
        viewBtn.setBackground(Theme.CYAN);
        viewBtn.setFocusPainted(false);
        viewBtn.setBorderPainted(false);
        viewBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        viewBtn.setPreferredSize(new Dimension(65, 24));

        JButton borrowBtn = new JButton("Borrow");
        borrowBtn.setFont(new Font("Segoe UI", Font.BOLD, 10));
        borrowBtn.setForeground(Color.WHITE);
        borrowBtn.setBackground(available ? Theme.INDIGO : Color.GRAY);
        borrowBtn.setFocusPainted(false);
        borrowBtn.setBorderPainted(false);
        borrowBtn.setCursor(new Cursor(available ? Cursor.HAND_CURSOR : Cursor.DEFAULT_CURSOR));
        borrowBtn.setEnabled(available);
        borrowBtn.setPreferredSize(new Dimension(65, 24));

        buttonPanel.add(viewBtn);
        buttonPanel.add(borrowBtn);

        card.add(iconLabel, BorderLayout.NORTH);
        card.add(detailsPanel, BorderLayout.CENTER);
        card.add(buttonPanel, BorderLayout.SOUTH);

        card.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                card.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(Theme.VIOLET, 2),
                        new EmptyBorder(12, 12, 12, 12)
                ));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                card.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(Theme.CYAN, 2),
                        new EmptyBorder(12, 12, 12, 12)
                ));
            }
        });

        return card;
    }

    private JPanel createMyBooksPanel() {
        JPanel panel = new JPanel(new BorderLayout(20, 20));
        panel.setBackground(Theme.AQUA);
        panel.setBorder(new EmptyBorder(30, 30, 30, 30));

        JLabel titleLabel = new JLabel("My Borrowed Books");
        titleLabel.setFont(Theme.HEADER_FONT);
        titleLabel.setForeground(Theme.VIOLET);

        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setOpaque(false);
        tablePanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        String[] columns = {"Book Title", "Author", "Borrowed Date", "Due Date", "Days Left", "Fine", "Actions"};
        myBooksTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        myBooksTable = createStyledTable(myBooksTableModel);

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

        JLabel titleLabel = new JLabel("Borrowing History");
        titleLabel.setFont(Theme.HEADER_FONT);
        titleLabel.setForeground(Theme.VIOLET);

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

        historyTable = createStyledTable(historyTableModel);
        JScrollPane scrollPane = new JScrollPane(historyTable);
        scrollPane.setBorder(null);

        tablePanel.add(scrollPane, BorderLayout.CENTER);

        panel.add(titleLabel, BorderLayout.NORTH);
        panel.add(tablePanel, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createFinesPanel() {
        JPanel panel = new JPanel(new BorderLayout(20, 20));
        panel.setBackground(Theme.AQUA);
        panel.setBorder(new EmptyBorder(30, 30, 30, 30));

        JLabel titleLabel = new JLabel("My Fines");
        titleLabel.setFont(Theme.HEADER_FONT);
        titleLabel.setForeground(Theme.VIOLET);

        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setOpaque(false);
        tablePanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        String[] columns = {"Loan ID", "Amount", "Status", "Created Date", "Paid Date"};
        finesTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        finesTable = createStyledTable(finesTableModel);
        JScrollPane scrollPane = new JScrollPane(finesTable);
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

        table.getTableHeader().setFont(Theme.SUB_HEADER_FONT);
        table.getTableHeader().setBackground(Theme.INDIGO);
        table.getTableHeader().setForeground(Theme.AQUA);
        table.getTableHeader().setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, new Color(230, 230, 230)));
        table.getTableHeader().setPreferredSize(new Dimension(0, 45));

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

        JMenuItem viewDetails = UIComponents.createMenuItem("👁️ View Details", new Color(37, 117, 252));
        JMenuItem renewBook = UIComponents.createMenuItem("🔄 Renew Book", new Color(52, 211, 153));
        JMenuItem returnBook = UIComponents.createMenuItem("📥 Return Book", new Color(106, 17, 203));

        popup.add(viewDetails);
        popup.add(renewBook);
        popup.addSeparator();
        popup.add(returnBook);

        viewDetails.addActionListener(e -> {
            if (myBookActionsListener != null) {
                myBookActionsListener.onView(bookTitle, row);
            }
        });

        renewBook.addActionListener(e -> {
            if (myBookActionsListener != null) {
                myBookActionsListener.onRenew(bookTitle, row);
            }
        });

        returnBook.addActionListener(e -> {
            if (myBookActionsListener != null) {
                myBookActionsListener.onReturn(bookTitle, row);
            }
        });

        popup.show(table, x, y);
    }

    private void setupLayout() {
        add(sidebarPanel, BorderLayout.WEST);
        add(mainContentPanel, BorderLayout.CENTER);

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

        finesBtn.addActionListener(e -> {
            setSelectedButton(finesBtn);
            cardLayout.show(mainContentPanel, "FINES");
        });
    }

    public void setDashboardStats(int borrowed, int dueSoon, int overdue, int totalRead) {
        if (booksBorrowedLabel != null) booksBorrowedLabel.setText(String.valueOf(borrowed));
        if (dueSoonLabel != null) dueSoonLabel.setText(String.valueOf(dueSoon));
        if (overdueLabel != null) overdueLabel.setText(String.valueOf(overdue));
        if (totalReadLabel != null) totalReadLabel.setText(String.valueOf(totalRead));
    }

    public void setUnpaidFines(double amount) {
        if (unpaidFinesLabel != null) {
            unpaidFinesLabel.setText("Rs " + String.format("%.2f", amount));
        }
    }

    public void clearBorrowedCards() {
        if (borrowedCardsPanel != null) {
            borrowedCardsPanel.removeAll();
            borrowedCardsPanel.revalidate();
            borrowedCardsPanel.repaint();
        }
    }

    public void addBorrowedCard(String title, String author, LocalDate borrowedDate, LocalDate dueDate, double fine) {
        long daysLeft = java.time.temporal.ChronoUnit.DAYS.between(LocalDate.now(), dueDate);
        JPanel card = createBorrowedBookCard(title, author, borrowedDate.toString(), dueDate.toString(), (int) daysLeft, fine);

        if (borrowedCardsPanel != null) {
            borrowedCardsPanel.add(card);
            borrowedCardsPanel.revalidate();
            borrowedCardsPanel.repaint();
        }
    }

    public void setMyBooksData(Object[][] rows) {
        myBooksTableModel.setRowCount(0);
        for (Object[] row : rows) {
            myBooksTableModel.addRow(row);
        }
    }

    public void clearMyBooks() {
        myBooksTableModel.setRowCount(0);
    }

    public void removeMyBookRow(int row) {
        if (row >= 0 && row < myBooksTableModel.getRowCount()) {
            myBooksTableModel.removeRow(row);
        }
    }

    public void setHistoryData(Object[][] rows) {
        historyTableModel.setRowCount(0);
        for (Object[] row : rows) {
            historyTableModel.addRow(row);
        }
    }

    public void setFinesData(Object[][] rows) {
        finesTableModel.setRowCount(0);
        for (Object[] row : rows) {
            finesTableModel.addRow(row);
        }
    }

    public void clearBrowseBooks() {
        booksCardsContainer.removeAll();
        booksCardsContainer.revalidate();
        booksCardsContainer.repaint();
    }

    public void addBrowseBookCard(String title, String author, String category, boolean available) {
        booksCardsContainer.add(createBookCard(title, author, category, available));
        booksCardsContainer.revalidate();
        booksCardsContainer.repaint();
    }

    public void clearHistory() {
        historyTableModel.setRowCount(0);
    }

    public void clearFines() {
        finesTableModel.setRowCount(0);
    }

    public void setMyBookActionsListener(MyBookActionsListener listener) {
        this.myBookActionsListener = listener;
    }

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

    public void addFinesListener(ActionListener listener) {
        finesBtn.addActionListener(listener);
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

    public DefaultTableModel getFinesTableModel() {
        return finesTableModel;
    }

    public JTable getMyBooksTable() {
        return myBooksTable;
    }

    public JTable getHistoryTable() {
        return historyTable;
    }

    public JTable getFinesTable() {
        return finesTable;
    }

    public JPanel getBooksCardsContainer() {
        return booksCardsContainer;
    }

    public String getBookSearchText() {
        return bookSearchField.getText();
    }

    public interface MyBookActionsListener {
        void onView(String bookTitle, int row);
        void onRenew(String bookTitle, int row);
        void onReturn(String bookTitle, int row);
    }
}