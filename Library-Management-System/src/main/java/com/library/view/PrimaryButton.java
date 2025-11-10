package com.library.gui;

// PrimaryButton.java
import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;

public class PrimaryButton extends JButton {
    public PrimaryButton(String text) {
        super(text);
        setFont(new Font("Segoe UI", Font.BOLD, 24));
        setBackground(new Color(71, 52, 114));     // blue
        setForeground(new Color(214, 244, 237));
        setFocusPainted(false);
        setBorderPainted(false);
        setPreferredSize(new Dimension(120, 40));

        addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                setBackground(new Color(214, 244, 237));
                setForeground(new Color(71, 52, 114));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                setBackground(new Color(71, 52, 114));
                setForeground(new Color(214, 244, 237));
            }
        });

    }
}
