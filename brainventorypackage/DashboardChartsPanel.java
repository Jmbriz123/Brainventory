package brainventorypackage;

// DashboardChartsPanel: small bar-chart style summary of study hours per course
// It listens for session save events and repaints itself. When a weekly
// report is active it shows totals for that week; otherwise it shows overall totals.

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.DayOfWeek;
import java.util.*;

public class DashboardChartsPanel extends JPanel implements PomodoroStudy.SessionSaveListener {

    private final ISessionRepository sessionManager;
    private final ReportGenerator reportGenerator;
    private SessionsPanel sessionsPanel;

    public DashboardChartsPanel(ISessionRepository sessionManager) {
        this.sessionManager = sessionManager;
        this.reportGenerator = new ReportGenerator(sessionManager, new StudyMetadataManager());
        setPreferredSize(new Dimension(520, 340));
        setOpaque(false);
        setLayout(new BorderLayout());
        // No manual refresh button: panel will repaint when a session is saved via listener
    }

    public void setSessionsPanel(SessionsPanel sessionsPanel) {
        this.sessionsPanel = sessionsPanel;
        if (sessionsPanel != null) {
            sessionsPanel.addReportChangeListener(this);
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        Map<String, Integer> totals;
        
        // Determine which totals to aggregate based on report type
        if (sessionsPanel != null && "weekly".equals(sessionsPanel.getCurrentReportType())) {
            // Calculate totals for the selected week
            LocalDate reportDate = sessionsPanel.getCurrentReportDate();
            LocalDate weekStart = reportDate.with(DayOfWeek.MONDAY);
            LocalDate weekEnd = weekStart.plusDays(6);
            
            // Get all sessions in the week and aggregate seconds per subject
            java.util.List<Session> weeklySessions = reportGenerator.getSessionsBetween(
                weekStart.atStartOfDay(),
                weekEnd.plusDays(1).atStartOfDay()
            );
            
            totals = new HashMap<>();
            for (Session session : weeklySessions) {
                String subject = session.course;
                int seconds = session.durationSeconds;
                totals.put(subject, totals.getOrDefault(subject, 0) + seconds);
            }
        } else {
            // Use all-time totals for daily view
            totals = sessionManager.totalSecondsBySubject();
        }

        java.util.List<Map.Entry<String, Integer>> entries = new ArrayList<>(totals.entrySet());
        entries.sort((a,b)->Integer.compare(b.getValue(), a.getValue()));

        int padding = 40;
        int left = padding;
        int top = 40;
        int width = getWidth() - padding*2;
        int height = getHeight() - padding*2 - 30;

        // Background
        g2.setColor(new Color(250,250,250,0));
        g2.fillRect(0,0,getWidth(),getHeight());

        if(entries.isEmpty()){
            g2.setColor(Color.GRAY);
            g2.drawString("No study data yet. Complete a Pomodoro to see charts.", left+10, top+20);
            g2.dispose();
            return;
        }

        // compute max hours
        int maxSeconds = entries.stream().mapToInt(Map.Entry::getValue).max().orElse(1);

        int barCount = Math.min(entries.size(), 8);
        int gap = 12;
        int barWidth = (width - (barCount-1)*gap) / barCount;

        int x = left;
        for (int i=0;i<barCount;i++){
            Map.Entry<String,Integer> e = entries.get(i);
            double frac = (double)e.getValue()/maxSeconds;
            int barH = (int)(frac * (height-60));
            int y = top + (height - barH);

            // bar color
            g2.setColor(new Color(0,113,112));
            g2.fillRoundRect(x, y, barWidth, barH, 8, 8);

            // value label
            g2.setColor(Color.DARK_GRAY);
            String hours = String.format("%.1f", e.getValue()/3600.0);
            int strW = g2.getFontMetrics().stringWidth(hours);
            g2.drawString(hours, x + (barWidth - strW)/2, y - 6);

            // subject label (rotated slightly)
            String subj = e.getKey();
            int labelY = top + height + 14;
            drawCenteredString(g2, subj, x, labelY, barWidth);

            x += barWidth + gap;
        }

        g2.dispose();
    }

    private void drawCenteredString(Graphics2D g2, String text, int x, int y, int width){
        FontMetrics fm = g2.getFontMetrics();
        int textWidth = fm.stringWidth(text);
        int tx = x + (width - textWidth)/2;
        g2.setColor(Color.DARK_GRAY);
        g2.drawString(text, tx, y);
    }

    @Override
    public void onSessionSaved() {
        // repaint the chart when sessions change
        SwingUtilities.invokeLater(this::repaint);
    }
}
