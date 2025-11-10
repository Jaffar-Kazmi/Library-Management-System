package com.library.gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class LibraryGUI extends JFrame {
    private JPanel mainPanel;
    private LaunchPanel launchPanel;
    private LoginPanel loginPanel;

    public LibraryGUI() {
        setTitle("Good Books");
        setSize(1000, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setMinimumSize(new Dimension(700, 700));

        mainPanel = new JPanel(new CardLayout());
        add(mainPanel);

        launchPanel = new LaunchPanel(
                e-> showLoginPanel("librarian"),
                e-> showLoginPanel("reader")
        );

        loginPanel = new LoginPanel();

        mainPanel.add(launchPanel, "launch");
        mainPanel.add(loginPanel, "login");

        // Start on launch
        CardLayout layout = (CardLayout) mainPanel.getLayout();
        layout.show(mainPanel, "launch");
    }


    private void showLoginPanel(String role) {
        CardLayout layout = (CardLayout) mainPanel.getLayout();
        layout.show(mainPanel, "login");
        loginPanel.setRole(role);
    }

    public void showDashboardPanel(String role, String username){
        DashboardPanel dashboard = new DashboardPanel(role, username);
        mainPanel.add(dashboard, "dashboard");
        CardLayout layout = (CardLayout) mainPanel.getLayout();
        layout.show(mainPanel, "dashboard");

    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new LibraryGUI().setVisible(true));
    }
}
