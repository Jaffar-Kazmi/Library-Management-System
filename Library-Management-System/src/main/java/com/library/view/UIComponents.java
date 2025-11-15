package com.library.view;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * Utility class for creating reusable UI components
 */
public class UIComponents {

    /**
     * Creates a styled sidebar button with hover effects
     * @param text Button text
     * @param selected Whether the button is currently selected
     * @param selectedButtonGetter Function to get the currently selected button text
     * @return Styled JButton
     */
    public static JButton createSidebarButton(String text, boolean selected, java.util.function.Supplier<String> selectedButtonGetter) {
        JButton button = new JButton(text);
        button.setFont(Theme.NORMAL_FONT);
        button.setForeground(Theme.VIOLET);
        button.setMaximumSize(new Dimension(230, 50));
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setHorizontalAlignment(SwingConstants.LEFT);
        button.setBorder(new EmptyBorder(10, 30, 10, 10));

        if (selected) {
            button.setOpaque(true);
            button.setBackground(Theme.CYAN);
        }

        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setOpaque(true);
                button.setBackground(Theme.INDIGO);
                button.setForeground(Theme.AQUA);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                if (selectedButtonGetter != null && !button.getText().equals(selectedButtonGetter.get())) {
                    button.setOpaque(false);
                    button.setForeground(Theme.VIOLET);
                }
            }
        });

        return button;
    }

    /**
     * Creates a styled action button with hover effects
     * @param text Button text
     * @param color Background color
     * @return Styled JButton
     */
    public static JButton createActionButton(String text, Color color) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 13));
        button.setForeground(Color.WHITE);
        button.setBackground(color);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setBorder(new EmptyBorder(10, 20, 10, 20));

        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(color.darker());
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(color);
            }
        });

        return button;
    }

    /**
     * Creates a styled menu item for popup menus
     * @param text Menu item text
     * @param iconColor Color for the icon (not used currently but kept for consistency)
     * @return Styled JMenuItem
     */
    public static JMenuItem createMenuItem(String text, Color iconColor) {
        JMenuItem item = new JMenuItem(text);
        item.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        item.setBorder(new EmptyBorder(8, 10, 8, 10));
        item.setBackground(Color.WHITE);
        item.setCursor(new Cursor(Cursor.HAND_CURSOR));

        item.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                item.setBackground(new Color(245, 245, 250));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                item.setBackground(Color.WHITE);
            }
        });

        return item;
    }

    /**
     * Creates a statistics card for dashboard
     * @param title Card title
     * @param value Card value
     * @param icon Emoji icon
     * @param color Icon background color
     * @return Styled JPanel
     */
    public static JPanel createStatCard(String title, String value, String icon, Color color) {
        JPanel card = new JPanel();
        card.setLayout(new BorderLayout(15, 10));
        card.setBackground(Theme.AQUA);
        card.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Icon
        JLabel iconLabel = new JLabel(icon);
        iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 40));
        iconLabel.setOpaque(true);
        iconLabel.setBackground(new Color(color.getRed(), color.getGreen(), color.getBlue(), 30));
        iconLabel.setPreferredSize(new Dimension(70, 70));
        iconLabel.setHorizontalAlignment(SwingConstants.CENTER);

        // Text
        JPanel textPanel = new JPanel();
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
        textPanel.setOpaque(false);

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(Theme.SUB_HEADER_FONT);
        titleLabel.setForeground(new Color(120, 120, 120));

        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        valueLabel.setForeground(Theme.VIOLET);

        textPanel.add(titleLabel);
        textPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        textPanel.add(valueLabel);

        card.add(iconLabel, BorderLayout.WEST);
        card.add(textPanel, BorderLayout.CENTER);

        return card;
    }

    /**
     * Truncates text to specified length with ellipsis
     * @param text Text to truncate
     * @param maxLength Maximum length
     * @return Truncated text
     */
    public static String truncateText(String text, int maxLength) {
        if (text.length() > maxLength) {
            return text.substring(0, maxLength - 3) + "...";
        }
        return text;
    }

    /**
     * Custom rounded border class for modern card designs
     */
    public static class RoundedBorder extends javax.swing.border.AbstractBorder {
        private int radius;
        private Color color;

        public RoundedBorder(int radius, Color color) {
            this.radius = radius;
            this.color = color;
        }

        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.setColor(color);
            g2d.drawRoundRect(x, y, width - 1, height - 1, radius, radius);
            g2d.dispose();
        }

        @Override
        public Insets getBorderInsets(Component c) {
            return new Insets(1, 1, 1, 1);
        }

        @Override
        public Insets getBorderInsets(Component c, Insets insets) {
            insets.left = insets.top = insets.right = insets.bottom = 1;
            return insets;
        }
    }
}