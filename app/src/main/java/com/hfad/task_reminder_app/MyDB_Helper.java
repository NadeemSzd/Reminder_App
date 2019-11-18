package com.hfad.task_reminder_app;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

import androidx.annotation.Nullable;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class MyDB_Helper extends SQLiteOpenHelper
{
    public static final String DB_NAME = "ReminderApp";
    public static final int VERSION = 1;

    Context context;
    public MyDB_Helper(Context context)
    {
        super(context, DB_NAME, null, VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db)
    {
        String createTable = "CREATE TABLE REMINDERS_LIST(_id INTEGER PRIMARY KEY AUTOINCREMENT,"+"REMINDER TEXT,"
                +"DATE_AND_TIME TEXT)";
        db.execSQL(createTable);
    }

    public void insertData(SQLiteDatabase database,Alarm_Data data)
    {
       String reminder = data.getTask_title();
       Calendar calendar = data.getCalendar();

       String time = DateFormat.getTimeInstance(DateFormat.SHORT).format(calendar.getTime());

       Date calendardates = calendar.getTime();
       DateFormat format = new SimpleDateFormat("MM-dd-yy");
       String date = format.format(calendardates);

       String dateANDtime = date+" "+time;

       ContentValues values = new ContentValues();
       values.put("REMINDER",reminder);
       values.put("DATE_AND_TIME",dateANDtime);

       long i=database.insert("REMINDERS_LIST",null,values);
       if(i!=-1)
       {
           Toast.makeText(context,"Data Inserted Successfully",Toast.LENGTH_LONG).show();
       }
       else
       {
           Toast.makeText(context,"Data Not Inserted!",Toast.LENGTH_LONG).show();
       }

    }

    public void delete_reminder(int id,SQLiteDatabase db)
    {
        String query = "DELETE FROM REMINDERS_LIST WHERE _id = "+id;
        db.execSQL(query);
    }

    public void update_reminder(int id,SQLiteDatabase database)
    {
        Alarm_Data data = Alarm_Data.data.get(id);
        int position = data.get_ReminderId();
        String title = data.getTask_title();
        Calendar calendar = data.getCalendar();

        String time = DateFormat.getTimeInstance(DateFormat.SHORT).format(calendar.getTime());
        Date calendardates = calendar.getTime();
        DateFormat format = new SimpleDateFormat("MM-dd-yy");
        String date = format.format(calendardates);
        String dateANDtime = date+" "+time;

        String TABLE_NAME = "REMINDERS_LIST";
        String COL1 = "_id";
        String COL2 = "REMINDER";
        String COL3 = "DATE_AND_TIME";

        /*  ContentValues values = new ContentValues();
        values.put("REMINDER",title);
        values.put("DATE_AND_TIME",dateANDtime);

        database.update("REMINDERS_LIST",values,"_id=?",new String[]{"4"});*/

        String query = "UPDATE " + TABLE_NAME + " SET " + COL2 + " = '"+ title +"'" + COL3 + " = '"+ dateANDtime
                +"' WHERE "+COL1+" = '"+id+"'";
        database.execSQL(query);
    }



    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {

    }
}
