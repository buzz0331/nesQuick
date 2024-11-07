package client.ui.icon;

import javax.swing.*;
import java.awt.*;

public class ArrowIcon implements Icon {
    private final int size;
    private final Color color;

    public ArrowIcon(int size, Color color) {
        this.size = size;
        this.color = color;
    }

    @Override
    public void paintIcon(Component c, Graphics g, int x, int y) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(color);

        // 좌측 방향 화살표 모양
        int[] xPoints = {x + size, x, x + size};
        int[] yPoints = {y, y + size / 2, y + size};
        g2.fillPolygon(xPoints, yPoints, 3);

        g2.dispose();
    }

    @Override
    public int getIconWidth() {
        return size;
    }

    @Override
    public int getIconHeight() {
        return size;
    }
}

