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

        JPanel panel = createStyledPanel();

        addFieldRow(panel, "ISBN:", isbnField, "Unique identifier (max 20 chars)");
        addFieldRow(panel, "Title:", titleField, "Required field (max 200 chars)");
        addFieldRow(panel, "Author:", authorField, "Required field (max 100 chars)");
        addFieldRow(panel, "Publisher:", publisherField, "Optional (max 100 chars)");
        addFieldRow(panel, "Category:", categoryField, "Optional (max 50 chars)");
        addFieldRow(panel, "Published Date:", publishedDateField, "Format: YYYY-MM-DD");
        addFieldRow(panel, "Total Copies:", totalCopiesField, "Must be ≥ available copies");
        addFieldRow(panel, "Available Copies:", availableCopiesField, "Must be ≤ total copies");

        int result = showStyledDialog(parent, panel, "Edit Book");

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

        JPanel panel = createStyledPanel();

        addFieldRow(panel, "ISBN:", isbnField, "Unique identifier (max 20 chars)");
        addFieldRow(panel, "Title:", titleField, "Required field (max 200 chars)");
        addFieldRow(panel, "Author:", authorField, "Required field (max 100 chars)");
        addFieldRow(panel, "Publisher:", publisherField, "Optional (max 100 chars)");
        addFieldRow(panel, "Category:", categoryField, "Optional (max 50 chars)");
        addFieldRow(panel, "Published Date:", publishedDateField, "Format: YYYY-MM-DD");
        addFieldRow(panel, "Total Copies:", totalCopiesField, "Must be ≥ available copies");
        addFieldRow(panel, "Available Copies:", availableCopiesField, "Must be ≤ total copies");

        int result = showStyledDialog(parent, panel, "Add New Book");

        if (result != JOptionPane.OK_OPTION) {
            return null;
        }

        return validateAndCreateBook(parent, isbnField, titleField, authorField,
                publisherField, categoryField, publishedDateField,
                totalCopiesField, availableCopiesField, null);
    }

    /**
     * Creates a styled text field with theme colors and fonts.
     */
    private static JTextField createStyledTextField(String text) {
        JTextField field = new JTextField(text, 20);
        field.setFont(Theme.NORMAL_FONT);
        field.setForeground(Theme.VIOLET);
        field.setCaretColor(Theme.INDIGO);
        field.setBorder(new CompoundBorder(
                new LineBorder(Theme.CYAN, 2),
                new EmptyBorder(5, 8, 5, 8)
        ));
        return field;
    }

    /**
     * Creates a styled panel with proper layout and theme colors.
     */
    private static JPanel createStyledPanel() {
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBackground(Theme.AQUA);
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        JPanel fieldsPanel = new JPanel(new GridBagLayout());
        fieldsPanel.setBackground(Theme.AQUA);
        mainPanel.add(fieldsPanel, BorderLayout.CENTER);

        return fieldsPanel;
    }

    /**
     * Shows a styled dialog with custom buttons.
     */
    private static int showStyledDialog(Component parent, JPanel panel, String title) {
        // Create custom option pane
        JOptionPane optionPane = new JOptionPane(
                panel,
                JOptionPane.PLAIN_MESSAGE,
                JOptionPane.OK_CANCEL_OPTION
        );

        // Style the buttons
        UIManager.put("OptionPane.background", Theme.AQUA);
        UIManager.put("Panel.background", Theme.AQUA);
        UIManager.put("Button.background", Theme.INDIGO);
        UIManager.put("Button.foreground", Color.WHITE);
        UIManager.put("Button.font", Theme.SECONDARY_FONT);

        JDialog dialog = optionPane.createDialog(parent, title);
        dialog.getContentPane().setBackground(Theme.AQUA);

        // Style dialog title
        dialog.setFont(Theme.HEADER_FONT);

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
     * Adds a labeled field row with helper text to the panel.
     */
    private static void addFieldRow(JPanel panel, String labelText,
                                    JTextField field, String helperText) {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(8, 5, 2, 5);

        int row = panel.getComponentCount() / 3;

        // Label
        JLabel label = new JLabel(labelText);
        label.setFont(Theme.SECONDARY_FONT);
        label.setForeground(Theme.VIOLET);
        label.setBackground(Theme.AQUA);
        label.setOpaque(true);
        gbc.gridx = 0;
        gbc.gridy = row * 2;
        gbc.weightx = 0.3;
        gbc.anchor = GridBagConstraints.WEST;
        panel.add(label, gbc);

        // Field
        gbc.gridx = 1;
        gbc.weightx = 0.7;
        panel.add(field, gbc);

        // Helper text
        JLabel helper = new JLabel(helperText);
        helper.setFont(Theme.SUB_HEADER_FONT);
        helper.setForeground(Theme.INDIGO);
        helper.setBackground(Theme.AQUA);
        helper.setOpaque(true);
        gbc.gridx = 1;
        gbc.gridy = row * 2 + 1;
        gbc.insets = new Insets(0, 5, 8, 5);
        panel.add(helper, gbc);
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