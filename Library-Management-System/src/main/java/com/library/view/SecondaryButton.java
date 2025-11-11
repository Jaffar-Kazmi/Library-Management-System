package com.library.view;

// SecondaryButton.java
import javax.swing.*;
import java.awt.*;

public class SecondaryButton extends JButton {
    public SecondaryButton(String text) {
        super(text);
        setFont(new Font("Segoe UI", Font.BOLD, 24));
        setBackground(new Color(214, 244, 237));
        setForeground(new Color(71, 52, 114));
        setPreferredSize(new Dimension(120, 40));
        setFocusPainted(false);

        addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                setBackground(new Color(71, 52, 114));
                setForeground(new Color(214, 244, 237));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                setBackground(new Color(214, 244, 237));
                setForeground(new Color(71, 52, 114));
            }
        });
    }
}
