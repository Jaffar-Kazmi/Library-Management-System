package com.library.view;

import com.library.controller.LoginController;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class LoginPanel extends GradientPanel {
    private JLabel title;

    private JPanel usernamePanel;
    private JLabel usernameLabel;
    private JTextField usernameField;

    private JPanel passPanel;
    private JLabel passLabel;
    private JPasswordField passField;

    private JPanel btnPanel;
    private PrimaryButton loginButton;
    private SecondaryButton backButton;
    private LoginController controller;
    private String userType; // "Librarian" or "Reader"

    public LoginPanel(String type) {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBorder(BorderFactory.createEmptyBorder(30, 60, 30, 60));

        // --- Title ---
        title = new JLabel("\uD83D\uDCDA Good Books Library");
        title.setFont(Theme.PRIMARY_FONT);
        title.setForeground(Theme.VIOLET);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        this.userType = type;

        // --- Username Field ---
        usernamePanel = new JPanel();
        usernamePanel.setOpaque(false);
        usernamePanel.setLayout(new BoxLayout(usernamePanel, BoxLayout.X_AXIS));

        usernameLabel = new JLabel("Username");
        usernameLabel.setFont(Theme.SECONDARY_FONT);
        usernameLabel.setForeground(Theme.VIOLET);

        usernameField = new JTextField(15);
        usernameField.setFont(Theme.SECONDARY_FONT);
        usernameField.setMaximumSize(new Dimension(250, 35));
        usernameField.setMargin(new Insets(5, 10, 5, 10));

        usernamePanel.add(usernameLabel);
        usernamePanel.add(Box.createRigidArea(new Dimension(10, 0)));
        usernamePanel.add(usernameField);
        usernamePanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // --- Password Field ---
        passPanel = new JPanel();
        passPanel.setOpaque(false);
        passPanel.setLayout(new BoxLayout(passPanel, BoxLayout.X_AXIS));

        passLabel = new JLabel("Password");
        passLabel.setFont(Theme.SECONDARY_FONT);
        passLabel.setForeground(Theme.VIOLET);

        passField = new JPasswordField(15);
        passField.setFont(Theme.SECONDARY_FONT);
        passField.setMaximumSize(new Dimension(250, 35));
        passField.setMargin(new Insets(5, 10, 5, 10));

        passPanel.add(passLabel);
        passPanel.add(Box.createRigidArea(new Dimension(10, 0)));
        passPanel.add(passField);
        passPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // --- Buttons ---
        btnPanel = new JPanel();
        btnPanel.setOpaque(false);
        btnPanel.setLayout(new BoxLayout(btnPanel, BoxLayout.X_AXIS));

        backButton = new SecondaryButton("Back");
        loginButton = new PrimaryButton("Login");

        Dimension btnSize = new Dimension(100, 35);
        backButton.setMaximumSize(btnSize);
        loginButton.setMaximumSize(btnSize);

        btnPanel.add(backButton);
        btnPanel.add(Box.createRigidArea(new Dimension(20, 0)));
        btnPanel.add(loginButton);
        btnPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // --- Add components with spacing ---
        add(Box.createRigidArea(new Dimension(0, 70)));
        add(title);
        add(Box.createRigidArea(new Dimension(0, 50)));

        add(usernamePanel);
        add(Box.createRigidArea(new Dimension(0, 20)));
        add(passPanel);
        add(Box.createRigidArea(new Dimension(0, 30)));
        add(btnPanel);

        loginButton.addActionListener((ActionEvent e) -> {
            if (controller != null) {
                String username = usernameField.getText();
                String password = new String(passField.getPassword());
                controller.handleLogin(username, password, userType);
            }
        });

        backButton.addActionListener((ActionEvent e) -> {
            Container parent = getParent();
            CardLayout layout = (CardLayout) parent.getLayout();
            layout.show(parent, "launch");
        });

    }

    public void clearPassword() {
        passField.setText("");
    }

    public void clearAll() {
        usernameField.setText("");
        passField.setText("");
    }

    public void setController(LoginController controller){
        this.controller = controller;
    }
}
