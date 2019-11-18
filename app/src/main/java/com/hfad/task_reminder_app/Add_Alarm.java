package com.hfad.task_reminder_app;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.icu.util.LocaleData;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.time.LocalTime;
import java.util.Calendar;
import java.util.Date;

public class Add_Alarm extends AppCompatActivity implements View.OnClickListener
{
    TextView time,date;
    EditText reminder;
    Button addAlarm;

    int currentYear,currentMonth,currentDay,currentHour,currentMinute,currentSecond;

    TimePickerDialog timePickerDialog;
    DatePickerDialog datePickerDialog;

    static Intent[] intents ;
    static AlarmManager[] alarmManagers ;

    Calendar calendar;

    SQLiteDatabase database;
    MyDB_Helper helper;

    static int total_Reminder = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add__alarm);

        setReferences();
        CurrentDateANDTime();
        setonClickListeners();
    }

    public void setReferences()
    {
        calendar = Calendar.getInstance();
        time = findViewById(R.id.addAlarm_time);
        date = findViewById(R.id.addAlarm_date);
        reminder = findViewById(R.id.enter_reminder_field);
        addAlarm = findViewById(R.id.addAlarm_Btns);

        helper = new MyDB_Helper(this);
        database = helper.getWritableDatabase();
    }

    public void CurrentDateANDTime()
    {
        currentYear = calendar.get(Calendar.YEAR);
        currentMonth = calendar.get(Calendar.MONTH);
        currentDay = calendar.get(Calendar.DAY_OF_MONTH);

        currentHour = calendar.get(Calendar.HOUR);
        currentMinute = calendar.get(Calendar.MINUTE);
        currentSecond = calendar.get(Calendar.SECOND);

        setTime(calendar);
        setDate(calendar);
       // setCurrentDate(currentYear,currentMonth,currentDay);
    }

    public void setTime(Calendar calendar)
    {
        String calendarTime = DateFormat.getTimeInstance(DateFormat.SHORT).format(calendar.getTime());
        time.setText(calendarTime);
    }

    public void setDate(Calendar calendar)
    {
        Date calendardates = calendar.getTime();
        DateFormat format = new SimpleDateFormat("MM-dd-yy");
        String calendarDate = format.format(calendardates);
        date.setText(calendarDate);
    }

    public void setonClickListeners()
    {
        time.setOnClickListener(this);
        date.setOnClickListener(this);
        reminder.setOnClickListener(this);
        addAlarm.setOnClickListener(this);
    }

    @Override
    public void onClick(View v)
    {
        String task = reminder.getText().toString();
        switch(v.getId())
        {
            case R.id.addAlarm_time:
            {
                   timePickerDialog = new TimePickerDialog(
                           Add_Alarm.this,
                           new TimePickerDialog.OnTimeSetListener()
                           {
                               @Override
                               public void onTimeSet(TimePicker view, int hourOfDay, int minute)
                               {
                                   calendar.set(Calendar.HOUR,hourOfDay);
                                   calendar.set(Calendar.MINUTE,minute);
                                   calendar.set(Calendar.SECOND,0);
                                   setTime(calendar);
                               }
                           },currentHour,currentMinute,false);
                   timePickerDialog.show();
            }
            break;

            case R.id.addAlarm_date:
            {
                datePickerDialog = new DatePickerDialog(
                        Add_Alarm.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth)
                    {
                        Calendar calendar1 = Calendar.getInstance();
                        calendar1.set(Calendar.YEAR,year);
                        calendar1.set(Calendar.MONTH,month);
                        calendar1.set(Calendar.DAY_OF_MONTH,dayOfMonth);
                        // check whether current date is less or greater than picked date
                        int i = calendar1.compareTo(calendar);

                        if(i==0 || i==1)
                        {
                            calendar.set(Calendar.YEAR,year);
                            calendar.set(Calendar.MONTH,month);
                            calendar.set(Calendar.DAY_OF_MONTH,dayOfMonth);

                            setDate(calendar);
                        }
                        else
                        {
                            Toast.makeText(Add_Alarm.this,"Please Select Date of Coming Time!",Toast.LENGTH_LONG).show();
                        }
                       // setCurrentDate(year,month,dayOfMonth);
                    }
                },currentYear,currentMonth,currentDay);
                datePickerDialog.show();
            }
            break;

            case R.id.addAlarm_Btns:
            {
                if(task.isEmpty())
                {
                    Toast.makeText(this,"Please Enter Task First!",Toast.LENGTH_SHORT).show();
                }
                else
                {
                        Alarm_Data alarm_data = new Alarm_Data();
                        alarm_data.setTask_title(task);
                        alarm_data.setCalendar(calendar);
                        Alarm_Data.data.add(alarm_data);

                        int size = Alarm_Data.data.size();
                        intents = new Intent[size];
                        alarmManagers = new AlarmManager[size];

                        for (int i = 0; i < alarmManagers.length; i++)
                        {
                            Alarm_Data alarmData = Alarm_Data.data.get(i);
                            Calendar calendar1 = alarmData.getCalendar();
                            intents[i] = new Intent(getApplicationContext(), Alarm_BroadcastReciver.class);
                            intents[i].putExtra("Task", alarmData.getTask_title());
                            intents[i].putExtra("Position",i);

                            PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), i, intents[i], 0);
                            alarmManagers[i] = (AlarmManager) getApplicationContext().getSystemService(ALARM_SERVICE);
                            alarmManagers[i].setExact(AlarmManager.RTC_WAKEUP, calendar1.getTimeInMillis(), pendingIntent);
                        }

                        // add Data to database
                        helper.insertData(database,alarm_data);

                        Intent intentss = new Intent(this, Home.class);
                        startActivity(intentss);
                }
            }
            break;
        }

    }
}
