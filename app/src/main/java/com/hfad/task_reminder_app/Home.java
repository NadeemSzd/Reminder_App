package com.hfad.task_reminder_app;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class Home extends AppCompatActivity
{

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setSubtitle("All Reminders");

        // getting data from Database
        getDataFromDataBase();

        RecyclerView recyclerView = findViewById(R.id.reCyclerView);
        RecyclerView_AlarmData alarmData = new RecyclerView_AlarmData(this,Alarm_Data.getListOfAlarms());
        recyclerView.setAdapter(alarmData);

        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(manager);
    }

    public void getDataFromDataBase()
    {
        MyDB_Helper helper = new MyDB_Helper(this);
        SQLiteDatabase database = helper.getReadableDatabase();
        Cursor cursor = database.rawQuery("SELECT _id,REMINDER, DATE_AND_TIME FROM REMINDERS_LIST",new String[]{});

        Alarm_Data.data.clear();
        String title,dateANDtime;
        int id;

        while (cursor.moveToNext())
        {
            id = cursor.getInt(0);
            title = cursor.getString(1);
            dateANDtime = cursor.getString(2);

            try
            {
                setData(id,title,dateANDtime);
            }
            catch (Exception e)
            {
                Toast.makeText(this,"Exception",Toast.LENGTH_LONG).show();
            }
        }
    }

    public void setData(int id,String title,String date) throws ParseException
    {
        Alarm_Data alarm_data;
        Calendar calendar =Calendar.getInstance();

        Date dateFormat = new SimpleDateFormat("MM-dd-yy HH:mm").parse(date);
        calendar.setTime(dateFormat);

        alarm_data = new Alarm_Data();
        alarm_data.set_ReminderId(id);
        alarm_data.setTask_title(title);
        alarm_data.setCalendar(calendar);

        Alarm_Data.data.add(alarm_data);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.home_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.add_Alarm_menu:
            {
                Intent intent = new Intent(this,Add_Alarm.class);
                startActivity(intent);
            }
            break;
            case R.id.settings:
            {
                Intent intent = new Intent(this,Settings.class);
                startActivity(intent);
            }
            break;
        }
        return true;
    }

    public void Add_Alarms(View view)
    {
        Intent intent = new Intent(this,Add_Alarm.class);
        startActivity(intent);
    }
}
