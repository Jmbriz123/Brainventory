package brainventorypackage;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;


public class MyButton extends JButton{

    public MyButton(String text, Color defaultColor, Color hoverColor){
        super(text);
        setFocusPainted(false);
        setForeground(Color.WHITE);
        setFont(new Font("Segoe UI", Font.BOLD, 16));
        setBorder(BorderFactory.createEmptyBorder(12, 20, 12, 20));
        setContentAreaFilled(false);
        setOpaque(false);
        setHorizontalAlignment(SwingConstants.LEFT);
        setBackground(defaultColor);

        addMouseListener(new MouseAdapter(){
            @Override
            public void mouseEntered(MouseEvent e){
                setBackground(hoverColor);
            }
            @Override
            public void mouseExited(MouseEvent e){
                setBackground(defaultColor);
            }
        });
    }
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
}