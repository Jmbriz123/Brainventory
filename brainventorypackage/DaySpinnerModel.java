package brainventorypackage;

// DaySpinnerModel: custom SpinnerDateModel that advances the spinner
// by one day instead of default month/year ticks. Used for date picker.

import javax.swing.*;
import java.util.Calendar;
import java.util.Date;

public class DaySpinnerModel extends SpinnerDateModel {

    public DaySpinnerModel(Date value, Comparable start, Comparable end) {
        super(value, start, end, Calendar.DAY_OF_MONTH);
    }

    @Override
    public Object getNextValue() {
        Date val = (Date) getValue();
        Calendar c = Calendar.getInstance();
        c.setTime(val);
        c.add(Calendar.DAY_OF_MONTH, 1);
        Date next = c.getTime();
        Comparable end = getEnd();
        if (end != null && next.compareTo((Date) end) > 0) return null;
        return next;
    }

    @Override
    public Object getPreviousValue() {
        Date val = (Date) getValue();
        Calendar c = Calendar.getInstance();
        c.setTime(val);
        c.add(Calendar.DAY_OF_MONTH, -1);
        Date prev = c.getTime();
        Comparable start = getStart();
        if (start != null && prev.compareTo((Date) start) < 0) return null;
        return prev;
    }
}
