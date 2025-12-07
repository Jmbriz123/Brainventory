package brainventorypackage;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class Main extends JFrame {

    private JPanel mainContent;
    private CardLayout cardLayout;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Main().setVisible(true));
    }

    public Main() {
        setTitle("Brainventory");
        setSize(950, 620);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // High-quality antialiasing
        System.setProperty("sun.java2d.uiScale", "1.0");

        add(createSidebar(), BorderLayout.WEST);

        cardLayout = new CardLayout();
        mainContent = new JPanel(cardLayout);
        mainContent.setBackground(new Color(245, 246, 248));

        mainContent.add(createDashboardPanel(), "Dashboard");
        mainContent.add(createSessionsMainPanel(), "Focus Sessions");
        mainContent.add(createCoursesPanel(), "Courses");
        mainContent.add(createSettingsPanel(), "Settings");

        add(mainContent, BorderLayout.CENTER);
    }

   
    private JPanel createSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setPreferredSize(new Dimension(190, 600));
        sidebar.setLayout(new GridLayout(5, 1, 0, 0));
        sidebar.setBackground(new Color(27, 29, 38)); // dark-modern


        JButton btnDashboard = createSidebarButton("Dashboard");
        JButton btnFocus = createSidebarButton("Focus Sessions");
        JButton btnCourses = createSidebarButton("Courses");
        JButton btnSettings = createSidebarButton("Settings");

        btnDashboard.addActionListener(e -> cardLayout.show(mainContent, "Dashboard"));
        btnFocus.addActionListener(e -> cardLayout.show(mainContent, "Focus Sessions"));
        btnCourses.addActionListener(e -> cardLayout.show(mainContent, "Courses"));
        btnSettings.addActionListener(e -> cardLayout.show(mainContent, "Settings"));

        sidebar.add(btnDashboard);
        sidebar.add(btnFocus);
        sidebar.add(btnCourses);
        sidebar.add(btnSettings);

        return sidebar;
    }


    private JButton createSidebarButton(String text) {
        JButton button = new JButton(text) {
            @Override
            //apply styles
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create(); //cast to 2D for more drawing features
                //anti-aliasing 
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                                    RenderingHints.VALUE_ANTIALIAS_ON);

                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 18, 18);
                super.paintComponent(g2);
                g2.dispose();
            }
        };

        button.setFocusPainted(false);
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Segoe UI", Font.BOLD, 16));
        button.setBorder(BorderFactory.createEmptyBorder(12, 20, 12, 20));
        button.setContentAreaFilled(false);
        button.setOpaque(false);
        button.setHorizontalAlignment(SwingConstants.LEFT);

        // Default & hover colors
        Color defaultColor = new Color(42, 46, 58); //darker bluish-gray
        Color hoverColor = new Color(54, 57, 71);  //lighter bluish-gray

        button.setBackground(defaultColor);

        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(hoverColor);
            }
            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(defaultColor);
            }
        });

        return button;
    }

   

    private JPanel createDashboardPanel() {
        JPanel panel = modernCard();
        JLabel label = new JLabel("Dashboard", SwingConstants.CENTER);
        label.setFont(new Font("Segoe UI", Font.BOLD, 30));
        panel.add(label, BorderLayout.CENTER);
        return panel;
    }

    private JPanel createSessionsMainPanel() {
        JPanel panel = new JPanel(new GridLayout(1, 2, 30, 30));
        panel.setBorder(BorderFactory.createEmptyBorder(40, 40, 40, 40));
        panel.setBackground(new Color(245, 246, 248));

        panel.add(createTile("Create New Session"));
        panel.add(createTile("Sessions"));

        return panel;
    }

    private JPanel createTile(String title) {
        JPanel container = modernCard();

        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblTitle.setBorder(BorderFactory.createEmptyBorder(10, 10, 20, 10));

        container.add(lblTitle, BorderLayout.NORTH);
        return container;
    }

    private JPanel createCoursesPanel() {
        JPanel panel = modernCard();
        JLabel label = new JLabel("Courses", SwingConstants.CENTER);
        label.setFont(new Font("Segoe UI", Font.BOLD, 30));
        panel.add(label, BorderLayout.CENTER);
        return panel;
    }

    private JPanel createSettingsPanel() {
        JPanel panel = modernCard();
        JLabel label = new JLabel("Settings", SwingConstants.CENTER);
        label.setFont(new Font("Segoe UI", Font.BOLD, 30));
        panel.add(label, BorderLayout.CENTER);
        return panel;
    }

    

    private JPanel modernCard() {
        return new JPanel(new BorderLayout()) {
            {
                setBackground(Color.WHITE);
                setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createEmptyBorder(25, 25, 25, 25),
                        BorderFactory.createLineBorder(new Color(230, 230, 230), 1)
                ));
            }

            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                                    RenderingHints.VALUE_ANTIALIAS_ON);

                g2.setColor(Color.WHITE);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);

                // soft shadow
                g2.setColor(new Color(0, 0, 0, 20));
                g2.drawRoundRect(0, 0, getWidth(), getHeight(), 20, 20);

                super.paintComponent(g2);
                g2.dispose();
            }
        };
    }
}
