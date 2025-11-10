package com.library.gui;

import java.awt.*;
import java.awt.geom.*;
import javax.swing.*;

public class GradientPanel extends JPanel {
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        int w = getWidth();
        int h = getHeight();
        // Set fractions: 0=start, 0.5=middle, 1=end
        float[] fractions = {0.0f, 0.5f, 1.0f};
        Color[] colors = {Theme.AQUA, Theme.CYAN, Theme.INDIGO};

        LinearGradientPaint paint = new LinearGradientPaint(
                new Point2D.Float(0, 0),
                new Point2D.Float(w, h),
                fractions,
                colors
        );
        g2d.setPaint(paint);
        g2d.fillRect(0, 0, w, h);
    }
}
