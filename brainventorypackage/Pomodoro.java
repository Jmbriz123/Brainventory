package brainventorypackage;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class Pomodoro extends JPanel{
    int remainingTimeInSeconds = 25*60;

    int displayMinutes = remainingTimeInSeconds/60;
    int displaySeconds = remainingTimeInSeconds % 60;
    String buttonText;
    Boolean isTimerOn = false;

    public Pomodoro(){
        //add timer
        JLabel pomoTimer = createPomoTimer(displayMinutes, displaySeconds);
        add(pomoTimer);
        int delay = 1000;
        Timer timer = new Timer(delay, new ActionListener(){
            public void actionPerformed(ActionEvent e){
                //display pomoTimer
                
        
                pomoTimer.setText(String.format("%d:%02d", displayMinutes, displaySeconds));
                remainingTimeInSeconds --;
                //update minutes and seconds
                displayMinutes = remainingTimeInSeconds/60;
                displaySeconds = remainingTimeInSeconds%60;
                
                if (remainingTimeInSeconds == 0){
                    ((Timer)e.getSource()).stop();
                    pomoTimer.setText(String.format("%d:%02d", displayMinutes, displaySeconds));
                }
            }
            
        });
        

        //add start/pause/reset buttons
        JPanel buttonsPanel = new JPanel(new FlowLayout());

        MyButton btnStart = new MyButton("Start", new Color(42, 46, 58),new Color(54, 57, 71));
        MyButton btnPause = new  MyButton("Pause", new Color(42, 46, 58),new Color(54, 57, 71));
        MyButton btnReset = new  MyButton("Reset", new Color(42, 46, 58),new Color(54, 57, 71));

        buttonsPanel.add(btnStart);
        buttonsPanel.add(btnPause);
        buttonsPanel.add(btnReset);
        //make buttons responsive
        btnStart.addActionListener(e->timer.start());
        btnPause.addActionListener(e->timer.stop());
        btnReset.addActionListener(e->{
            remainingTimeInSeconds = 25*60;});

        add(buttonsPanel);
    
    
    }
    
   

    
    
    

    private JLabel createPomoTimer(int minuteDisplay, int secondsDisplay){
        
    
        JLabel timerLabel = new JLabel(String.format("%d:%02d", minuteDisplay, secondsDisplay));
        timerLabel.setFont(new Font("Roboto", Font.BOLD, 36));
        timerLabel.setHorizontalAlignment(SwingConstants.CENTER);
       
        return timerLabel;
    }

    
}