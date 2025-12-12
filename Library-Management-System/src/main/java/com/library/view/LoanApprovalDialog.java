package com.library.view;

import com.library.model.Reader;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.CompoundBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.time.LocalDate;

public class LoanApprovalDialog extends JDialog {
    private LocalDate borrowDate;
    private LocalDate dueDate;
    private int loanDays = 14; // Default 14 days
    private boolean approved = false;

    public LoanApprovalDialog(JFrame parent) {
        super(parent, "Set Loan Duration", true);
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        setSize(600, 550);
        setLocationRelativeTo(parent);
        getContentPane().setBackground(Theme.AQUA);

        initComponents();
    }

    private void initComponents() {
        JPanel mainPanel = new JPanel(new BorderLayout(15, 15));
        mainPanel.setBackground(Theme.AQUA);
        mainPanel.setBorder(new EmptyBorder(25, 25, 25, 25));

        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(Theme.VIOLET);
        headerPanel.setBorder(new EmptyBorder(15, 20, 15, 20));

        JLabel titleLabel = new JLabel("📚 Book Loan Approval");
        titleLabel.setFont(Theme.HEADER_FONT);
        titleLabel.setForeground(Color.WHITE);
        headerPanel.add(titleLabel, BorderLayout.CENTER);

        mainPanel.add(headerPanel, BorderLayout.NORTH);

        // Content Panel
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(Theme.AQUA);
        contentPanel.setBorder(new EmptyBorder(25, 10, 25, 10));

        // Initialize dates
        borrowDate = LocalDate.now();
        dueDate = borrowDate.plusDays(loanDays);

        // Borrow Date Section
        JPanel borrowSection = createFieldSection("Borrow Date", borrowDate.toString(), true);
        contentPanel.add(borrowSection);
        contentPanel.add(Box.createVerticalStrut(25));

        // Loan Duration Section
        JPanel durationSection = new JPanel();
        durationSection.setLayout(new BoxLayout(durationSection, BoxLayout.Y_AXIS));
        durationSection.setBackground(Theme.AQUA);
        durationSection.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel durationLabel = new JLabel("Loan Duration");
        durationLabel.setFont(Theme.SECONDARY_FONT);
        durationLabel.setForeground(Theme.VIOLET);
        durationLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel durationHelper = new JLabel("Number of days to borrow (1-90 days)");
        durationHelper.setFont(Theme.SUB_HEADER_FONT);
        durationHelper.setForeground(Theme.INDIGO);
        durationHelper.setAlignmentX(Component.LEFT_ALIGNMENT);
        durationHelper.setBorder(new EmptyBorder(3, 0, 8, 0));

        // Spinner Panel
        JPanel spinnerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        spinnerPanel.setBackground(Color.WHITE);
        spinnerPanel.setBorder(new CompoundBorder(
                new LineBorder(Theme.CYAN, 2),
                new EmptyBorder(8, 10, 8, 10)
        ));
        spinnerPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 55));
        spinnerPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JSpinner durationSpinner = new JSpinner(new SpinnerNumberModel(14, 1, 90, 1));
        durationSpinner.setFont(Theme.NORMAL_FONT);
        durationSpinner.setPreferredSize(new Dimension(100, 35));
        ((JSpinner.DefaultEditor) durationSpinner.getEditor()).getTextField().setFont(Theme.NORMAL_FONT);
        ((JSpinner.DefaultEditor) durationSpinner.getEditor()).getTextField().setForeground(Theme.VIOLET);

        JLabel daysLabel = new JLabel(" days");
        daysLabel.setFont(Theme.NORMAL_FONT);
        daysLabel.setForeground(Theme.INDIGO);

        spinnerPanel.add(durationSpinner);
        spinnerPanel.add(daysLabel);

        durationSection.add(durationLabel);
        durationSection.add(durationHelper);
        durationSection.add(spinnerPanel);

        contentPanel.add(durationSection);
        contentPanel.add(Box.createVerticalStrut(25));

        // Due Date Section (initially calculated)
        JTextField dueField = new JTextField(dueDate.toString());
        dueField.setEditable(false);
        dueField.setFont(Theme.NORMAL_FONT);
        dueField.setForeground(Theme.INDIGO);
        dueField.setBackground(Color.WHITE);
        dueField.setBorder(new CompoundBorder(
                new LineBorder(Theme.CYAN, 2),
                new EmptyBorder(12, 10, 12, 10)
        ));
        dueField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 55));
        dueField.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel dueSection = createFieldSectionWithComponent("Due Date", dueField);
        contentPanel.add(dueSection);
        contentPanel.add(Box.createVerticalStrut(10));

        // Update due date when loan days change
        durationSpinner.addChangeListener(e -> {
            loanDays = (Integer) durationSpinner.getValue();
            dueDate = borrowDate.plusDays(loanDays);
            dueField.setText(dueDate.toString());
        });

        mainPanel.add(contentPanel, BorderLayout.CENTER);

        // Buttons Panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonPanel.setBackground(Theme.AQUA);
        buttonPanel.setBorder(new EmptyBorder(10, 0, 0, 0));

        JButton approveBtn = createStyledButton("✓ Approve Loan", Theme.INDIGO);
        approveBtn.addActionListener(e -> {
            loanDays = (Integer) durationSpinner.getValue();
            dueDate = borrowDate.plusDays(loanDays);
            approved = true;
            dispose();
        });

        JButton cancelBtn = createStyledButton("✗ Cancel", new Color(150, 150, 150));
        cancelBtn.addActionListener(e -> {
            approved = false;
            dispose();
        });

        buttonPanel.add(cancelBtn);
        buttonPanel.add(approveBtn);

        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(mainPanel);
    }

    /**
     * Creates a field section with label, helper text, and read-only value.
     */
    private JPanel createFieldSection(String labelText, String value, boolean isReadOnly) {
        JPanel section = new JPanel();
        section.setLayout(new BoxLayout(section, BoxLayout.Y_AXIS));
        section.setBackground(Theme.AQUA);
        section.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel label = new JLabel(labelText);
        label.setFont(Theme.SECONDARY_FONT);
        label.setForeground(Theme.VIOLET);
        label.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel helper = new JLabel("Today's date");
        helper.setFont(Theme.SUB_HEADER_FONT);
        helper.setForeground(Theme.INDIGO);
        helper.setAlignmentX(Component.LEFT_ALIGNMENT);
        helper.setBorder(new EmptyBorder(3, 0, 8, 0));

        JTextField field = new JTextField(value);
        field.setEditable(false);
        field.setFont(Theme.NORMAL_FONT);
        field.setForeground(Theme.INDIGO);
        field.setBackground(Color.WHITE);
        field.setBorder(new CompoundBorder(
                new LineBorder(Theme.CYAN, 2),
                new EmptyBorder(8, 10, 8, 10)
        ));
        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 55));
        field.setAlignmentX(Component.LEFT_ALIGNMENT);

        section.add(label);
        section.add(helper);
        section.add(field);

        return section;
    }

    /**
     * Creates a field section with a custom component.
     */
    private JPanel createFieldSectionWithComponent(String labelText, JComponent component) {
        JPanel section = new JPanel();
        section.setLayout(new BoxLayout(section, BoxLayout.Y_AXIS));
        section.setBackground(Theme.AQUA);
        section.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel label = new JLabel(labelText);
        label.setFont(Theme.SECONDARY_FONT);
        label.setForeground(Theme.VIOLET);
        label.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel helper = new JLabel("Automatically calculated based on loan duration");
        helper.setFont(Theme.SUB_HEADER_FONT);
        helper.setForeground(Theme.INDIGO);
        helper.setAlignmentX(Component.LEFT_ALIGNMENT);
        helper.setBorder(new EmptyBorder(3, 0, 8, 0));

        component.setAlignmentX(Component.LEFT_ALIGNMENT);

        section.add(label);
        section.add(helper);
        section.add(component);

        return section;
    }

    /**
     * Creates a styled button with theme colors.
     */
    private JButton createStyledButton(String text, Color bgColor) {
        JButton button = new JButton(text);
        button.setFont(Theme.SECONDARY_FONT);
        button.setForeground(Color.WHITE);
        button.setBackground(bgColor);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setOpaque(true);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(180, 45));

        // Hover effect
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(bgColor.darker());
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(bgColor);
            }
        });

        return button;
    }

    public static LoanApprovalResult showDialog(JFrame parent) {
        LoanApprovalDialog dialog = new LoanApprovalDialog(parent);
        dialog.setVisible(true);

        if (dialog.approved) {
            return new LoanApprovalResult(
                    dialog.borrowDate,
                    dialog.dueDate,
                    dialog.loanDays
            );
        }
        return null;
    }

    public static class LoanApprovalResult {
        private final LocalDate borrowDate;
        private final LocalDate dueDate;
        private final int loanDays;

        public LoanApprovalResult(LocalDate borrowDate, LocalDate dueDate, int loanDays) {
            this.borrowDate = borrowDate;
            this.dueDate = dueDate;
            this.loanDays = loanDays;
        }

        public LocalDate getBorrowDate() {
            return borrowDate;
        }

        public LocalDate getDueDate() {
            return dueDate;
        }

        public int getLoanDays() {
            return loanDays;
        }
    }
}