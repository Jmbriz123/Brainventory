package brainventorypackage;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.Map;
import java.util.ArrayList;

import java.time.LocalDate;

// SessionsPanel: UI panel that displays reports (daily / weekly)
// - Provides controls to pick date and switch between daily/weekly views
// - Notifies listeners when the user changes the report selection
public class SessionsPanel extends RoundedPanel implements PomodoroStudy.SessionSaveListener {

    private final ISessionRepository sessionManager = new SessionManager();
    private final SubjectManager subjectManager = new SubjectManager();
    private final StudyMetadataManager metadataManager = new StudyMetadataManager();
    private final ReportGenerator reportGenerator = new ReportGenerator(sessionManager, metadataManager);

    // UI components: a simple output area (debug) and the modern report renderer
    private final JTextArea outputArea = new JTextArea();
    private final ModernReportDisplay reportDisplay = new ModernReportDisplay();
    
    private LocalDate currentReportDate = LocalDate.now();
    private String currentReportType = "daily"; // "daily" or "weekly"
    private java.util.List<PomodoroStudy.SessionSaveListener> reportListeners = new ArrayList<>();

    public SessionsPanel(){
        super(35, new Color(33,36, 43));
        setLayout(new BorderLayout(12,12));
        // removed outer padding so the sessions/report panel can fill available space
        setBorder(BorderFactory.createEmptyBorder(0,0,0,0));

        // Controls
        JPanel controls = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 8));
        controls.setOpaque(false);

        // date picker (DaySpinnerModel steps by one day)
        SpinnerDateModel model = new DaySpinnerModel(new Date(), null, null);
        JSpinner dateSpinner = new JSpinner(model);
        dateSpinner.setEditor(new JSpinner.DateEditor(dateSpinner, "yyyy-MM-dd"));
        dateSpinner.setPreferredSize(new Dimension(120, 32));
        JLabel dateLabel = new JLabel("Date:", SwingConstants.CENTER);
        dateLabel.setFont(UiTheme.BODY_FONT);
        dateLabel.setForeground(new Color(200,200,200));
        controls.add(dateLabel);
        controls.add(dateSpinner);

        MyButton dailyBtn = new MyButton("Daily", new Color(76, 175, 80), new Color(56, 142, 60));
        MyButton weeklyBtn = new MyButton("Weekly", new Color(255, 152, 0), new Color(245, 127, 23));

        dailyBtn.setPreferredSize(new Dimension(90, 32));
        weeklyBtn.setPreferredSize(new Dimension(90, 32));

        controls.add(dailyBtn);
        controls.add(weeklyBtn);

        add(controls, BorderLayout.NORTH);

        // Use JScrollPane with reportDisplay for modern rendering
        JScrollPane sc = new JScrollPane(reportDisplay);
        sc.setBorder(BorderFactory.createEmptyBorder());
        sc.setOpaque(false);
        sc.getViewport().setOpaque(false);
        add(sc, BorderLayout.CENTER);

        // Daily button: generate and display a daily report for selected date
        dailyBtn.addActionListener(e -> {
            Date d = (Date) dateSpinner.getValue();
            LocalDate ld = d.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            currentReportType = "daily";
            currentReportDate = ld;
            String json = reportGenerator.dailyReport(ld);
            displayDailyReport(ld, json);
            notifyReportChange();
        });

        // Weekly button: generate and display a weekly report (Mon-Sun)
        weeklyBtn.addActionListener(e -> {
            Date d = (Date) dateSpinner.getValue();
            LocalDate ld = d.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            currentReportType = "weekly";
            currentReportDate = ld;
            String json = reportGenerator.weeklyReport(ld);
            displayWeeklyReport(ld, json);
            notifyReportChange();
        });
    }

    private void displayDailyReport(LocalDate date, String json) {
        // Parse JSON and extract subjects
        try {
            com.google.gson.Gson gson = new com.google.gson.Gson();
            java.util.Map<String, Object> map = gson.fromJson(json, java.util.Map.class);

            int totalSessions = ((Number) map.get("totalSessions")).intValue();
            int totalSeconds = ((Number) map.get("totalSeconds")).intValue();
            java.util.List<java.util.Map<String, Object>> subjectsData = 
                (java.util.List<java.util.Map<String, Object>>) map.get("subjects");

            java.util.List<ReportGenerator.SubjectSummary> subjects = new ArrayList<>();
            if (subjectsData != null) {
                for (java.util.Map<String, Object> s : subjectsData) {
                    ReportGenerator.SubjectSummary ss = new ReportGenerator.SubjectSummary();
                    ss.subject = (String) s.get("subject");
                    ss.totalSeconds = ((Number) s.get("totalSeconds")).intValue();
                    ss.avgProductivity = ((Number) s.get("avgProductivity")).doubleValue();
                    ss.avgEnergy = ((Number) s.get("avgEnergy")).doubleValue();
                    subjects.add(ss);
                }
            }

            // Populate the modern report display with parsed daily data
            reportDisplay.setDailyReportData(totalSessions, totalSeconds, subjects, date.toString());
            reportDisplay.revalidate();
            reportDisplay.repaint();
        } catch (Exception ex) {
            outputArea.setText("Error displaying report: " + ex.getMessage());
        }
    }

    // programmatic API to show a daily report (used by Dashboard to auto-load today's data)
    public void showDailyReport(LocalDate date) {
        String json = reportGenerator.dailyReport(date);
        displayDailyReport(date, json);
    }

    // Build and render a weekly report (Mon - Sun) for a date within that week
    private void displayWeeklyReport(LocalDate date, String json) {
        // Parse JSON and extract subjects (same structure as daily)
        try {
            com.google.gson.Gson gson = new com.google.gson.Gson();
            java.util.Map<String, Object> map = gson.fromJson(json, java.util.Map.class);

            int totalSessions = ((Number) map.get("totalSessions")).intValue();
            int totalSeconds = ((Number) map.get("totalSeconds")).intValue();
            java.util.List<java.util.Map<String, Object>> subjectsData = 
                (java.util.List<java.util.Map<String, Object>>) map.get("subjects");

            java.util.List<ReportGenerator.SubjectSummary> subjects = new ArrayList<>();
            if (subjectsData != null) {
                for (java.util.Map<String, Object> s : subjectsData) {
                    ReportGenerator.SubjectSummary ss = new ReportGenerator.SubjectSummary();
                    ss.subject = (String) s.get("subject");
                    ss.totalSeconds = ((Number) s.get("totalSeconds")).intValue();
                    ss.avgProductivity = ((Number) s.get("avgProductivity")).doubleValue();
                    ss.avgEnergy = ((Number) s.get("avgEnergy")).doubleValue();
                    subjects.add(ss);
                }
            }

            // Get Monday of the week for date range label
            LocalDate weekStart = date.with(java.time.DayOfWeek.MONDAY);
            LocalDate weekEnd = weekStart.plusDays(6);
            // Use a human-friendly date range label when rendering weekly summary
            String dateRangeLabel = weekStart + " to " + weekEnd;

            reportDisplay.setDailyReportData(totalSessions, totalSeconds, subjects, dateRangeLabel);
            reportDisplay.revalidate();
            reportDisplay.repaint();
        } catch (Exception ex) {
            outputArea.setText("Error displaying report: " + ex.getMessage());
        }
    }

    public String getCurrentReportType() {
        return currentReportType;
    }

    public LocalDate getCurrentReportDate() {
        return currentReportDate;
    }

    public void addReportChangeListener(PomodoroStudy.SessionSaveListener listener) {
        reportListeners.add(listener);
    }

    private void notifyReportChange() {
        for (PomodoroStudy.SessionSaveListener listener : reportListeners) {
            listener.onSessionSaved();
        }
    }

    @Override
    public void onSessionSaved() {
        // When a session is saved elsewhere, refresh the current report view (on EDT)
        SwingUtilities.invokeLater(() -> {
            try {
                sessionManager.reloadSessions();
                if ("weekly".equals(currentReportType)) {
                    String json = reportGenerator.weeklyReport(currentReportDate);
                    displayWeeklyReport(currentReportDate, json);
                } else {
                    showDailyReport(currentReportDate);
                }
            } catch (Exception ignore) {}
        });
    }
}
