package brainventorypackage;

// Main: application entry point and UI wiring
// - Constructs shared managers and panels, registers listeners
// - Hosts CardLayout for Dashboard / Focus / Courses

import javax.swing.*;
import java.awt.*;
import java.util.Map;
import java.time.LocalDate;

public class Main extends JFrame {

    private JPanel mainContent;
    private CardLayout cardLayout;
    private CoursesPanel coursesPanel;

    // Shared session repository instance used by UI and reports
    static ISessionRepository sessionManager = new SessionManager();
    static SubjectManager subjectManager = new SubjectManager();
    static StudyMetadataManager metadataManager = new StudyMetadataManager();

    static PomodoroStudy myPomodoro = new PomodoroStudy(sessionManager, subjectManager, metadataManager);

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Main().setVisible(true));
    }

    public Main() {
        setTitle("Brainventory");
        
        setSize(550, 1000);
        // remove traditional window decorations and disable default close
        setUndecorated(true);
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        setResizable(false);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        getContentPane().setBackground(new Color(245, 246, 248));

        cardLayout = new CardLayout();
        mainContent = new JPanel(cardLayout);
        mainContent.setOpaque(false);

        mainContent.add(createDashboardPanel(), "Dashboard");
        mainContent.add(createSessionsMainPanel(), "Focus");
        mainContent.add(createCoursesPanel(), "Courses");
        // Settings panel removed; replaced by Exit action in nav

        // Register CoursesPanel as a listener for session saves
        myPomodoro.addSessionSaveListener(coursesPanel);

        add(createTopBar(), BorderLayout.NORTH);
        add(mainContent, BorderLayout.CENTER);
        add(createBottomNav(), BorderLayout.SOUTH);
    }

    private JPanel createTopBar() {
        JPanel top = new JPanel(new BorderLayout());
        top.setPreferredSize(new Dimension(550, 80));
        top.setBackground(new Color(27, 29, 38));
        top.setBorder(BorderFactory.createEmptyBorder(0, 28, 0, 28));

        JLabel title = new JLabel("Brainventory", SwingConstants.CENTER);
        title.setForeground(Color.WHITE);
        title.setFont(UiTheme.TITLE_FONT);

        top.add(title, BorderLayout.CENTER);
        return top;
    }

    private JPanel createBottomNav() {
        JPanel nav = new JPanel(new GridLayout(1, 4));
        nav.setPreferredSize(new Dimension(550, 90));
        nav.setBackground(Color.WHITE);
        nav.setBorder(BorderFactory.createMatteBorder(
                1, 0, 0, 0, new Color(220, 220, 220)));

        nav.add(createNavButton("Dashboard", "Dashboard"));
        nav.add(createNavButton("Focus", "Focus"));
        nav.add(createNavButton("Courses", "Courses"));
        // Exit button replaces Settings: will terminate the app
        JButton exitBtn = new NavButton("Exit", new Color(200,50,50), new Color(150,20,20));
        exitBtn.setForeground(Color.WHITE);
        exitBtn.setBorder(BorderFactory.createEmptyBorder());
        exitBtn.setHorizontalAlignment(SwingConstants.CENTER);
        exitBtn.setPreferredSize(new Dimension(137, 90));
        exitBtn.setFont(new Font("Segoe UI", Font.BOLD, 16));
        exitBtn.addActionListener(e -> System.exit(0));
        nav.add(exitBtn);

        return nav;
    }

    private JButton createNavButton(String text, String card) {
        NavButton btn = new NavButton(
                text,
                new Color(33, 36, 43), 
                new Color(80, 83, 95)
        );

        btn.setForeground(Color.WHITE);
        btn.setBorder(BorderFactory.createEmptyBorder());
        btn.setHorizontalAlignment(SwingConstants.CENTER);
        btn.setPreferredSize(new Dimension(137, 90));
        btn.setFont(new Font("Segoe UI", Font.BOLD, 16));
        btn.addActionListener(e -> cardLayout.show(mainContent, card));
        return btn;
    }

    private JPanel createDashboardPanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 12));
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));

        // Title area
        JLabel label = new JLabel("Dashboard", SwingConstants.CENTER);
        label.setFont(UiTheme.TITLE_FONT);
        label.setForeground(UiTheme.PRIMARY);
        panel.add(label, BorderLayout.NORTH);

        // Main content area: Daily report takes most space
        JPanel center = new JPanel(new BorderLayout());
        center.setOpaque(false);
        
        // Daily report panel (main focus, takes most of the space)
        RoundedPanel reportCard = new RoundedPanel(20, Color.WHITE);
        reportCard.setLayout(new BorderLayout(12, 12));
        // removed internal padding so the reports occupy the full available space
        // reportCard.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        
        SessionsPanel reports = new SessionsPanel();
        reportCard.add(reports, BorderLayout.CENTER);
        // auto-load today's daily report when dashboard is created
        try {
            reports.showDailyReport(LocalDate.now());
        } catch (Exception ignore) {} 
        
        center.add(reportCard, BorderLayout.CENTER);
        
        // Chart at the bottom (smaller, for reference)
        RoundedPanel chartCard = new RoundedPanel(20, Color.WHITE);
        chartCard.setLayout(new BorderLayout());
        chartCard.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        chartCard.setPreferredSize(new Dimension(500, 220));
        
        DashboardChartsPanel charts = new DashboardChartsPanel(sessionManager);
        charts.setSessionsPanel(reports);  // so charts can query report type
        chartCard.add(charts, BorderLayout.CENTER);
        
        center.add(chartCard, BorderLayout.SOUTH);

        // register dashboard components to automatically update when a session is saved
        myPomodoro.addSessionSaveListener(charts);
        myPomodoro.addSessionSaveListener(reports);
        
        panel.add(center, BorderLayout.CENTER);

        return wrap(panel);
    }

    private JPanel createSessionsMainPanel() {
        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setOpaque(false);

        // Focus panel shows only the pomodoro control (no reports here)
        content.add(myPomodoro);
        content.add(Box.createVerticalStrut(24));

        return wrap(content);
    }

    private JPanel createCoursesPanel() {
        coursesPanel = new CoursesPanel();
        return wrap(coursesPanel);
    }

    private JPanel createSettingsPanel() {
        JPanel panel = modernCard();
        JLabel label = new JLabel("Settings", SwingConstants.CENTER);
        label.setFont(new Font("Segoe UI", Font.BOLD, 28));
        panel.add(label, BorderLayout.CENTER);
        return wrap(panel);
    }

    private JPanel wrap(JComponent inner) {
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setOpaque(false);
        wrapper.setBorder(BorderFactory.createEmptyBorder(32, 32, 32, 32));
        wrapper.add(inner, BorderLayout.CENTER);
        return wrapper;
    }

    private JPanel modernCard() {
        return new JPanel(new BorderLayout()) {
            {
                setOpaque(false);
                setBorder(BorderFactory.createEmptyBorder(24, 24, 24, 24));
            }

            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON);

                g2.setColor(Color.WHITE);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 26, 26);

                g2.setColor(new Color(0, 0, 0, 18));
                g2.drawRoundRect(0, 0,
                        getWidth() - 1, getHeight() - 1, 26, 26);

                g2.dispose();
                paintBorder(g);
                paintChildren(g);
            }
        };
    }
}
