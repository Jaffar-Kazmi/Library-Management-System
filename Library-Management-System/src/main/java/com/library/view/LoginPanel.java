package com.library.view;

import com.library.controller.LoginController;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionListener;

public class LoginPanel extends JPanel {

    private String userType;
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JButton backButton;
    private LoginController controller;

    public LoginPanel(String userType) {
        this.userType = userType;
        setLayout(new BorderLayout());
        setBackground(Theme.AQUA);

        initializeComponents();
    }

    private void initializeComponents() {
        // Center Panel
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setBackground(Color.WHITE);
        centerPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Theme.INDIGO, 2, true),
                new EmptyBorder(40, 40, 40, 40)));
        centerPanel.setMaximumSize(new Dimension(400, 450));

        // Icon
        JLabel iconLabel = new JLabel(userType.equals("Librarian") ? "👨‍💼" : "📖");
        iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 64));
        iconLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Title
        JLabel titleLabel = new JLabel(userType + " Login");
        titleLabel.setFont(Theme.HEADER_FONT);
        titleLabel.setForeground(Theme.VIOLET);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Fields
        usernameField = new JTextField(20);
        usernameField.setMaximumSize(new Dimension(300, 40));
        styleTextField(usernameField);

        passwordField = new JPasswordField(20);
        passwordField.setMaximumSize(new Dimension(300, 40));
        styleTextField(passwordField);

        // Buttons
        loginButton = new JButton("Login");
        styleButton(loginButton, Theme.INDIGO);
        loginButton.setMaximumSize(new Dimension(300, 45));

        backButton = new JButton("Back");
        styleButton(backButton, Color.GRAY);
        backButton.setMaximumSize(new Dimension(300, 45));

        // Labels
        JLabel userLabel = new JLabel("Username");
        userLabel.setFont(Theme.NORMAL_FONT);
        userLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel passLabel = new JLabel("Password");
        passLabel.setFont(Theme.NORMAL_FONT);
        passLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Add to center panel
        centerPanel.add(iconLabel);
        centerPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        centerPanel.add(titleLabel);
        centerPanel.add(Box.createRigidArea(new Dimension(0, 30)));

        centerPanel.add(userLabel);
        centerPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        centerPanel.add(usernameField);
        centerPanel.add(Box.createRigidArea(new Dimension(0, 15)));

        centerPanel.add(passLabel);
        centerPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        centerPanel.add(passwordField);
        centerPanel.add(Box.createRigidArea(new Dimension(0, 30)));

        centerPanel.add(loginButton);
        centerPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        centerPanel.add(backButton);

        // Wrapper for centering
        JPanel wrapper = new JPanel(new GridBagLayout());
        wrapper.setBackground(Theme.AQUA);
        wrapper.add(centerPanel);

        add(wrapper, BorderLayout.CENTER);
    }

    private void styleTextField(JTextField field) {
        field.setFont(Theme.NORMAL_FONT);
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Theme.VIOLET, 1),
                new EmptyBorder(5, 10, 5, 10)));
    }

    private void styleButton(JButton btn, Color bg) {
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
    }

    public void setController(LoginController controller) {
        this.controller = controller;
        loginButton.addActionListener(e -> controller.handleLogin(getUsername(), getPassword(), userType));
    }

    public void addBackListener(ActionListener l) {
        backButton.addActionListener(l);
    }

    public String getUsername() {
        return usernameField.getText();
    }

    public String getPassword() {
        return new String(passwordField.getPassword());
    }

    public void clearAll() {
        usernameField.setText("");
        passwordField.setText("");
    }

    public void clearPassword() {
        passwordField.setText("");
    }

    public void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Login Error", JOptionPane.ERROR_MESSAGE);
    }
}
