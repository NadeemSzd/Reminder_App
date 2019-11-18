package com.hfad.task_reminder_app;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class Alarm_Data
{
    private int id;
    private String task_title;
    private Calendar calendar;
    static List<Alarm_Data> data;

    static boolean isFirstTime;

    // static block
    static
    {
        isFirstTime = true;
        data = new ArrayList<>();
    }

    public int get_ReminderId()
    {
        return id;
    }
    public void set_ReminderId(int id)
    {
        this.id = id;
    }

    public String getTask_title()
    {
        return task_title;
    }
    public void setTask_title(String task_title)
    {
        this.task_title = task_title;
    }

    public Calendar getCalendar()
    {
        return calendar;
    }
    public void setCalendar(Calendar calendar)
    {
        this.calendar = calendar;
    }

    public static List<Alarm_Data> getListOfAlarms()
    {

        return data;
    }

}
