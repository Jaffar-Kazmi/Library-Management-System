package com.library.view;

import com.library.model.Book;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.CompoundBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class BookDialog {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final int DIALOG_WIDTH = 700;

    /**
     * Shows an edit dialog for an existing book.
     * @param parent parent component for positioning
     * @param original the book to edit
     * @return edited Book, or null if cancelled
     */
    public static Book showEditBookDialog(Component parent, Book original) {
        JTextField isbnField = createStyledTextField(original.getIsbn());
        JTextField titleField = createStyledTextField(original.getTitle());
        JTextField authorField = createStyledTextField(original.getAuthor());
        JTextField publisherField = createStyledTextField(
                original.getPublisher() != null ? original.getPublisher() : ""
        );
        JTextField categoryField = createStyledTextField(
                original.getCategory() != null ? original.getCategory() : ""
        );
        JTextField publishedDateField = createStyledTextField(
                original.getPublicationDate() != null ?
                        original.getPublicationDate().format(DATE_FORMATTER) : ""
        );
        JTextField totalCopiesField = createStyledTextField(String.valueOf(original.getTotalCopies()));
        JTextField availableCopiesField = createStyledTextField(String.valueOf(original.getAvailableCopies()));

        JPanel mainPanel = createEditAddPanel(isbnField, titleField, authorField,
                publisherField, categoryField, publishedDateField,
                totalCopiesField, availableCopiesField);

        int result = showStyledDialog(parent, mainPanel, "Edit Book");

        if (result != JOptionPane.OK_OPTION) {
            return null;
        }

        return validateAndCreateBook(parent, isbnField, titleField, authorField,
                publisherField, categoryField, publishedDateField,
                totalCopiesField, availableCopiesField, original.getBookId());
    }

    /**
     * Shows an add dialog for a new book.
     * @param parent parent component
     * @return new Book, or null if cancelled
     */
    public static Book showAddDialog(Component parent) {
        JTextField isbnField = createStyledTextField("");
        JTextField titleField = createStyledTextField("");
        JTextField authorField = createStyledTextField("");
        JTextField publisherField = createStyledTextField("");
        JTextField categoryField = createStyledTextField("");
        JTextField publishedDateField = createStyledTextField("");
        JTextField totalCopiesField = createStyledTextField("1");
        JTextField availableCopiesField = createStyledTextField("1");

        JPanel mainPanel = createEditAddPanel(isbnField, titleField, authorField,
                publisherField, categoryField, publishedDateField,
                totalCopiesField, availableCopiesField);

        int result = showStyledDialog(parent, mainPanel, "Add New Book");

        if (result != JOptionPane.OK_OPTION) {
            return null;
        }

        return validateAndCreateBook(parent, isbnField, titleField, authorField,
                publisherField, categoryField, publishedDateField,
                totalCopiesField, availableCopiesField, null);
    }

    /**
     * Shows a details dialog for viewing book information (read-only).
     * @param parent parent component for positioning
     * @param book the book to display
     */
    public static void showDetailsDialog(Component parent, Book book) {
        JPanel mainPanel = new JPanel(new BorderLayout(15, 15));
        mainPanel.setBackground(Theme.AQUA);
        mainPanel.setBorder(new EmptyBorder(25, 25, 25, 25));

        // Header with book title
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(Theme.VIOLET);
        headerPanel.setBorder(new EmptyBorder(15, 20, 15, 20));

        JLabel titleLabel = new JLabel(book.getTitle());
        titleLabel.setFont(Theme.HEADER_FONT);
        titleLabel.setForeground(Color.WHITE);
        headerPanel.add(titleLabel, BorderLayout.CENTER);

        JLabel idLabel = new JLabel("ID: " + book.getBookId());
        idLabel.setFont(Theme.SUB_HEADER_FONT);
        idLabel.setForeground(Theme.AQUA);
        headerPanel.add(idLabel, BorderLayout.EAST);

        mainPanel.add(headerPanel, BorderLayout.NORTH);

        // Content area with two columns
        JPanel contentPanel = new JPanel(new GridLayout(1, 2, 20, 0));
        contentPanel.setBackground(Theme.AQUA);

        // Left column - Basic Information
        JPanel leftPanel = createSectionPanel("Basic Information");
        addDetailRow(leftPanel, "ISBN", book.getIsbn());
        addDetailRow(leftPanel, "Author", book.getAuthor());
        addDetailRow(leftPanel, "Publisher",
                book.getPublisher() != null ? book.getPublisher() : "N/A");
        addDetailRow(leftPanel, "Category",
                book.getCategory() != null ? book.getCategory() : "N/A");
        addDetailRow(leftPanel, "Published Date",
                book.getPublicationDate() != null ?
                        book.getPublicationDate().format(DATE_FORMATTER) : "N/A");

        // Right column - Availability
        JPanel rightPanel = createSectionPanel("Availability");
        addDetailRow(rightPanel, "Total Copies", String.valueOf(book.getTotalCopies()));
        addDetailRow(rightPanel, "Available", String.valueOf(book.getAvailableCopies()));

        int borrowedCopies = book.getTotalCopies() - book.getAvailableCopies();
        addDetailRow(rightPanel, "Borrowed", String.valueOf(borrowedCopies));

        // Status with visual indicator
        String status = book.getAvailableCopies() > 0 ? "✓ Available" : "✗ All Borrowed";
        Color statusColor = book.getAvailableCopies() > 0 ? new Color(0, 150, 0) : new Color(200, 0, 0);
        addStatusRow(rightPanel, "Status", status, statusColor);

        contentPanel.add(leftPanel);
        contentPanel.add(rightPanel);

        // Add scroll pane for details dialog
        JScrollPane scrollPane = new JScrollPane(contentPanel);
        scrollPane.setBackground(Theme.AQUA);
        scrollPane.getViewport().setBackground(Theme.AQUA);
        scrollPane.setBorder(null);
        scrollPane.setPreferredSize(new Dimension(DIALOG_WIDTH, 450));
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
                "Book Details",
                JOptionPane.PLAIN_MESSAGE
        );
    }

    /**
     * Creates the main panel for edit/add dialogs with two-column layout.
     */
    private static JPanel createEditAddPanel(JTextField isbnField, JTextField titleField,
                                             JTextField authorField, JTextField publisherField,
                                             JTextField categoryField, JTextField publishedDateField,
                                             JTextField totalCopiesField, JTextField availableCopiesField) {
        JPanel mainPanel = new JPanel(new BorderLayout(15, 15));
        mainPanel.setBackground(Theme.AQUA);
        mainPanel.setBorder(new EmptyBorder(25, 25, 25, 25));

        // Content area with two columns wrapped in scroll pane
        JPanel contentPanel = new JPanel(new GridLayout(1, 2, 20, 0));
        contentPanel.setBackground(Theme.AQUA);

        // Left column - Basic Information
        JPanel leftPanel = createSectionPanel("Basic Information");
        addFieldRow(leftPanel, "ISBN", isbnField, "Unique identifier (max 20 chars)");
        addFieldRow(leftPanel, "Title", titleField, "Required field (max 200 chars)");
        addFieldRow(leftPanel, "Author", authorField, "Required field (max 100 chars)");
        addFieldRow(leftPanel, "Publisher", publisherField, "Optional (max 100 chars)");

        // Right column - Additional Details
        JPanel rightPanel = createSectionPanel("Additional Details");
        addFieldRow(rightPanel, "Category", categoryField, "Optional (max 50 chars)");
        addFieldRow(rightPanel, "Published Date", publishedDateField, "Format: YYYY-MM-DD");
        addFieldRow(rightPanel, "Total Copies", totalCopiesField, "Must be ≥ available");
        addFieldRow(rightPanel, "Available Copies", availableCopiesField, "Must be ≤ total");

        contentPanel.add(leftPanel);
        contentPanel.add(rightPanel);

        // Add scroll pane
        JScrollPane scrollPane = new JScrollPane(contentPanel);
        scrollPane.setBackground(Theme.AQUA);
        scrollPane.getViewport().setBackground(Theme.AQUA);
        scrollPane.setBorder(null);
        scrollPane.setPreferredSize(new Dimension(DIALOG_WIDTH, 500));
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
                                    JTextField field, String helperText) {
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
     * Validates all fields and creates a Book object.
     */
    private static Book validateAndCreateBook(Component parent,
                                              JTextField isbnField,
                                              JTextField titleField,
                                              JTextField authorField,
                                              JTextField publisherField,
                                              JTextField categoryField,
                                              JTextField publishedDateField,
                                              JTextField totalCopiesField,
                                              JTextField availableCopiesField,
                                              Integer existingBookId) {
        // Extract and trim values
        String isbn = isbnField.getText().trim();
        String title = titleField.getText().trim();
        String author = authorField.getText().trim();
        String publisher = publisherField.getText().trim();
        String category = categoryField.getText().trim();
        String publishedDateStr = publishedDateField.getText().trim();

        // Validate required fields
        if (isbn.isEmpty() || title.isEmpty() || author.isEmpty()) {
            showError(parent, "ISBN, Title, and Author are required fields.");
            return null;
        }

        // Validate field lengths
        if (isbn.length() > 20) {
            showError(parent, "ISBN must not exceed 20 characters.");
            return null;
        }
        if (title.length() > 200) {
            showError(parent, "Title must not exceed 200 characters.");
            return null;
        }
        if (author.length() > 100) {
            showError(parent, "Author must not exceed 100 characters.");
            return null;
        }
        if (publisher.length() > 100) {
            showError(parent, "Publisher must not exceed 100 characters.");
            return null;
        }
        if (category.length() > 50) {
            showError(parent, "Category must not exceed 50 characters.");
            return null;
        }

        // Validate and parse date
        LocalDate publishedDate = null;
        if (!publishedDateStr.isEmpty()) {
            try {
                publishedDate = LocalDate.parse(publishedDateStr, DATE_FORMATTER);
            } catch (DateTimeParseException e) {
                showError(parent, "Invalid date format. Please use YYYY-MM-DD (e.g., 2024-01-15).");
                return null;
            }
        }

        // Validate total copies
        int totalCopies;
        try {
            totalCopies = Integer.parseInt(totalCopiesField.getText().trim());
            if (totalCopies < 1) {
                showError(parent, "Total copies must be at least 1.");
                return null;
            }
        } catch (NumberFormatException ex) {
            showError(parent, "Total copies must be a valid positive integer.");
            return null;
        }

        // Validate available copies
        int availableCopies;
        try {
            availableCopies = Integer.parseInt(availableCopiesField.getText().trim());
            if (availableCopies < 0) {
                showError(parent, "Available copies cannot be negative.");
                return null;
            }
        } catch (NumberFormatException ex) {
            showError(parent, "Available copies must be a valid non-negative integer.");
            return null;
        }

        // Validate relationship between total and available copies
        if (availableCopies > totalCopies) {
            showError(parent, "Available copies cannot exceed total copies.");
            return null;
        }

        // Create the book
        Book book = new Book(isbn, title, author, publisher);

        if (existingBookId != null) {
            book.setBookId(existingBookId);
        }

        book.setCategory(category.isEmpty() ? null : category);
        book.setPublicationDate(publishedDate);
        book.setTotalCopies(totalCopies);
        book.setAvailableCopies(availableCopies);

        return book;
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