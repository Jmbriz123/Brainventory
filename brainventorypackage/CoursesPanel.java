package brainventorypackage;

// CoursesPanel: UI for managing courses and showing per-course details
// Responsibilities:
// - List available subjects
// - Show aggregated duration and recent sessions for selected subject
// - React to session save events to refresh displayed data

import javax.swing.*;
import java.awt.*;
import java.util.Map;

public class CoursesPanel extends RoundedPanel implements PomodoroStudy.SessionSaveListener {

    private final SubjectManager subjectManager = new SubjectManager();
    private final SessionManager sessionManager = new SessionManager();

    private final DefaultListModel<String> listModel = new DefaultListModel<>();
    private final JList<String> subjectList = new JList<>(listModel);
    private final JTextArea infoArea = new JTextArea();

    public CoursesPanel() {
        super(20, new Color(33, 36, 43));
        setLayout(new BorderLayout(0, 24));
        setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

        // ===== TITLE =====
        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setOpaque(false);
        JLabel title = new JLabel("My Courses", SwingConstants.CENTER);
        title.setForeground(new Color(230, 230, 230));
        title.setFont(UiTheme.SUBTITLE_FONT);
        titlePanel.add(title, BorderLayout.CENTER);
        add(titlePanel, BorderLayout.NORTH);

        // ===== CENTER: LIST + INFO =====
        JPanel center = new JPanel(new GridLayout(1, 2, 16, 0));
        center.setOpaque(false);

        // --- LEFT: COURSE LIST ---
        JPanel leftPanel = new RoundedPanel(20, new Color(45, 48, 56));
        leftPanel.setLayout(new BorderLayout());
        leftPanel.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));

        JLabel listTitle = new JLabel("Courses", SwingConstants.CENTER);
        listTitle.setForeground(new Color(108, 203, 119));
        listTitle.setFont(UiTheme.BODY_FONT);
        leftPanel.add(listTitle, BorderLayout.NORTH);

        subjectList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        subjectList.setBackground(new Color(50, 53, 62));
        subjectList.setForeground(new Color(230, 230, 230));
        subjectList.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        subjectList.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

        JScrollPane leftScroll = new JScrollPane(subjectList);
        leftScroll.setBorder(BorderFactory.createEmptyBorder(12, 0, 0, 0));
        leftScroll.setBackground(new Color(50, 53, 62));
        leftPanel.add(leftScroll, BorderLayout.CENTER);

        center.add(leftPanel);

        // --- RIGHT: INFO PANEL ---
        JPanel rightPanel = new RoundedPanel(20, new Color(45, 48, 56));
        rightPanel.setLayout(new BorderLayout());
        rightPanel.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));

        JLabel infoTitle = new JLabel("Details", SwingConstants.CENTER);
        infoTitle.setForeground(new Color(86, 156, 214));
        infoTitle.setFont(UiTheme.BODY_FONT);
        rightPanel.add(infoTitle, BorderLayout.NORTH);

        infoArea.setEditable(false);
        infoArea.setFont(UiTheme.BODY_FONT);
        infoArea.setForeground(new Color(200, 200, 200));
        infoArea.setBackground(new Color(50, 53, 62));
        infoArea.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        infoArea.setLineWrap(true);
        infoArea.setWrapStyleWord(true);

        JScrollPane rightScroll = new JScrollPane(infoArea);
        rightScroll.setBorder(BorderFactory.createEmptyBorder(12, 0, 0, 0));
        rightScroll.setBackground(new Color(50, 53, 62));
        rightPanel.add(rightScroll, BorderLayout.CENTER);

        center.add(rightPanel);
        add(center, BorderLayout.CENTER);

        // ===== BUTTONS =====
        JPanel buttonsPanel = new JPanel(new GridLayout(1, 2, 12, 0));
        buttonsPanel.setOpaque(false);

        MyButton addBtn = new MyButton("Add Course",
            new Color(76, 175, 80), new Color(56, 142, 60));
        MyButton deleteBtn = new MyButton("Delete Course",
            new Color(244, 67, 54), new Color(211, 47, 47));

        buttonsPanel.add(addBtn);
        buttonsPanel.add(deleteBtn);

        add(buttonsPanel, BorderLayout.SOUTH);

        // ===== ACTIONS =====
        addBtn.addActionListener(e -> onAddCourse());
        deleteBtn.addActionListener(e -> onDeleteCourse());
        // refresh button removed; data updates automatically via SessionSaveListener

        subjectList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) showSelectedInfo();
        });

        loadSubjects();
        refreshDurations();
    }

    private void loadSubjects() {
        listModel.clear();
        for (Subject s : subjectManager.getSubjects()) {
            listModel.addElement(s.getName());
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

            loadSubjects();
            JOptionPane.showMessageDialog(this, "Course '" + name + "' added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error adding course: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void onDeleteCourse() {
        try {
            String sel = subjectList.getSelectedValue();
            if (sel == null) {
                JOptionPane.showMessageDialog(this, "Please select a course to delete.", "No Selection", JOptionPane.WARNING_MESSAGE);
                return;
            }

            int ok = JOptionPane.showConfirmDialog(this,
                    "Are you sure you want to delete the course '" + sel + "'?\nThis action cannot be undone.",
                    "Confirm Delete",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE);

            if (ok == JOptionPane.YES_OPTION) {
                subjectManager.removeSubject(sel);
                loadSubjects();
                infoArea.setText("");
                JOptionPane.showMessageDialog(this, "Course '" + sel + "' deleted successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error deleting course: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void refreshDurations() {
        try {
            Map<String, Integer> totals = sessionManager.totalSecondsBySubject();
            StringBuilder sb = new StringBuilder();
            sb.append("Course Summary\n");
            sb.append("====================\n\n");

            if (totals.isEmpty()) {
                sb.append("No courses with study sessions yet.");
            } else {
                for (Subject subj : subjectManager.getSubjects()) {
                    String name = subj.getName();
                    int secs = totals.getOrDefault(name, 0);
                    double hours = secs / 3600.0;
                    sb.append(String.format("%s: %.2f h\n", name, hours));
                }
            }
            infoArea.setText(sb.toString());
        } catch (Exception ex) {
            infoArea.setText("Error loading durations: " + ex.getMessage());
        }
    }

    private void showSelectedInfo() {
        try {
            String sel = subjectList.getSelectedValue();
            if (sel == null) return;

            Map<String, Integer> totals = sessionManager.totalSecondsBySubject();
            int secs = totals.getOrDefault(sel, 0);
            double hours = secs / 3600.0;

            StringBuilder sb = new StringBuilder();
            sb.append("Course: ").append(sel).append("\n");
            sb.append("====================\n\n");
            sb.append("Total Hours: ").append(String.format("%.2f", hours)).append(" h\n\n");
            sb.append("Recent Sessions:\n");

            java.util.List<Session> sessions = sessionManager.getSessionsBySubject(sel);
            if (sessions.isEmpty()) {
                sb.append("No sessions recorded yet.");
            } else {
                for (Session s : sessions) {
                    int mins = s.durationSeconds / 60;
                    sb.append(String.format("%s (%d min)\n", s.startTime, mins));
                }
            }

            infoArea.setText(sb.toString());
        } catch (Exception ex) {
            infoArea.setText("Error loading details: " + ex.getMessage());
        }
    }

    @Override
    public void onSessionSaved() {
        // Refresh all data when a session is saved (on EDT)
        SwingUtilities.invokeLater(() -> {
            sessionManager.reloadSessions();
            loadSubjects();
            refreshDurations();
            if (subjectList.getSelectedIndex() >= 0) {
                showSelectedInfo();
            }
        });
    }
}