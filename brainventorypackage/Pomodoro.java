package brainventorypackage;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class Pomodoro extends JPanel{
    int remainingTimeInSeconds = 60;
    int minutes = 1;
    String[] seconds = {"00", "01", "02", "03", "04", "05", "06", "07", "08", "09", "10",
 "11", "12", "13", "14", "15", "16", "17", "18", "19", "20",
 "21", "22", "23", "24", "25", "26", "27", "28", "29", "30",
 "31", "32", "33", "34", "35", "36", "37", "38", "39", "40",
 "41", "42", "43", "44", "45", "46", "47", "48", "49", "50",
 "51", "52", "53", "54", "55", "56", "57", "58", "59"};
    int secondsIndex = 0; //start at 00
    String buttonText;
    Boolean isTimerOn = false;

    public Pomodoro(){
        //add timer
        int delay = 1000;
        Timer timer = new Timer(delay, new ActionListener(){
            public void actionPerformed(ActionEvent e){
                //display time remaining (min:secs)
                
        
                displayTimer(secondsIndex);
                remainingTimeInSeconds --;
                //updates seconds
                if (secondsIndex > 0){
                    secondsIndex--;
                }else if (secondsIndex ==0){
                    secondsIndex = 59; //wrap around
                    //update minutes
                    minutes --;
                }
                

                if (remainingTimeInSeconds == 0){
                    ((Timer)e.getSource()).stop();
                    displayTimer(secondsIndex);
                }
            }
        });
        timer.start();

        //add start/pause button

        //add reset button
    
    }
    private void displayTimer(int sec){ //for illustration purposes only
        System.out.print(minutes+ ":");
        System.out.println(seconds[sec]);

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