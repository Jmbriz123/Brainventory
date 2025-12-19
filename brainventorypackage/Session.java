package brainventorypackage;

// Session: immutable-ish data holder describing a single study session
// Fields:
// - startTime, endTime: timestamps as strings
// - durationSeconds: length of session in seconds
// - course: the subject/course name (may be empty)
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;

public class Session{
    String startTime;
    String endTime;
    int durationSeconds;
    String course;

    Session(String startTime, String endTime, int durationSeconds, String course){
        this.startTime  = startTime;
        this.endTime = endTime;
        this.durationSeconds = durationSeconds;
        this.course = course;
    }
}
