package brainventorypackage;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class Main extends JFrame {

    private JPanel mainContent;
    private CardLayout cardLayout;


    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> { //run in  EDT
            new Main().setVisible(true);
        });
    }

    public Main() {
        setTitle("Brainventory");
        setSize(900, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Sidebar
        JPanel sidebar = createSidebar();

        // Main Content with CardLayout
        cardLayout = new CardLayout();
        mainContent = new JPanel(cardLayout); //container layout to be able to switch panels within 1 frame
        mainContent.add(createDashboardPanel(), "Dashboard");
        mainContent.add(createSessionsMainPanel(), "Focus Sessions");
        mainContent.add(createCoursesPanel(), "Courses");
        mainContent.add(createSettingsPanel(), "Settings");

        //add sidebar and main content
        add(sidebar, BorderLayout.WEST);
        add(mainContent, BorderLayout.CENTER);
    }

    private JPanel createSidebar() {//done
        //create panel for sidebar
        JPanel sidebar = new JPanel();
        sidebar.setPreferredSize(new Dimension(175, 600));
        sidebar.setLayout(new GridLayout(5,1,0,0));
        sidebar.setBackground(new Color(40, 45, 52));

        //make buttons for different pages
        JButton btnDashboard = createSidebarButton("Dashboard");
        JButton btnFocusSessions = createSidebarButton("Focus Sessions");
        JButton btnCourses = createSidebarButton("Courses");
        JButton btnSettings = createSidebarButton("Settings");

        //make sidebar buttons responsive
        btnDashboard.addActionListener(e->cardLayout.show(mainContent, "Dashboard"));
        btnFocusSessions.addActionListener(e -> cardLayout.show(mainContent, "Focus Sessions"));
        btnCourses.addActionListener(e -> cardLayout.show(mainContent, "Courses"));
        btnSettings.addActionListener(e -> cardLayout.show(mainContent, "Settings"));

        sidebar.add(btnDashboard);
        sidebar.add(btnFocusSessions);
        sidebar.add(btnCourses);
        sidebar.add(btnSettings);

        return sidebar;
    }

    private JButton createSidebarButton(String text) { //done
        JButton button = new JButton(text);
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
    
        button.setPreferredSize(new Dimension(Integer.MAX_VALUE, 150));
        button.setFocusPainted(false);
        button.setBackground(new Color(0, 113, 112)); //greenish
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Roboto", Font.BOLD, 16));
        return button;
    }

    //Main Panels 

    private JPanel createDashboardPanel(){
        JPanel panel = new JPanel(new BorderLayout());
        JLabel label = new JLabel("Dashboard", SwingConstants.CENTER);
        label.setFont(new Font("Roboto", Font.BOLD, 24));
        panel.add(label, BorderLayout.CENTER);
        return panel;
    }
    private JPanel createSessionsMainPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(1, 2, 20, 20));
        panel.setBorder(BorderFactory.createEmptyBorder(40, 40, 40, 40));

        panel.add(createNewSessionPanel());
        panel.add(createDailySessionsPanel());
        

        return panel;
    }

    private JPanel createNewSessionPanel() {
        JPanel container = new JPanel();
        container.setLayout(new BorderLayout());
        container.setBackground(new Color(245, 245, 245)); //white smoke 
        container.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel lblTitle = new JLabel("Create New Session");
       
        lblTitle.setFont(new Font("Roboto", Font.BOLD, 18));

        //add elements

        container.add(lblTitle, BorderLayout.NORTH);
        

        return container;
    }

    private JPanel createDailySessionsPanel() {
        JPanel container = new JPanel();
        container.setLayout(new BorderLayout());
        container.setBackground(new Color(245, 245, 245)); //white smoke 
        container.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel lblTitle = new JLabel("Sessions");
       
        lblTitle.setFont(new Font("Roboto", Font.BOLD, 18));

        //add elements

        container.add(lblTitle, BorderLayout.NORTH);
        

        return container;
    }

    private JPanel createCoursesPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        JLabel label = new JLabel("Courses", SwingConstants.CENTER);
        label.setFont(new Font("Roboto", Font.BOLD, 24));
        panel.add(label, BorderLayout.CENTER);
        return panel;
    }

    private JPanel createSettingsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        JLabel label = new JLabel("Settings", SwingConstants.CENTER);
        label.setFont(new Font("Roboto", Font.BOLD, 24));
        panel.add(label, BorderLayout.CENTER);
        return panel;
    }

    
}
