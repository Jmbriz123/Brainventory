package brainventorypackage;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Pomodoro extends RoundedPanel {

    private SessionManager sessionManager;
    // ---- CONFIG ----
    private static final int FOCUS_DURATION = 25 * 60;
    private static final int BREAK_DURATION = 5 * 60;

    private enum Mode { FOCUS, BREAK }

    private Mode currentMode = Mode.FOCUS;
    private int remainingTime = FOCUS_DURATION;
    private boolean isRunning = false;

    // track elapsed seconds for focus session (pauses/resumes accounted)
    private int focusElapsedSeconds = 0;

    // ---- UI ----
    private JLabel timerLabel;
    private JLabel modeLabel;
    private Timer timer;

    public Pomodoro(SessionManager sessionManager) {
        super(20, new Color(33, 36, 43));
        this.sessionManager = sessionManager;
        setLayout(new BorderLayout(0, 24));
        setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

        // ===== MODE LABEL =====
        modeLabel = new JLabel("Focus");
        modeLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        modeLabel.setHorizontalAlignment(SwingConstants.CENTER);
        modeLabel.setForeground(new Color(108, 203, 119)); // green

        // ===== TIMER LABEL =====
        timerLabel = new JLabel(formatTime(remainingTime));
        timerLabel.setFont(new Font("Segoe UI", Font.BOLD, 56));
        timerLabel.setHorizontalAlignment(SwingConstants.CENTER);
        timerLabel.setForeground(new Color(230, 230, 230));

        JPanel timerCard = new RoundedPanel(20, new Color(45, 48, 56));
        timerCard.setLayout(new BorderLayout(0, 8));
        timerCard.setBorder(BorderFactory.createEmptyBorder(24, 20, 24, 20));
        timerCard.add(modeLabel, BorderLayout.NORTH);
        timerCard.add(timerLabel, BorderLayout.CENTER);

        add(timerCard, BorderLayout.CENTER);

        // ===== BUTTONS =====
        JPanel buttonsPanel = new JPanel(new GridLayout(1, 3, 12, 0));
        buttonsPanel.setBackground(new Color(33, 36, 43));

        MyButton btnStart = new MyButton("Start",
                new Color(76, 175, 80), new Color(56, 142, 60));
        MyButton btnPause = new MyButton("Pause",
                new Color(255, 152, 0), new Color(245, 127, 23));
        MyButton btnReset = new MyButton("Reset",
                new Color(244, 67, 54), new Color(211, 47, 47));

        buttonsPanel.add(btnStart);
        buttonsPanel.add(btnPause);
        buttonsPanel.add(btnReset);

        add(buttonsPanel, BorderLayout.SOUTH);

        // ===== TIMER =====
        timer = new Timer(1000, e -> tick());

        // ===== ACTIONS =====
        btnStart.addActionListener(e -> {
            if (!isRunning) {
                timer.start();
                isRunning = true;
            }
        });

        btnPause.addActionListener(e -> {
            timer.stop();
            isRunning = false;
        });

        btnReset.addActionListener(e -> resetCurrentMode());
    }

    // ---- LOGIC ----
    private void tick() {
        remainingTime--;

        // if we are in focus mode and timer is running, count elapsed seconds
        if (currentMode == Mode.FOCUS) {
            focusElapsedSeconds++;
        }

        timerLabel.setText(formatTime(remainingTime));

        if (remainingTime <= 0) {
            // when a focus period finishes, record the session
            if (currentMode == Mode.FOCUS) {
                recordFocusSession();
            }

            switchMode();
        }
    }

    private void switchMode() {
        timer.stop();
        isRunning = false;

        if (currentMode == Mode.FOCUS) {
            currentMode = Mode.BREAK;
            remainingTime = BREAK_DURATION;
            modeLabel.setText("Break");
            modeLabel.setForeground(new Color(86, 156, 214)); // blue
        } else {
            currentMode = Mode.FOCUS;
            remainingTime = FOCUS_DURATION;
            modeLabel.setText("Focus");
            modeLabel.setForeground(new Color(108, 203, 119)); // green
        }

        timerLabel.setText(formatTime(remainingTime));
    }

    private void resetCurrentMode() {
        timer.stop();
        isRunning = false;

        remainingTime = (currentMode == Mode.FOCUS) ? FOCUS_DURATION : BREAK_DURATION;

        // reset elapsed tracking when resetting a focus session
        if (currentMode == Mode.FOCUS) {
            focusElapsedSeconds = 0;
        }

        timerLabel.setText(formatTime(remainingTime));
    }

    // create and save a Session when a focus period completes
    private void recordFocusSession() {
        try {
            DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            LocalDateTime end = LocalDateTime.now();
            LocalDateTime start = end.minusSeconds(focusElapsedSeconds > 0 ? focusElapsedSeconds : FOCUS_DURATION);

            String startStr = start.format(fmt);
            String endStr = end.format(fmt);

            Session s = new Session(startStr, endStr, focusElapsedSeconds > 0 ? focusElapsedSeconds : FOCUS_DURATION, "");

            if (sessionManager != null) {
                sessionManager.saveSession(s);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            // reset elapsed counter after recording
            focusElapsedSeconds = 0;
        }
    }

    private String formatTime(int seconds) {
        int min = seconds / 60;
        int sec = seconds % 60;
        return String.format("%d:%02d", min, sec);
    }
}
