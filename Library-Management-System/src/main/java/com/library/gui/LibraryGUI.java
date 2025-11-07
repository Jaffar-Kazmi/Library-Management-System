package com.library.gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class LibraryGUI extends JFrame {
    public LibraryGUI() {
        setTitle("Good Books");
        setSize(1000, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // --- Main Panel ---
        GradientPanel panel = new GradientPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        // Title
        JLabel title = new JLabel("\uD83D\uDCDA Good Books Library");
        title.setFont(new Font("Arial", Font.BOLD, 44));
        title.setForeground(new Color(71, 52, 114));
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Subtitle
        JLabel subtitle = new JLabel("Digital Library Management System");
        subtitle.setFont(new Font("Arial", Font.ITALIC, 18));
        subtitle.setForeground(new Color(71, 52, 114));
        subtitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        JSeparator separator = new JSeparator(SwingConstants.HORIZONTAL);
        separator.setMaximumSize(new Dimension(400, 2));
        separator.setForeground(new Color(150, 130, 200));

        // Librarian Button
        PrimaryButton librarianButton = new PrimaryButton("Librarian");
        librarianButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        librarianButton.setToolTipText("Login as librarian to manage library resources");

        // Reader Button
        SecondaryButton readerButton = new SecondaryButton("  Reader  ");
        readerButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        readerButton.setToolTipText("Access as a reader to explore books");

        JLabel footer = new JLabel("Developed by Jaffar Raza Kazmi © 2025");
        footer.setFont(new Font("Arial", Font.ITALIC, 14));
        footer.setForeground(new Color(110, 90, 140));
        footer.setAlignmentX(Component.CENTER_ALIGNMENT);


        panel.add(Box.createRigidArea(new Dimension(0, 70))); // Top Margin
        panel.add(title);
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        panel.add(subtitle);
        panel.add(Box.createRigidArea(new Dimension(0, 30)));
        panel.add(separator);
        panel.add(Box.createRigidArea(new Dimension(0, 70)));
        panel.add(librarianButton);
        panel.add(Box.createRigidArea(new Dimension(0, 50)));
        panel.add(readerButton);
        panel.add(Box.createVerticalGlue());
        panel.add(footer);
        panel.add(Box.createRigidArea(new Dimension(0, 20)));


        // Action Listeners
        librarianButton.addActionListener((ActionEvent e) -> {
            JOptionPane.showMessageDialog(this, "Librarian module coming soon!");
        });

        readerButton.addActionListener((ActionEvent e) -> {
            JOptionPane.showMessageDialog(this, "Reader module coming soon!");
        });

        add(panel);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new LibraryGUI().setVisible(true));
    }
}
