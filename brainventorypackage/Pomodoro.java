package brainventorypackage;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class Pomodoro extends JPanel {

    int remainingTimeInSeconds = 25 * 60;
    int displayMinutes = remainingTimeInSeconds / 60;
    int displaySeconds = remainingTimeInSeconds % 60;
    boolean isTimerOn = false;

    public Pomodoro() {

        // MAIN PANEL 
        setLayout(new BorderLayout());
        setBackground(new Color(33, 36, 43)); // dark gray background
        setBorder(BorderFactory.createEmptyBorder(40, 40, 40, 40));

        
        // timer
        JLabel pomoTimer = new JLabel(String.format("%d:%02d", displayMinutes, displaySeconds));
        pomoTimer.setFont(new Font("Segoe UI", Font.BOLD, 60));
        pomoTimer.setHorizontalAlignment(SwingConstants.CENTER);
        pomoTimer.setForeground(new Color(220, 220, 220)); // light gray text

        // panel for timer
        JPanel timerCard = new JPanel();
        timerCard.setBackground(new Color(45, 48, 56));
        timerCard.setBorder(BorderFactory.createEmptyBorder(30, 20, 30, 20));
        timerCard.setLayout(new BorderLayout());
        timerCard.add(pomoTimer, BorderLayout.CENTER);
        

        // ROUND the panel using a custom UI delegate
        timerCard.setOpaque(false);
        JPanel roundedWrapper = new RoundedPanel(35, new Color(45, 48, 56));
        roundedWrapper.setLayout(new BorderLayout());
        roundedWrapper.add(timerCard);

        
        add(roundedWrapper, BorderLayout.NORTH);

        
        // add working timer
        Timer timer = new Timer(1000, e -> {
            pomoTimer.setText(String.format("%d:%02d", displayMinutes, displaySeconds));
            remainingTimeInSeconds--;
            displayMinutes = remainingTimeInSeconds / 60;
            displaySeconds = remainingTimeInSeconds % 60;
            if (remainingTimeInSeconds == 0) {
                ((Timer)e.getSource()).stop();
            }
        });

        
        // add buttons
        JPanel buttonsPanel = new JPanel(new GridLayout(1, 3, 20, 0));
        buttonsPanel.setBackground(new Color(33, 36, 43)); // match background
        
        MyButton btnStart = new MyButton("Start", new Color(60, 63, 74), new Color(80, 83, 95));
        MyButton btnPause = new MyButton("Pause", new Color(60, 63, 74), new Color(80, 83, 95));
        MyButton btnReset = new MyButton("Reset", new Color(60, 63, 74), new Color(80, 83, 95));

        btnStart.setPreferredSize(new Dimension(140, 45));
        btnPause.setPreferredSize(new Dimension(140, 45));
        btnReset.setPreferredSize(new Dimension(140, 45));

        buttonsPanel.add(btnStart);
        buttonsPanel.add(btnPause);
        buttonsPanel.add(btnReset);

        add(buttonsPanel, BorderLayout.SOUTH);

        // ACTIONS
        btnStart.addActionListener(e -> timer.start());
        btnPause.addActionListener(e -> timer.stop());
        btnReset.addActionListener(e -> {
            timer.stop();
            remainingTimeInSeconds = 25 * 60;
            displayMinutes = remainingTimeInSeconds / 60;
            displaySeconds = remainingTimeInSeconds % 60;
            pomoTimer.setText(String.format("%d:%02d", displayMinutes, displaySeconds));
        });
    }


    // ROUNDED PANEL CLASS (for modern UI cards)
    static class RoundedPanel extends JPanel {
        private int cornerRadius;
        private Color bgColor;

        public RoundedPanel(int radius, Color bgColor) {
            this.cornerRadius = radius;
            this.bgColor = bgColor;
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(bgColor);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), cornerRadius, cornerRadius);
        }
    }
}
