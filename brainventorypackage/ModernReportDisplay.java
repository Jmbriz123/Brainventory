package brainventorypackage;

// ModernReportDisplay: custom Swing component that renders the
// summary cards and per-subject rows for daily/weekly reports.
// It exposes a simple `setDailyReportData` API used by SessionsPanel.

import javax.swing.*;
import java.awt.*;
import java.util.*;

public class ModernReportDisplay extends JPanel {

    private java.util.List<ReportGenerator.SubjectSummary> subjects = new ArrayList<>();
    private int totalSessions = 0;
    private int totalSeconds = 0;
    private String title = "Report";

    public ModernReportDisplay() {
        setOpaque(false);
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
    }

    public void displayDailyReport(ReportGenerator reportGenerator, java.time.LocalDate date) {
        setData(reportGenerator.dailyReport(date));
        title = "Daily Report: " + date.toString();
    }

    public void setData(String jsonReport) {
        // Parse JSON manually (simple extraction)
        subjects.clear();
        totalSessions = 0;
        totalSeconds = 0;

        try {
            // Extract totalSessions
            int idx = jsonReport.indexOf("\"totalSessions\":");
            if (idx >= 0) {
                int end = jsonReport.indexOf(",", idx);
                String val = jsonReport.substring(idx + 16, end).trim();
                totalSessions = Integer.parseInt(val);
            }

            // Extract totalSeconds
            idx = jsonReport.indexOf("\"totalSeconds\":");
            if (idx >= 0) {
                int end = jsonReport.indexOf(",", idx);
                if (end < 0) end = jsonReport.indexOf("}", idx);
                String val = jsonReport.substring(idx + 15, end).trim();
                totalSeconds = Integer.parseInt(val);
            }
        } catch (Exception ex) {
            System.err.println("[ModernReportDisplay] Error parsing report: " + ex.getMessage());
        }

        removeAll();
        revalidate();
        repaint();
    }

    public void setDailyReportData(int sessions, int seconds, java.util.List<ReportGenerator.SubjectSummary> subs, String reportDate) {
        subjects = subs;
        totalSessions = sessions;
        totalSeconds = seconds;
        title = "Daily Report: " + reportDate;

        removeAll();
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int y = 16;
        int w = getWidth() - 32;

        // Title
        g2.setColor(new Color(230, 230, 230));
        g2.setFont(new Font("Segoe UI", Font.BOLD, 22));
        g2.drawString(title, 16, y);
        y += 32;

        // Summary cards
        double hours = totalSeconds / 3600.0;

        // Sessions card
        drawCard(g2, 16, y, (w - 12) / 2, 100, "Sessions", String.valueOf(totalSessions), new Color(76, 175, 80));
        drawCard(g2, 16 + (w - 12) / 2 + 12, y, (w - 12) / 2, 100, "Hours", String.format("%.1f", hours), new Color(86, 156, 214));
        y += 112;

        // Subjects section
        if (!subjects.isEmpty()) {
            g2.setColor(new Color(200, 200, 200));
            g2.setFont(new Font("Segoe UI", Font.BOLD, 16));
            g2.drawString("By Subject", 16, y);
            y += 28;

            for (ReportGenerator.SubjectSummary subj : subjects) {
                drawSubjectRow(g2, 16, y, w, subj);
                y += 76;
            }
        }

        g2.dispose();
    }

    private void drawCard(Graphics2D g2, int x, int y, int width, int height, String label, String value, Color accentColor) {
        // Background
        g2.setColor(new Color(45, 48, 56));
        g2.fillRoundRect(x, y, width, height, 16, 16);

        // Border
        g2.setColor(accentColor);
        g2.setStroke(new BasicStroke(2));
        g2.drawRoundRect(x, y, width, height, 16, 16);

        // Label
        g2.setColor(new Color(150, 150, 150));
        g2.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        g2.drawString(label, x + 16, y + 24);

        // Value
        g2.setColor(accentColor);
        g2.setFont(new Font("Segoe UI", Font.BOLD, 28));
        g2.drawString(value, x + 16, y + 62);
    }

    private void drawSubjectRow(Graphics2D g2, int x, int y, int width, ReportGenerator.SubjectSummary subj) {
        // Background card
        g2.setColor(new Color(45, 48, 56));
        g2.fillRoundRect(x, y, width, 68, 12, 12);

        // Subject name
        g2.setColor(new Color(230, 230, 230));
        g2.setFont(new Font("Segoe UI", Font.BOLD, 14));
        g2.drawString(subj.subject, x + 16, y + 24);

        // Hours
        double hrs = subj.totalSeconds / 3600.0;
        g2.setColor(new Color(108, 203, 119));
        g2.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        g2.drawString(String.format("Hours: %.2f", hrs), x + 16, y + 44);

        // Productivity and Energy
        g2.setColor(new Color(86, 156, 214));
        g2.drawString(String.format("Productivity: %.1f | Energy: %.1f", subj.avgProductivity, subj.avgEnergy), x + 16, y + 62);
    }

    @Override
    public Dimension getPreferredSize() {
        int h = 64 + 112; // title + summary cards
        h += 28 + (subjects.size() * 76); // subjects
        return new Dimension(getWidth(), h);
    }
}
