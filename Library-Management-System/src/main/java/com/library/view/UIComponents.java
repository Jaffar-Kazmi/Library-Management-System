package com.library.view;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.function.Supplier;

public class UIComponents {

    public static JButton createSidebarButton(String text, boolean isSelected, Supplier<String> getSelectedText) {
        JButton btn = new JButton(text);
        btn.setFont(Theme.NORMAL_FONT);
        btn.setForeground(isSelected ? Theme.AQUA : Theme.VIOLET);
        btn.setBackground(isSelected ? Theme.INDIGO : new Color(0, 0, 0, 0));
        btn.setOpaque(isSelected);
        btn.setBorder(new EmptyBorder(10, 20, 10, 20));
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setMaximumSize(new Dimension(250, 45));

        btn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                if (!btn.getText().equals(getSelectedText.get())) {
                    btn.setOpaque(true);
                    btn.setBackground(new Color(230, 230, 250)); // Light lavender
                    btn.repaint();
                }
            }

            @Override
            public void mouseExited(MouseEvent e) {
                if (!btn.getText().equals(getSelectedText.get())) {
                    btn.setOpaque(false);
                    btn.setBackground(new Color(0, 0, 0, 0));
                    btn.repaint();
                }
            }
        });

        return btn;
    }

    public static JPanel createStatCard(String title, String value, String icon, Color color) {
        JPanel card = new JPanel();
        card.setLayout(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 4, 0, color),
                new EmptyBorder(15, 15, 15, 15)));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        titleLabel.setForeground(Color.GRAY);

        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        valueLabel.setForeground(Theme.VIOLET);

        JLabel iconLabel = new JLabel(icon);
        iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 24));
        iconLabel.setForeground(color);

        JPanel textPanel = new JPanel(new GridLayout(2, 1));
        textPanel.setOpaque(false);
        textPanel.add(titleLabel);
        textPanel.add(valueLabel);

        card.add(textPanel, BorderLayout.CENTER);
        card.add(iconLabel, BorderLayout.EAST);

        return card;
    }

    public static JMenuItem createMenuItem(String text, Color color) {
        JMenuItem item = new JMenuItem(text);
        item.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        item.setForeground(color);
        item.setBackground(Color.WHITE);
        item.setBorder(new EmptyBorder(5, 10, 5, 10));
        return item;
    }

    public static JButton createActionButton(String text, Color color) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 12));
        button.setForeground(Color.WHITE);
        button.setBackground(color);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(120, 35));
        return button;
    }
}
