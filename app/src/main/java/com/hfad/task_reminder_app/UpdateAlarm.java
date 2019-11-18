package com.hfad.task_reminder_app;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class UpdateAlarm extends AppCompatActivity implements View.OnClickListener
{
    Intent intent;
    int position;
    Alarm_Data data;
    EditText updateTitle;
    TextView times,dates;
    Button saveReminder;

    Calendar calendar;

    boolean isTimeUpDated = false, isDateUpDated = false;

    TimePickerDialog timePickerDialog;
    DatePickerDialog datePickerDialog;

    SQLiteDatabase database;
    MyDB_Helper helper;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_alarm);

        setInitialReferences();
        displayPreviousData();
    }
    public void setInitialReferences()
    {
        intent = getIntent();
        position = intent.getIntExtra("Data_Position",0); // Data Position in AlarmData's List
        data = Alarm_Data.data.get(position); // Selected Data

        updateTitle = findViewById(R.id.enter_reminder_field_update);
        times = findViewById(R.id.updateAlarm_time);
        dates = findViewById(R.id.updateAlarm_date);
        saveReminder = findViewById(R.id.updateReminder_Btns);

        times.setOnClickListener(this);
        dates.setOnClickListener(this);
        saveReminder.setOnClickListener(this);

        helper = new MyDB_Helper(this);
        database = helper.getWritableDatabase();

    }

    public void displayPreviousData()
    {
        updateTitle.setText(data.getTask_title());
        calendar = data.getCalendar();

        setTime(calendar);
        setDate(calendar);
    }

    public void setTime(Calendar calendar)
    {
        String prev_Time = DateFormat.getTimeInstance(DateFormat.SHORT).format(calendar.getTime());
        times.setText(prev_Time);
    }

    public void setDate(Calendar calendar)
    {
        Date date = calendar.getTime();
        DateFormat dateFormat = new SimpleDateFormat("MM-dd-yy");
        String prev_Date = dateFormat.format(date);
        dates.setText(prev_Date);
    }

    @Override
    public void onClick(View v)
    {
        // get Current Date and Time
        final Calendar current_Calendar = Calendar.getInstance();

        switch (v.getId())
        {
            case R.id.updateAlarm_time:
            {
                timePickerDialog = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener()
                {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute)
                    {
                        calendar.set(Calendar.HOUR,hourOfDay);
                        calendar.set(Calendar.MINUTE,minute);
                        calendar.set(Calendar.SECOND,0);
                        isTimeUpDated = true;
                        setTime(calendar);
                    }
                },current_Calendar.get(Calendar.HOUR),current_Calendar.get(Calendar.MINUTE),false);
                timePickerDialog.show();
            }
            break;

            case R.id.updateAlarm_date:
            {
                datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth)
                    {
                         Calendar calendar1 = Calendar.getInstance();
                         calendar1.set(Calendar.YEAR,year);
                         calendar1.set(Calendar.MONTH,month);
                         calendar1.set(Calendar.DAY_OF_MONTH,dayOfMonth);

                         int i= calendar1.compareTo(current_Calendar);

                         if(i==0||i==1)
                         {
                             calendar.set(Calendar.YEAR,year);
                             calendar.set(Calendar.MONTH,month);
                             calendar.set(Calendar.DAY_OF_MONTH,dayOfMonth);
                             isDateUpDated = true;
                             setDate(calendar);
                         }
                         else
                         {
                             Toast.makeText(UpdateAlarm.this,"Please Select Date of Coming Time!",Toast.LENGTH_LONG).show();
                         }

                    }
                },current_Calendar.get(Calendar.YEAR),current_Calendar.get(Calendar.MONTH),current_Calendar.get(Calendar.DAY_OF_MONTH));
                datePickerDialog.show();
            }
            break;

            case R.id.updateReminder_Btns:
            {
                String newTitle = updateTitle.getText().toString();
                if(newTitle.isEmpty())
                {
                    Toast.makeText(this,"You Need to Enter Some Reminder!",Toast.LENGTH_SHORT).show();
                }
                else
                {
                    // remove alarm from old data
                    AlarmManager alarmManager = Add_Alarm.alarmManagers[position];
                    Intent intent = Add_Alarm.intents[position];
                    PendingIntent pendingIntent = PendingIntent.getBroadcast(this,position,intent,0);
                    alarmManager.cancel(pendingIntent);

                    int id = data.get_ReminderId();
                    Alarm_Data.data.remove(data);  // remove old data

                    Alarm_Data newData = new Alarm_Data();
                    newData.set_ReminderId(id);
                    newData.setTask_title(newTitle);
                    newData.setCalendar(calendar);

                    Alarm_Data.data.add(position,newData);

                    // add to database
                    helper.update_reminder(id,database);

                    // add alarm to new data
                    AlarmManager alarmManagers = Add_Alarm.alarmManagers[position];
                    Intent intents = Add_Alarm.intents[position];
                    PendingIntent pendingIntents = PendingIntent.getBroadcast(this,position,intents,0);
                    alarmManagers.setExact(AlarmManager.RTC_WAKEUP,calendar.getTimeInMillis(),pendingIntents);


                    Intent intentss = new Intent(this,Home.class);
                    startActivity(intentss);
                }
            }
        }
    }
}
