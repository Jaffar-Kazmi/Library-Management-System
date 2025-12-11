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
    private JLabel totalBooksLabel;
    private JLabel issuedBooksLabel;
    private JLabel totalUsersLabel;
    private JLabel availableBooksLabel;
    private DefaultTableModel activityTableModel;

    // Books panel components
    private JPanel booksPanel;
    private JTable booksTable;
    private DefaultTableModel booksTableModel;
    private JTextField bookSearchField;
    private JButton addBookBtn;
    private JButton searchBookBtn;
    private BookActionsListener bookActionsListener;

    // Users panel components
    private JPanel usersPanel;
    private JTable usersTable;
    private DefaultTableModel usersTableModel;
    private JTextField userSearchField;
    private JButton addUserBtn;
    private JButton searchUserBtn;
    private UserActionsListener userActionsListener;

    public LibrarianDashboardPanel(Librarian librarian) {
        this.librarian = librarian;
        setLayout(new BorderLayout());
        setBackground(Theme.AQUA);

        initializeComponents();
        setupLayout();
    }

    private void initializeComponents() {
        cardLayout = new CardLayout();
        mainContentPanel = new JPanel(cardLayout);
        mainContentPanel.setBackground(Theme.CYAN);

        createSidebar();

        dashboardPanel = createDashboardPanel();
        booksPanel = createBooksPanel();
        usersPanel = createUsersPanel();

        mainContentPanel.add(dashboardPanel, "DASHBOARD");
        mainContentPanel.add(booksPanel, "BOOKS");
        mainContentPanel.add(usersPanel, "USERS");
    }

    private void createSidebar() {
        sidebarPanel = new GradientPanel();
        sidebarPanel.setLayout(new BoxLayout(sidebarPanel, BoxLayout.Y_AXIS));
        sidebarPanel.setPreferredSize(new Dimension(250, getHeight()));

        // Logo
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

        // Buttons
        dashboardBtn = UIComponents.createSidebarButton("🏠  Dashboard", true, this::getSelectedButtonText);
        booksBtn = UIComponents.createSidebarButton("📚  Manage Books", false, this::getSelectedButtonText);
        usersBtn = UIComponents.createSidebarButton("👥  Manage Users", false, this::getSelectedButtonText);

        sidebarPanel.add(dashboardBtn);
        sidebarPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        sidebarPanel.add(booksBtn);
        sidebarPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        sidebarPanel.add(usersBtn);

        sidebarPanel.add(Box.createVerticalGlue());

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
        dashboardBtn.setOpaque(false);
        dashboardBtn.setForeground(Theme.VIOLET);
        booksBtn.setOpaque(false);
        booksBtn.setForeground(Theme.VIOLET);
        usersBtn.setOpaque(false);
        usersBtn.setForeground(Theme.VIOLET);

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
        JLabel titleLabel = new JLabel("Librarian Dashboard");
        titleLabel.setFont(Theme.HEADER_FONT);
        titleLabel.setForeground(Theme.VIOLET);
        JLabel welcomeLabel = new JLabel("Welcome back, " + librarian.getFullName());
        welcomeLabel.setFont(Theme.SUB_HEADER_FONT);
        welcomeLabel.setForeground(Color.GRAY);
        
        JPanel headerText = new JPanel();
        headerText.setLayout(new BoxLayout(headerText, BoxLayout.Y_AXIS));
        headerText.setOpaque(false);
        headerText.add(titleLabel);
        headerText.add(Box.createRigidArea(new Dimension(0, 5)));
        headerText.add(welcomeLabel);
        headerPanel.add(headerText);

        // Stats
        JPanel statsPanel = new JPanel(new GridLayout(1, 4, 20, 0));
        statsPanel.setOpaque(false);
        statsPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 120));

        JPanel totalBooksCard = UIComponents.createStatCard("Total Books", "0", "📚", new Color(106, 17, 203));
        totalBooksLabel = findNumberLabelInCard(totalBooksCard);

        JPanel issuedBooksCard = UIComponents.createStatCard("Issued Books", "0", "📤", new Color(251, 146, 60));
        issuedBooksLabel = findNumberLabelInCard(issuedBooksCard);

        JPanel totalUsersCard = UIComponents.createStatCard("Total Users", "0", "👥", new Color(52, 211, 153));
        totalUsersLabel = findNumberLabelInCard(totalUsersCard);

        JPanel availBooksCard = UIComponents.createStatCard("Available", "0", "✅", new Color(37, 117, 252));
        availableBooksLabel = findNumberLabelInCard(availBooksCard);

        statsPanel.add(totalBooksCard);
        statsPanel.add(issuedBooksCard);
        statsPanel.add(totalUsersCard);
        statsPanel.add(availBooksCard);

        // Activity Table
        JPanel activityPanel = new JPanel(new BorderLayout());
        activityPanel.setOpaque(false);
        activityPanel.setBorder(new EmptyBorder(20, 0, 0, 0));
        
        JLabel activityTitle = new JLabel("Recent Activity");
        activityTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        activityTitle.setForeground(Theme.VIOLET);
        
        String[] columns = {"ID", "Action", "Details", "Status"};
        activityTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        JTable activityTable = createStyledTable(activityTableModel);
        JScrollPane scrollPane = new JScrollPane(activityTable);
        scrollPane.setBorder(null);
        
        activityPanel.add(activityTitle, BorderLayout.NORTH);
        activityPanel.add(scrollPane, BorderLayout.CENTER);

        JPanel contentPanel = new JPanel(new BorderLayout(20, 20));
        contentPanel.setOpaque(false);
        contentPanel.add(statsPanel, BorderLayout.NORTH);
        contentPanel.add(activityPanel, BorderLayout.CENTER);

        panel.add(headerPanel, BorderLayout.NORTH);
        panel.add(contentPanel, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createBooksPanel() {
        JPanel panel = new JPanel(new BorderLayout(20, 20));
        panel.setBackground(Theme.AQUA);
        panel.setBorder(new EmptyBorder(30, 30, 30, 30));

        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        JLabel titleLabel = new JLabel("Manage Books");
        titleLabel.setFont(Theme.HEADER_FONT);
        titleLabel.setForeground(Theme.VIOLET);

        JPanel actionsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        actionsPanel.setOpaque(false);

        bookSearchField = new JTextField(20);
        searchBookBtn = new JButton("Search");
        addBookBtn = new JButton("+ Add Book");
        
        styleButton(searchBookBtn, Theme.INDIGO);
        styleButton(addBookBtn, Theme.VIOLET);

        actionsPanel.add(bookSearchField);
        actionsPanel.add(searchBookBtn);
        actionsPanel.add(addBookBtn);

        headerPanel.add(titleLabel, BorderLayout.WEST);
        headerPanel.add(actionsPanel, BorderLayout.EAST);

        // Table
        String[] columns = {"ISBN", "Title", "Author", "Category", "Status", "Actions"};
        booksTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        booksTable = createStyledTable(booksTableModel);
        
        booksTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = booksTable.rowAtPoint(e.getPoint());
                int col = booksTable.columnAtPoint(e.getPoint());
                if (row >= 0 && col == 5) {
                    showBookActionsMenu(booksTable, row, e.getX(), e.getY());
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(booksTable);
        scrollPane.setBorder(null);

        panel.add(headerPanel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createUsersPanel() {
        JPanel panel = new JPanel(new BorderLayout(20, 20));
        panel.setBackground(Theme.AQUA);
        panel.setBorder(new EmptyBorder(30, 30, 30, 30));

        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        JLabel titleLabel = new JLabel("Manage Users");
        titleLabel.setFont(Theme.HEADER_FONT);
        titleLabel.setForeground(Theme.VIOLET);

        JPanel actionsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        actionsPanel.setOpaque(false);

        userSearchField = new JTextField(20);
        searchUserBtn = new JButton("Search");
        addUserBtn = new JButton("+ Add User");

        styleButton(searchUserBtn, Theme.INDIGO);
        styleButton(addUserBtn, Theme.VIOLET);

        actionsPanel.add(userSearchField);
        actionsPanel.add(searchUserBtn);
        actionsPanel.add(addUserBtn);

        headerPanel.add(titleLabel, BorderLayout.WEST);
        headerPanel.add(actionsPanel, BorderLayout.EAST);

        // Table
        String[] columns = {"ID", "Name", "Email", "Role", "Status", "Actions"};
        usersTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        usersTable = createStyledTable(usersTableModel);

        usersTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = usersTable.rowAtPoint(e.getPoint());
                int col = usersTable.columnAtPoint(e.getPoint());
                if (row >= 0 && col == 5) {
                    showUserActionsMenu(usersTable, row, e.getX(), e.getY());
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(usersTable);
        scrollPane.setBorder(null);

        panel.add(headerPanel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);

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

    private void styleButton(JButton btn, Color bg) {
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    private JLabel findNumberLabelInCard(JPanel card) {
        for (Component c : card.getComponents()) {
            if (c instanceof JPanel) {
                for (Component sub : ((JPanel) c).getComponents()) {
                    if (sub instanceof JLabel) {
                        String text = ((JLabel) sub).getText();
                        if (text.matches("\\d+")) return (JLabel) sub;
                    }
                }
            }
        }
        return new JLabel("0");
    }

    private void showBookActionsMenu(JTable table, int row, int x, int y) {
        JPopupMenu popup = new JPopupMenu();
        String isbn = table.getValueAt(row, 0).toString();

        JMenuItem view = UIComponents.createMenuItem("View Details", Theme.INDIGO);
        JMenuItem edit = UIComponents.createMenuItem("Edit Book", Theme.VIOLET);
        JMenuItem delete = UIComponents.createMenuItem("Delete Book", Color.RED);
        JMenuItem issue = UIComponents.createMenuItem("Issue Book", new Color(52, 211, 153));
        JMenuItem returnBook = UIComponents.createMenuItem("Return Book", new Color(251, 146, 60));

        view.addActionListener(e -> { if (bookActionsListener != null) bookActionsListener.onView(isbn, row); });
        edit.addActionListener(e -> { if (bookActionsListener != null) bookActionsListener.onEdit(isbn, row); });
        delete.addActionListener(e -> { if (bookActionsListener != null) bookActionsListener.onDelete(isbn, row); });
        issue.addActionListener(e -> { if (bookActionsListener != null) bookActionsListener.onIssue(isbn, row); });
        returnBook.addActionListener(e -> { if (bookActionsListener != null) bookActionsListener.onReturn(isbn, row); });

        popup.add(view);
        popup.add(edit);
        popup.add(delete);
        popup.addSeparator();
        popup.add(issue);
        popup.add(returnBook);

        popup.show(table, x, y);
    }

    private void showUserActionsMenu(JTable table, int row, int x, int y) {
        JPopupMenu popup = new JPopupMenu();
        String id = table.getValueAt(row, 0).toString();

        JMenuItem view = UIComponents.createMenuItem("View Details", Theme.INDIGO);
        JMenuItem edit = UIComponents.createMenuItem("Edit User", Theme.VIOLET);
        JMenuItem delete = UIComponents.createMenuItem("Delete User", Color.RED);

        view.addActionListener(e -> { if (userActionsListener != null) userActionsListener.onView(id, row); });
        edit.addActionListener(e -> { if (userActionsListener != null) userActionsListener.onEdit(id, row); });
        delete.addActionListener(e -> { if (userActionsListener != null) userActionsListener.onDelete(id, row); });

        popup.add(view);
        popup.add(edit);
        popup.add(delete);

        popup.show(table, x, y);
    }

    private void setupLayout() {
        add(sidebarPanel, BorderLayout.WEST);
        add(mainContentPanel, BorderLayout.CENTER);

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

    // Public methods for listeners and data
    public void addDashboardListener(ActionListener l) { dashboardBtn.addActionListener(l); }
    public void addBooksListener(ActionListener l) { booksBtn.addActionListener(l); }
    public void addUsersListener(ActionListener l) { usersBtn.addActionListener(l); }
    public void addLogoutListener(ActionListener l) { logoutBtn.addActionListener(l); }
    
    public void addAddBookButtonListener(ActionListener l) { addBookBtn.addActionListener(l); }
    public void addSearchBookButtonListener(ActionListener l) { searchBookBtn.addActionListener(l); }
    
    public void addAddUserButtonListener(ActionListener l) { addUserBtn.addActionListener(l); }
    public void addUserSearchListener(ActionListener l) { searchUserBtn.addActionListener(l); }

    public void setBookActionsListener(BookActionsListener l) { this.bookActionsListener = l; }
    public void setUserActionsListener(UserActionsListener l) { this.userActionsListener = l; }

    public String getBooksSearchText() { return bookSearchField.getText(); }
    public String getUserSearchText() { return userSearchField.getText(); }

    public void setStats(int totalBooks, int issued, int users, int available) {
        totalBooksLabel.setText(String.valueOf(totalBooks));
        issuedBooksLabel.setText(String.valueOf(issued));
        totalUsersLabel.setText(String.valueOf(users));
        availableBooksLabel.setText(String.valueOf(available));
    }

    public void setActivityData(Object[][] data) {
        activityTableModel.setRowCount(0);
        for (Object[] row : data) activityTableModel.addRow(row);
    }

    public void setBooksData(Object[][] data) {
        booksTableModel.setRowCount(0);
        for (Object[] row : data) booksTableModel.addRow(row);
    }

    public void setUsersData(Object[][] data) {
        usersTableModel.setRowCount(0);
        for (Object[] row : data) usersTableModel.addRow(row);
    }

    public void removeBookRow(int row) {
        if (row >= 0 && row < booksTableModel.getRowCount()) booksTableModel.removeRow(row);
    }

    public void removeUserRow(int row) {
        if (row >= 0 && row < usersTableModel.getRowCount()) usersTableModel.removeRow(row);
    }

    public interface BookActionsListener {
        void onView(String isbn, int row);
        void onEdit(String isbn, int row);
        void onDelete(String isbn, int row);
        void onIssue(String isbn, int row);
        void onReturn(String isbn, int row);
    }

    public interface UserActionsListener {
        void onView(String id, int row);
        void onEdit(String id, int row);
        void onDelete(String id, int row);
    }
}
