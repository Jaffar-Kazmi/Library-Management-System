package com.library.view;

import com.library.model.User;
import com.library.model.Librarian;
import com.library.model.Reader;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.CompoundBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class UserDialog {

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final int DIALOG_WIDTH = 700;

    public static User showAddDialog(Component parent) {
        JTextField usernameField = createStyledTextField("");
        JPasswordField passwordField = createStyledPasswordField("");
        JTextField fullNameField = createStyledTextField("");
        JTextField emailField = createStyledTextField("");

        String[] roles = {"LIBRARIAN", "READER"};
        JComboBox<String> roleCombo = createStyledComboBox(roles);

        String[] statuses = {"ACTIVE", "INACTIVE"};
        JComboBox<String> statusCombo = createStyledComboBox(statuses);

        JPanel mainPanel = createEditAddPanel(usernameField, passwordField, fullNameField,
                emailField, roleCombo, statusCombo);

        int result = showStyledDialog(parent, mainPanel, "Add New User");

        if (result != JOptionPane.OK_OPTION) {
            return null;
        }

        return validateAndCreateUser(parent, usernameField, passwordField, fullNameField,
                emailField, roleCombo, statusCombo, null);
    }


    public static User showEditDialog(Component parent, User original) {
        JTextField usernameField = createStyledTextField(original.getUsername());
        JPasswordField passwordField = createStyledPasswordField(original.getPassword());
        JTextField fullNameField = createStyledTextField(original.getFullName());
        JTextField emailField = createStyledTextField(
                original.getEmail() != null ? original.getEmail() : ""
        );

        String[] roles = {"LIBRARIAN", "READER"};
        JComboBox<String> roleCombo = createStyledComboBox(roles);
        roleCombo.setSelectedItem(original.getRole());

        String[] statuses = {"ACTIVE", "INACTIVE"};
        JComboBox<String> statusCombo = createStyledComboBox(statuses);
        statusCombo.setSelectedItem(original.getStatus() != null ? original.getStatus() : "ACTIVE");

        JPanel mainPanel = createEditAddPanel(usernameField, passwordField, fullNameField,
                emailField, roleCombo, statusCombo);

        int result = showStyledDialog(parent, mainPanel, "Edit User");

        if (result != JOptionPane.OK_OPTION) {
            return null;
        }

        return validateAndCreateUser(parent, usernameField, passwordField, fullNameField,
                emailField, roleCombo, statusCombo, original.getId());
    }

    /**
     * Shows a details dialog for viewing user information (read-only).
     * @param parent parent component for positioning
     * @param user the user to display
     */
    public static void showDetailsDialog(Component parent, User user) {
        JPanel mainPanel = new JPanel(new BorderLayout(15, 15));
        mainPanel.setBackground(Theme.AQUA);
        mainPanel.setBorder(new EmptyBorder(25, 25, 25, 25));

        // Header with user's full name
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(Theme.VIOLET);
        headerPanel.setBorder(new EmptyBorder(15, 20, 15, 20));

        JLabel nameLabel = new JLabel(user.getFullName());
        nameLabel.setFont(Theme.HEADER_FONT);
        nameLabel.setForeground(Color.WHITE);
        headerPanel.add(nameLabel, BorderLayout.CENTER);

        JLabel idLabel = new JLabel("ID: " + user.getId());
        idLabel.setFont(Theme.SUB_HEADER_FONT);
        idLabel.setForeground(Theme.AQUA);
        headerPanel.add(idLabel, BorderLayout.EAST);

        mainPanel.add(headerPanel, BorderLayout.NORTH);

        // Content area with two columns
        JPanel contentPanel = new JPanel(new GridLayout(1, 2, 20, 0));
        contentPanel.setBackground(Theme.AQUA);

        // Left column - Account Information
        JPanel leftPanel = createSectionPanel("Account Information");
        addDetailRow(leftPanel, "Username", user.getUsername());
        addDetailRow(leftPanel, "Full Name", user.getFullName());
        addDetailRow(leftPanel, "Email", user.getEmail() != null ? user.getEmail() : "N/A");

        // Right column - Role & Status
        JPanel rightPanel = createSectionPanel("Role & Status");

        // Role with icon
        String roleDisplay = user.getRole();
        if ("LIBRARIAN".equals(user.getRole())) {
            roleDisplay = "👤 LIBRARIAN";
        } else {
            roleDisplay = "📚 READER";
        }
        addDetailRow(rightPanel, "Role", roleDisplay);

        // Status with color indicator
        String status = user.getStatus() != null ? user.getStatus() : "ACTIVE";
        Color statusColor = "ACTIVE".equals(status) ? new Color(0, 150, 0) : new Color(200, 0, 0);
        String statusDisplay = "ACTIVE".equals(status) ? "✓ ACTIVE" : "✗ INACTIVE";
        addStatusRow(rightPanel, "Status", statusDisplay, statusColor);

        contentPanel.add(leftPanel);
        contentPanel.add(rightPanel);

        // Add scroll pane
        JScrollPane scrollPane = new JScrollPane(contentPanel);
        scrollPane.setBackground(Theme.AQUA);
        scrollPane.getViewport().setBackground(Theme.AQUA);
        scrollPane.setBorder(null);
        scrollPane.setPreferredSize(new Dimension(DIALOG_WIDTH, 350));
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        mainPanel.add(scrollPane, BorderLayout.CENTER);

        // Style the OK button
        UIManager.put("OptionPane.background", Theme.AQUA);
        UIManager.put("Panel.background", Theme.AQUA);
        UIManager.put("Button.background", Theme.INDIGO);
        UIManager.put("Button.foreground", Color.WHITE);
        UIManager.put("Button.font", Theme.SECONDARY_FONT);

        JOptionPane.showMessageDialog(
                parent,
                mainPanel,
                "User Details",
                JOptionPane.PLAIN_MESSAGE
        );
    }

    /**
     * Creates the main panel for edit/add dialogs with two-column layout.
     */
    private static JPanel createEditAddPanel(JTextField usernameField, JPasswordField passwordField,
                                             JTextField fullNameField, JTextField emailField,
                                             JComboBox<String> roleCombo, JComboBox<String> statusCombo) {
        JPanel mainPanel = new JPanel(new BorderLayout(15, 15));
        mainPanel.setBackground(Theme.AQUA);
        mainPanel.setBorder(new EmptyBorder(25, 25, 25, 25));

        // Content area with two columns
        JPanel contentPanel = new JPanel(new GridLayout(1, 2, 20, 0));
        contentPanel.setBackground(Theme.AQUA);

        // Left column - Account Information
        JPanel leftPanel = createSectionPanel("Account Information");
        addFieldRow(leftPanel, "Username", usernameField, "Required, unique (max 50 chars)");
        addFieldRow(leftPanel, "Password", passwordField, "Required (max 255 chars)");
        addFieldRow(leftPanel, "Full Name", fullNameField, "Required (max 100 chars)");

        // Right column - Additional Details
        JPanel rightPanel = createSectionPanel("Additional Details");
        addFieldRow(rightPanel, "Email", emailField, "Optional (max 100 chars)");
        addComboRow(rightPanel, "Role", roleCombo, "User access level");
        addComboRow(rightPanel, "Status", statusCombo, "Account status");

        contentPanel.add(leftPanel);
        contentPanel.add(rightPanel);

        // Add scroll pane
        JScrollPane scrollPane = new JScrollPane(contentPanel);
        scrollPane.setBackground(Theme.AQUA);
        scrollPane.getViewport().setBackground(Theme.AQUA);
        scrollPane.setBorder(null);
        scrollPane.setPreferredSize(new Dimension(DIALOG_WIDTH, 450));
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        mainPanel.add(scrollPane, BorderLayout.CENTER);

        return mainPanel;
    }

    /**
     * Creates a section panel with a header.
     */
    private static JPanel createSectionPanel(String title) {
        JPanel sectionPanel = new JPanel(new BorderLayout(0, 10));
        sectionPanel.setBackground(Theme.AQUA);

        JLabel headerLabel = new JLabel(title);
        headerLabel.setFont(Theme.SECONDARY_FONT);
        headerLabel.setForeground(Theme.VIOLET);
        headerLabel.setBorder(new EmptyBorder(0, 0, 10, 0));

        JPanel fieldsPanel = new JPanel();
        fieldsPanel.setLayout(new BoxLayout(fieldsPanel, BoxLayout.Y_AXIS));
        fieldsPanel.setBackground(Theme.AQUA);

        sectionPanel.add(headerLabel, BorderLayout.NORTH);
        sectionPanel.add(fieldsPanel, BorderLayout.CENTER);

        return sectionPanel;
    }

    /**
     * Creates a styled text field with theme colors and fonts.
     */
    private static JTextField createStyledTextField(String text) {
        JTextField field = new JTextField(text);
        field.setFont(Theme.NORMAL_FONT);
        field.setForeground(Theme.VIOLET);
        field.setCaretColor(Theme.INDIGO);
        field.setBorder(new CompoundBorder(
                new LineBorder(Theme.CYAN, 2),
                new EmptyBorder(8, 10, 8, 10)
        ));
        field.setPreferredSize(new Dimension(250, 45));
        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
        return field;
    }

    /**
     * Creates a styled password field with theme colors and fonts.
     */
    private static JPasswordField createStyledPasswordField(String text) {
        JPasswordField field = new JPasswordField(text);
        field.setFont(Theme.NORMAL_FONT);
        field.setForeground(Theme.VIOLET);
        field.setCaretColor(Theme.INDIGO);
        field.setBorder(new CompoundBorder(
                new LineBorder(Theme.CYAN, 2),
                new EmptyBorder(8, 10, 8, 10)
        ));
        field.setPreferredSize(new Dimension(250, 45));
        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
        return field;
    }

    /**
     * Creates a styled combo box with theme colors and fonts.
     */
    private static JComboBox<String> createStyledComboBox(String[] items) {
        JComboBox<String> combo = new JComboBox<>(items);
        combo.setFont(Theme.NORMAL_FONT);
        combo.setForeground(Theme.VIOLET);
        combo.setBackground(Color.WHITE);
        combo.setBorder(new CompoundBorder(
                new LineBorder(Theme.CYAN, 2),
                new EmptyBorder(5, 8, 5, 8)
        ));
        combo.setPreferredSize(new Dimension(250, 45));
        combo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
        return combo;
    }

    /**
     * Shows a styled dialog with custom buttons.
     */
    private static int showStyledDialog(Component parent, JPanel panel, String title) {
        UIManager.put("OptionPane.background", Theme.AQUA);
        UIManager.put("Panel.background", Theme.AQUA);
        UIManager.put("Button.background", Theme.INDIGO);
        UIManager.put("Button.foreground", Color.WHITE);
        UIManager.put("Button.font", Theme.SECONDARY_FONT);

        JOptionPane optionPane = new JOptionPane(
                panel,
                JOptionPane.PLAIN_MESSAGE,
                JOptionPane.OK_CANCEL_OPTION
        );

        JDialog dialog = optionPane.createDialog(parent, title);
        dialog.getContentPane().setBackground(Theme.AQUA);
        dialog.setVisible(true);

        Object selectedValue = optionPane.getValue();
        if (selectedValue == null) {
            return JOptionPane.CLOSED_OPTION;
        }
        if (selectedValue instanceof Integer) {
            return (Integer) selectedValue;
        }
        return JOptionPane.CLOSED_OPTION;
    }

    /**
     * Adds a labeled field row with helper text to a section panel.
     */
    private static void addFieldRow(JPanel sectionPanel, String labelText,
                                    JComponent field, String helperText) {
        JPanel fieldsPanel = (JPanel) ((BorderLayout) sectionPanel.getLayout())
                .getLayoutComponent(BorderLayout.CENTER);

        JPanel fieldGroup = new JPanel();
        fieldGroup.setLayout(new BoxLayout(fieldGroup, BoxLayout.Y_AXIS));
        fieldGroup.setBackground(Theme.AQUA);
        fieldGroup.setBorder(new EmptyBorder(0, 0, 15, 0));
        fieldGroup.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Label
        JLabel label = new JLabel(labelText);
        label.setFont(new Font("Segoe UI", Font.BOLD, 14));
        label.setForeground(Theme.VIOLET);
        label.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Helper text
        JLabel helper = new JLabel(helperText);
        helper.setFont(Theme.SUB_HEADER_FONT);
        helper.setForeground(Theme.INDIGO);
        helper.setAlignmentX(Component.LEFT_ALIGNMENT);
        helper.setBorder(new EmptyBorder(2, 0, 5, 0));

        field.setAlignmentX(Component.LEFT_ALIGNMENT);

        fieldGroup.add(label);
        fieldGroup.add(Box.createVerticalStrut(3));
        fieldGroup.add(helper);
        fieldGroup.add(Box.createVerticalStrut(3));
        fieldGroup.add(field);

        fieldsPanel.add(fieldGroup);
    }

    /**
     * Adds a combo box row with helper text to a section panel.
     */
    private static void addComboRow(JPanel sectionPanel, String labelText,
                                    JComboBox<String> combo, String helperText) {
        addFieldRow(sectionPanel, labelText, combo, helperText);
    }

    /**
     * Adds a detail row for read-only display in section panel.
     */
    private static void addDetailRow(JPanel sectionPanel, String labelText, String value) {
        JPanel fieldsPanel = (JPanel) ((BorderLayout) sectionPanel.getLayout())
                .getLayoutComponent(BorderLayout.CENTER);

        JPanel fieldGroup = new JPanel();
        fieldGroup.setLayout(new BoxLayout(fieldGroup, BoxLayout.Y_AXIS));
        fieldGroup.setBackground(Theme.AQUA);
        fieldGroup.setBorder(new EmptyBorder(0, 0, 12, 0));
        fieldGroup.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Label
        JLabel label = new JLabel(labelText);
        label.setFont(new Font("Segoe UI", Font.BOLD, 14));
        label.setForeground(Theme.VIOLET);
        label.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Value
        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(Theme.NORMAL_FONT);
        valueLabel.setForeground(Theme.INDIGO);
        valueLabel.setBackground(Color.WHITE);
        valueLabel.setOpaque(true);
        valueLabel.setBorder(new CompoundBorder(
                new LineBorder(Theme.CYAN, 2),
                new EmptyBorder(8, 10, 8, 10)
        ));
        valueLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        valueLabel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));

        fieldGroup.add(label);
        fieldGroup.add(Box.createVerticalStrut(5));
        fieldGroup.add(valueLabel);

        fieldsPanel.add(fieldGroup);
    }

    /**
     * Adds a status row with custom color for details dialog.
     */
    private static void addStatusRow(JPanel sectionPanel, String labelText, String value, Color valueColor) {
        JPanel fieldsPanel = (JPanel) ((BorderLayout) sectionPanel.getLayout())
                .getLayoutComponent(BorderLayout.CENTER);

        JPanel fieldGroup = new JPanel();
        fieldGroup.setLayout(new BoxLayout(fieldGroup, BoxLayout.Y_AXIS));
        fieldGroup.setBackground(Theme.AQUA);
        fieldGroup.setBorder(new EmptyBorder(5, 0, 0, 0));
        fieldGroup.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Label
        JLabel label = new JLabel(labelText);
        label.setFont(new Font("Segoe UI", Font.BOLD, 14));
        label.setForeground(Theme.VIOLET);
        label.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Value with custom color
        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        valueLabel.setForeground(valueColor);
        valueLabel.setBackground(Color.WHITE);
        valueLabel.setOpaque(true);
        valueLabel.setBorder(new CompoundBorder(
                new LineBorder(valueColor, 3),
                new EmptyBorder(10, 12, 10, 12)
        ));
        valueLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        valueLabel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));

        fieldGroup.add(label);
        fieldGroup.add(Box.createVerticalStrut(5));
        fieldGroup.add(valueLabel);

        fieldsPanel.add(fieldGroup);
    }

    /**
     * Validates all fields and creates a User object.
     */
    private static User validateAndCreateUser(Component parent,
                                              JTextField usernameField,
                                              JPasswordField passwordField,
                                              JTextField fullNameField,
                                              JTextField emailField,
                                              JComboBox<String> roleCombo,
                                              JComboBox<String> statusCombo,
                                              Integer existingUserId) {
        // Extract and trim values
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());
        String fullName = fullNameField.getText().trim();
        String email = emailField.getText().trim();
        String role = (String) roleCombo.getSelectedItem();
        String status = (String) statusCombo.getSelectedItem();

        // Validate required fields
        if (username.isEmpty() || password.isEmpty() || fullName.isEmpty()) {
            showError(parent, "Username, password, and full name are required fields.");
            return null;
        }

        // Validate field lengths
        if (username.length() > 50) {
            showError(parent, "Username must not exceed 50 characters.");
            return null;
        }
        if (password.length() > 255) {
            showError(parent, "Password must not exceed 255 characters.");
            return null;
        }
        if (fullName.length() > 100) {
            showError(parent, "Full name must not exceed 100 characters.");
            return null;
        }
        if (email.length() > 100) {
            showError(parent, "Email must not exceed 100 characters.");
            return null;
        }

        // Validate email format if provided
        if (!email.isEmpty() && !email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            showError(parent, "Please enter a valid email address.");
            return null;
        }

        // Create the user based on role
        User user;
        if ("LIBRARIAN".equals(role)) {
            user = new Librarian(username, password, fullName);
        } else {
            user = new Reader(username, password, fullName);
        }

        if (existingUserId != null) {
            user.setId(existingUserId);
        }

        user.setEmail(email.isEmpty() ? null : email);
        user.setStatus(status);

        return user;
    }

    /**
     * Shows a styled error message dialog.
     */
    private static void showError(Component parent, String message) {
        UIManager.put("OptionPane.background", Theme.AQUA);
        UIManager.put("Panel.background", Theme.AQUA);
        UIManager.put("OptionPane.messageForeground", Theme.VIOLET);
        UIManager.put("OptionPane.messageFont", Theme.NORMAL_FONT);

        JOptionPane.showMessageDialog(
                parent,
                message,
                "Validation Error",
                JOptionPane.ERROR_MESSAGE
        );
    }
}