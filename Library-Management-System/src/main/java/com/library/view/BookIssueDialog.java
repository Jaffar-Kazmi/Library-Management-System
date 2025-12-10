package com.library.view;

import com.library.model.Book;
import com.library.model.User;
import com.library.service.UserService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class BookIssueDialog extends JDialog {

    private JComboBox<String> readerComboBox;
    private JTextField borrowDateField;
    private JTextField dueDateField;
    private JSpinner daysSpinner;
    private JTextArea bookDetailsArea;

    private Book book;
    private User selectedReader;
    private LocalDate borrowDate;
    private LocalDate dueDate;

    private boolean confirmed = false;
    private UserService userService;

    public BookIssueDialog(Frame parent, Book book) {
        super(parent, "Issue Book", true);
        this.book = book;
        this.userService = new UserService();

        setSize(500, 550);
        setLocationRelativeTo(parent);
        setResizable(false);

        initComponents();
    }

    private void initComponents() {
        JPanel mainPanel = new JPanel(new BorderLayout(15, 15));
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        mainPanel.setBackground(Theme.AQUA);

        // Title
        JLabel titleLabel = new JLabel("Issue Book to Reader");
        titleLabel.setFont(Theme.HEADER_FONT);
        titleLabel.setForeground(Theme.VIOLET);

        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        titlePanel.setOpaque(false);
        titlePanel.add(titleLabel);

        // Book details section
        JPanel bookPanel = new JPanel(new BorderLayout(10, 10));
        bookPanel.setOpaque(false);
        bookPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Theme.INDIGO, 2),
                "Book Details",
                0, 0, Theme.SUB_HEADER_FONT, Theme.VIOLET
        ));

        bookDetailsArea = new JTextArea(4, 30);
        bookDetailsArea.setEditable(false);
        bookDetailsArea.setFont(Theme.NORMAL_FONT);
        bookDetailsArea.setBackground(Color.WHITE);
        bookDetailsArea.setBorder(new EmptyBorder(10, 10, 10, 10));
        bookDetailsArea.setText(
                "ISBN: " + book.getIsbn() + "\n" +
                        "Title: " + book.getTitle() + "\n" +
                        "Author: " + book.getAuthor() + "\n" +
                        "Available Copies: " + book.getAvailableCopies()
        );

        bookPanel.add(new JScrollPane(bookDetailsArea), BorderLayout.CENTER);

        // Form panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setOpaque(false);
        formPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Theme.INDIGO, 2),
                "Issue Details",
                0, 0, Theme.SUB_HEADER_FONT, Theme.VIOLET
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 10, 10, 10);

        // Reader selection
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0.3;
        JLabel readerLabel = new JLabel("Select Reader:");
        readerLabel.setFont(Theme.SUB_HEADER_FONT);
        formPanel.add(readerLabel, gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.7;
        readerComboBox = new JComboBox<>();
        readerComboBox.setFont(Theme.NORMAL_FONT);
        readerComboBox.setBackground(Color.WHITE);
        loadReaders();
        formPanel.add(readerComboBox, gbc);

        // Borrow date (today by default)
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0.3;
        JLabel borrowLabel = new JLabel("Borrow Date:");
        borrowLabel.setFont(Theme.SUB_HEADER_FONT);
        formPanel.add(borrowLabel, gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.7;
        borrowDateField = new JTextField();
        borrowDateField.setFont(Theme.NORMAL_FONT);
        borrowDateField.setEditable(false);
        borrowDateField.setText(LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE));
        borrowDateField.setBackground(new Color(240, 240, 240));
        formPanel.add(borrowDateField, gbc);

        // Loan duration
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weightx = 0.3;
        JLabel durationLabel = new JLabel("Loan Duration (days):");
        durationLabel.setFont(Theme.SUB_HEADER_FONT);
        formPanel.add(durationLabel, gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.7;
        SpinnerNumberModel spinnerModel = new SpinnerNumberModel(14, 1, 90, 1);
        daysSpinner = new JSpinner(spinnerModel);
        daysSpinner.setFont(Theme.NORMAL_FONT);
        daysSpinner.addChangeListener(e -> updateDueDate());
        formPanel.add(daysSpinner, gbc);

        // Due date
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.weightx = 0.3;
        JLabel dueLabel = new JLabel("Due Date:");
        dueLabel.setFont(Theme.SUB_HEADER_FONT);
        formPanel.add(dueLabel, gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.7;
        dueDateField = new JTextField();
        dueDateField.setFont(Theme.NORMAL_FONT);
        dueDateField.setEditable(false);
        dueDateField.setBackground(new Color(240, 240, 240));
        updateDueDate();
        formPanel.add(dueDateField, gbc);

        // Buttons panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonPanel.setOpaque(false);

        JButton issueButton = UIComponents.createActionButton("Issue Book", Theme.INDIGO);
        JButton cancelButton = UIComponents.createActionButton("Cancel", new Color(120, 120, 120));

        issueButton.addActionListener(e -> onIssue());
        cancelButton.addActionListener(e -> onCancel());

        buttonPanel.add(cancelButton);
        buttonPanel.add(issueButton);

        // Layout
        mainPanel.add(titlePanel, BorderLayout.NORTH);

        JPanel centerPanel = new JPanel(new BorderLayout(10, 10));
        centerPanel.setOpaque(false);
        centerPanel.add(bookPanel, BorderLayout.NORTH);
        centerPanel.add(formPanel, BorderLayout.CENTER);

        mainPanel.add(centerPanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(mainPanel);
    }

    private void loadReaders() {
        List<User> readers = userService.findAllReaders();

        if (readers.isEmpty()) {
            readerComboBox.addItem("No readers available");
            return;
        }

        for (User reader : readers) {
            String displayText = reader.getId() + " - " + reader.getFullName() + " (" + reader.getUsername() + ")";
            readerComboBox.addItem(displayText);
        }
    }

    private void updateDueDate() {
        int days = (Integer) daysSpinner.getValue();
        LocalDate due = LocalDate.now().plusDays(days);
        dueDateField.setText(due.format(DateTimeFormatter.ISO_LOCAL_DATE));
    }

    private void onIssue() {
        // Validate selection
        if (readerComboBox.getSelectedIndex() == -1 || readerComboBox.getItemCount() == 0) {
            JOptionPane.showMessageDialog(this,
                    "Please select a reader.",
                    "Validation Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        String selected = (String) readerComboBox.getSelectedItem();
        if (selected == null || selected.equals("No readers available")) {
            JOptionPane.showMessageDialog(this,
                    "No readers available in the system.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Extract reader ID from selection
        int readerId = Integer.parseInt(selected.split(" - ")[0]);
        selectedReader = userService.findById(readerId);

        if (selectedReader == null) {
            JOptionPane.showMessageDialog(this,
                    "Selected reader not found.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Set dates
        borrowDate = LocalDate.now();
        int days = (Integer) daysSpinner.getValue();
        dueDate = borrowDate.plusDays(days);

        // Confirm
        int confirm = JOptionPane.showConfirmDialog(this,
                "Issue this book to:\n\n" +
                        "Reader: " + selectedReader.getFullName() + "\n" +
                        "Book: " + book.getTitle() + "\n" +
                        "Due Date: " + dueDate + "\n\n" +
                        "Continue?",
                "Confirm Issue",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            confirmed = true;
            dispose();
        }
    }

    private void onCancel() {
        confirmed = false;
        dispose();
    }

    public static IssueResult showDialog(Frame parent, Book book) {
        BookIssueDialog dialog = new BookIssueDialog(parent, book);
        dialog.setVisible(true);

        if (dialog.confirmed) {
            return new IssueResult(
                    dialog.selectedReader,
                    dialog.borrowDate,
                    dialog.dueDate
            );
        }
        return null;
    }

    // Result class to return issue details
    public static class IssueResult {
        private final User reader;
        private final LocalDate borrowDate;
        private final LocalDate dueDate;

        public IssueResult(User reader, LocalDate borrowDate, LocalDate dueDate) {
            this.reader = reader;
            this.borrowDate = borrowDate;
            this.dueDate = dueDate;
        }

        public User getReader() {
            return reader;
        }

        public LocalDate getBorrowDate() {
            return borrowDate;
        }

        public LocalDate getDueDate() {
            return dueDate;
        }
    }
}
