package brainventorypackage;

// PomodoroStudy: focused Pomodoro UI that records completed sessions
// - Allows course selection and records session metadata (productivity/energy)
// - Notifies registered SessionSaveListener instances after saving a session

import javax.swing.*;
import java.awt.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class PomodoroStudy extends RoundedPanel {

    private ISessionRepository sessionManager;
    private SubjectManager subjectManager;
    private StudyMetadataManager metadataManager;

    // ---- LISTENER ----
    public interface SessionSaveListener {
        void onSessionSaved();
    }
    private java.util.List<SessionSaveListener> listeners = new java.util.ArrayList<>();

    public void addSessionSaveListener(SessionSaveListener listener) {
        listeners.add(listener);
    }

    private void notifySessionSaved() {
        for (SessionSaveListener listener : listeners) {
            listener.onSessionSaved();
        }
    }

    // notifySessionSaved(): called after a session and its metadata have
    // been persisted; listeners should reload data and update UI accordingly.

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
    private JComboBox<String> courseSelector;
    private JButton addCourseBtn;
    private Timer timer;
    private final DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public PomodoroStudy(ISessionRepository sessionManager, SubjectManager subjectManager, StudyMetadataManager metadataManager) {
        super(20, new Color(33, 36, 43));
        this.sessionManager = sessionManager;
        this.subjectManager = subjectManager;
        this.metadataManager = metadataManager;
        setLayout(new BorderLayout(0, 20));
        setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

        // ===== COURSE SELECTOR PANEL =====
        JPanel coursePanel = new JPanel(new BorderLayout(12, 0));
        coursePanel.setOpaque(false);

        JLabel courseLabel = new JLabel("Course:", SwingConstants.CENTER);
        courseLabel.setForeground(new Color(200, 200, 200));
        courseLabel.setFont(UiTheme.BODY_FONT);
        coursePanel.add(courseLabel, BorderLayout.WEST);

        courseSelector = new JComboBox<>();
        courseSelector.setBackground(new Color(50, 53, 62));
        courseSelector.setForeground(new Color(230, 230, 230));
        courseSelector.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        refreshCourseList();
        coursePanel.add(courseSelector, BorderLayout.CENTER);

        addCourseBtn = new MyButton("+ Add", new Color(76, 175, 80), new Color(56, 142, 60));
        addCourseBtn.setPreferredSize(new Dimension(80, 32));
        addCourseBtn.addActionListener(e -> onAddCourse());
        coursePanel.add(addCourseBtn, BorderLayout.EAST);

        add(coursePanel, BorderLayout.NORTH);

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
        buttonsPanel.setOpaque(false);

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
        timer = new Timer(10, e -> tick());

        // ===== ACTIONS =====
        btnStart.addActionListener(e -> onStartSession());
        btnPause.addActionListener(e -> {
            timer.stop();
            isRunning = false;
        });

        btnReset.addActionListener(e -> resetCurrentMode());
    }

    private void refreshCourseList() {
        courseSelector.removeAllItems();
        for (Subject s : subjectManager.getSubjects()) {
            courseSelector.addItem(s.getName());
        }
    }

    private void onAddCourse() {
        try {
            JPanel panel = new JPanel();
            panel.setLayout(new GridLayout(0, 2, 8, 8));

            JTextField nameField = new JTextField(20);
            JTextField descField = new JTextField(20);

            JLabel ln = new JLabel("Course Name:", SwingConstants.CENTER);
            ln.setFont(UiTheme.FORM_LABEL_FONT);
            panel.add(ln);
            panel.add(nameField);
            JLabel ld = new JLabel("Description (optional):", SwingConstants.CENTER);
            ld.setFont(UiTheme.FORM_LABEL_FONT);
            panel.add(ld);
            panel.add(descField);

            int result = JOptionPane.showConfirmDialog(this, panel,
                    "Add New Course", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

            if (result != JOptionPane.OK_OPTION) return;

            String name = nameField.getText().trim();
            if (name.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Course name cannot be empty!", "Invalid Input", JOptionPane.WARNING_MESSAGE);
                return;
            }

            String desc = descField.getText().trim();
            Subject s = new Subject(name, desc);
            subjectManager.addSubject(s);

            refreshCourseList();
            courseSelector.setSelectedItem(name);
            JOptionPane.showMessageDialog(this, "Course '" + name + "' added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error adding course: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void onStartSession() {
        if (courseSelector.getItemCount() == 0) {
            JOptionPane.showMessageDialog(this, "No courses available. Please add a course first!", "No Course Selected", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Object selected = courseSelector.getSelectedItem();
        if (selected == null || selected.toString().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please select a course before starting a session.", "Course Required", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (!isRunning) {
            timer.start();
            isRunning = true;
        }
    }

    private String formatTime(int seconds) {
        int min = seconds / 60;
        int sec = seconds % 60;
        return String.format("%d:%02d", min, sec);
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
            // when a focus period finishes, record the session and prompt for metadata
            if (currentMode == Mode.FOCUS) {
                promptAndSaveSession();
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

    // prompt for subject/productivity/energy and save
    private void promptAndSaveSession() {
        try {
            LocalDateTime end = LocalDateTime.now();
            LocalDateTime start = end.minusSeconds(focusElapsedSeconds > 0 ? focusElapsedSeconds : FOCUS_DURATION);

            String startStr = start.format(fmt);
            String endStr = end.format(fmt);

            // Get selected course
            Object selected = courseSelector.getSelectedItem();
            String subjectName = (selected != null) ? selected.toString() : "<Unassigned>";

            // Only ask for productivity and energy now
            JPanel panel = new JPanel(new GridLayout(0, 2, 8, 8));

            JLabel lp = new JLabel("Productivity (1-5):", SwingConstants.CENTER);
            lp.setFont(UiTheme.FORM_LABEL_FONT);
            panel.add(lp);
            JSpinner spinProd = new JSpinner(new SpinnerNumberModel(3, 1, 5, 1));
            panel.add(spinProd);

            JLabel le = new JLabel("Energy (1-5):", SwingConstants.CENTER);
            le.setFont(UiTheme.FORM_LABEL_FONT);
            panel.add(le);
            JSpinner spinEnergy = new JSpinner(new SpinnerNumberModel(3, 1, 5, 1));
            panel.add(spinEnergy);

            int res = JOptionPane.showConfirmDialog(this, panel, "Rate your session", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
            if (res != JOptionPane.OK_OPTION) return;

            int productivity = (Integer) spinProd.getValue();
            int energy = (Integer) spinEnergy.getValue();

            // create Session (existing Session uses 'course' field name)
            Session s = new Session(startStr, endStr, focusElapsedSeconds > 0 ? focusElapsedSeconds : FOCUS_DURATION, subjectName);
            sessionManager.saveSession(s);

            // save metadata keyed by start|end
            String key = startStr + "|" + endStr;
            metadataManager.putMetadata(key, new StudyMetadataManager.Metadata(productivity, energy));

            // notify all listeners that a session was saved
            notifySessionSaved();

        } catch (Exception ex) {
            System.err.println("[PomodoroStudy] Error in promptAndSaveSession:");
            ex.printStackTrace();
        } finally {
            focusElapsedSeconds = 0;
        }
    }
}
