package brainventorypackage;

// MyButton: small styled button wrapper used across the UI to
// provide consistent coloring and sizing without repeating styles.

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class MyButton extends JButton {

    protected Color defaultColor;
    protected Color hoverColor;

    public MyButton(String text, Color defaultColor, Color hoverColor) {
        super(text);
        this.defaultColor = defaultColor;
        this.hoverColor = hoverColor;

        setFocusPainted(false);
        setForeground(Color.WHITE);
        setFont(UiTheme.BUTTON_FONT);
        setBorder(BorderFactory.createEmptyBorder(12, 20, 12, 20));
        setContentAreaFilled(false);
        setOpaque(false);
        setHorizontalAlignment(SwingConstants.CENTER);
        setBackground(defaultColor);

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                setBackground(hoverColor);
                repaint();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                setBackground(defaultColor);
                repaint();
            }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);

        g2.setColor(getBackground());
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), 18, 18);

        g2.dispose();
        super.paintComponent(g); // ensures text & children are drawn properly
    }
}

class NavButton extends MyButton {

    public NavButton(String text, Color defaultColor, Color hoverColor) {
        super(text, defaultColor, hoverColor);
        setFont(new Font("Segoe UI", Font.BOLD, 14));
        setHorizontalAlignment(SwingConstants.CENTER);
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);

        if (getModel().isPressed()) {
            g2.setColor(hoverColor.darker());
        } else if (getModel().isRollover()) {
            g2.setColor(hoverColor);
        } else {
            g2.setColor(defaultColor);
        }

        g2.fillRect(0, 0, getWidth(), getHeight());

        g2.dispose();
        super.paintComponent(g); // draw text
    }
}
