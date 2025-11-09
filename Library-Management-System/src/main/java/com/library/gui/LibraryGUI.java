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
                e-> showLoginPanel(),
                e-> JOptionPane.showMessageDialog(this, "Reader")
        );

        loginPanel = new LoginPanel();

        mainPanel.add(launchPanel, "launch");
        mainPanel.add(loginPanel, "login");

        // Start on launch
        CardLayout layout = (CardLayout) mainPanel.getLayout();
        layout.show(mainPanel, "launch");
    }


    private void showLoginPanel() {
        CardLayout layout = (CardLayout) mainPanel.getLayout();
        layout.show(mainPanel, "login");
        mainPanel.revalidate();
        mainPanel.repaint();
    }

    public JPanel getMainPanel() {
        return mainPanel;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new LibraryGUI().setVisible(true));
    }
}
