package com.library.gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LaunchPanel extends GradientPanel {
    private PrimaryButton librarianButton;
    private SecondaryButton readerButton;

    public LaunchPanel(ActionListener librarianAction, ActionListener readerAction){
        // --- Main launchPanel ---
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setPreferredSize(new Dimension(1000, 800));

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
        librarianButton.addActionListener(librarianAction);

        // Reader Button
        SecondaryButton readerButton = new SecondaryButton("  Reader  ");
        readerButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        readerButton.setToolTipText("Access as a reader to explore books");
        readerButton.addActionListener(readerAction);

        JLabel footer = new JLabel("Developed by Jaffar Raza Kazmi © 2025");
        footer.setFont(new Font("Arial", Font.ITALIC, 14));
        footer.setForeground(new Color(110, 90, 140));
        footer.setAlignmentX(Component.CENTER_ALIGNMENT);


        add(Box.createRigidArea(new Dimension(0, 70))); // Top Margin
        add(title);
        add(Box.createRigidArea(new Dimension(0, 10)));
        add(subtitle);
        add(Box.createRigidArea(new Dimension(0, 30)));
        add(separator);
        add(Box.createRigidArea(new Dimension(0, 70)));
        add(librarianButton);
        add(Box.createRigidArea(new Dimension(0, 50)));
        add(readerButton);
        add(Box.createVerticalGlue());
        add(footer);
        add(Box.createRigidArea(new Dimension(0, 20)));
    }
}
