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
        int delay = 10;
        Timer timer = new Timer(delay, new ActionListener(){
            public void actionPerformed(ActionEvent e){
                //display time remaining (min:secs)
                
        
                displayTimer();
                remainingTimeInSeconds --;
                //update minutes and seconds
                displayMinutes = remainingTimeInSeconds/60;
                displaySeconds = remainingTimeInSeconds%60;
                
                if (remainingTimeInSeconds == 0){
                    ((Timer)e.getSource()).stop();
                    displayTimer();
                }
            }
        });
        timer.start();

        //add start/pause button

        //add reset button
    
    }
    private void displayTimer(){ //for illustration purposes only
        System.out.print(displayMinutes + ":");
        if (displaySeconds < 10){
            System.out.println("0" + displaySeconds);
        }else{
            System.out.println(displaySeconds);
        }
        

    }
    

    private JPanel createPomoTimer(){
        return new JPanel();
    }

    private JButton createButton(){
        JButton button = new JButton(buttonText);
        button.setFocusPainted(false);
        button.setBackground(new Color(0, 113, 112)); //greenish
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Roboto", Font.BOLD, 16));

        return button;
    }
}