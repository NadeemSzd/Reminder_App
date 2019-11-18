package com.hfad.task_reminder_app;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;


public class RecyclerView_AlarmData extends RecyclerView.Adapter<RecyclerView_AlarmData.Reminder_ViewHolder>
{

    SQLiteDatabase database;
    MyDB_Helper helper;
    List<Alarm_Data> alarm_data;
    LayoutInflater inflater;

    View parentView;
    Context context;

    RecyclerView_AlarmData(Context context,List<Alarm_Data> data)
    {
        this.context = context;
        alarm_data = data;
        inflater = LayoutInflater.from(context);
        helper = new MyDB_Helper(context);
        database = helper.getWritableDatabase();
    }

    @NonNull
    @Override
    public Reminder_ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        View view = inflater.inflate(R.layout.reminder_layout,parent,false);
        Reminder_ViewHolder viewHolder = new Reminder_ViewHolder(view);
        parentView = parent;
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull Reminder_ViewHolder holder, int position)
    {
          holder.setData(position);
    }

    @Override
    public int getItemCount()
    {
        return alarm_data.size();
    }

    public class Reminder_ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
    {
        TextView task_title,alarmTime,alarmDate;
        ImageView delete,edit;
        int position;

        public Reminder_ViewHolder(@NonNull View itemView)
        {
            super(itemView);
            task_title = itemView.findViewById(R.id.alarm_title);
            delete = itemView.findViewById(R.id.delete_icon);
            edit = itemView.findViewById(R.id.edit_icon);
            alarmTime = itemView.findViewById(R.id.alarm_time);
            alarmDate = itemView.findViewById(R.id.alarm_date);
        }

        public void setData(int position)
        {
           // Alarm_Data data = Alarm_Data.data.get(position);
            Alarm_Data data = alarm_data.get(position);

            String title = data.getTask_title();
            Calendar calendar = data.getCalendar();

            String calendarTime = DateFormat.getTimeInstance(DateFormat.SHORT).format(calendar.getTime());
            Date alarm_date = calendar.getTime();
            DateFormat formats = new SimpleDateFormat("MM-dd-yy");
            String calendarDate = formats.format(alarm_date);

            this.task_title.setText(title);
            this.alarmTime.setText(calendarTime);
            this.alarmDate.setText(calendarDate);
            this.position = position;

            // set delete and edit listeners
            this.delete.setOnClickListener(this);
            this.edit.setOnClickListener(this);
        }

        @Override
        public void onClick(View v)
        {
           switch (v.getId())
           {
               case R.id.delete_icon:
               {
                   removeItem(position);
               }
               break;
               case R.id.edit_icon:
               {
                   updateItem(position);
               }
           }
        }

        private void updateItem(int position)
        {
            Intent intent = new Intent(context,UpdateAlarm.class);
            intent.putExtra("Data_Position",position);
            itemView.getContext().startActivity(intent);
        }

        public void removeItem(final int position)
        {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setMessage("Do you really want to delete this item!")
                    .setCancelable(false)
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener()
                    {
                        @Override
                        public void onClick(DialogInterface dialog, int which)
                        {
                            final Alarm_Data data = alarm_data.get(position);

                            int id = data.get_ReminderId();

                            alarm_data.remove(data);
                            notifyItemRemoved(position);
                            notifyItemRangeChanged(position,alarm_data.size());

                            helper.delete_reminder(id,database);

                            final AlarmManager alarmManager = Add_Alarm.alarmManagers[position];
                            Intent intent = Add_Alarm.intents[position];
                            final PendingIntent pendingIntent = PendingIntent.getBroadcast(context,position,intent,0);

                            alarmManager.cancel(pendingIntent);

                            Snackbar snackbar = Snackbar.make(parentView,"Item is Removed Successfully",Snackbar.LENGTH_LONG);
                            snackbar.setAction("UNDO", new View.OnClickListener()
                            {
                                @Override
                                public void onClick(View v)
                                {
                                    alarm_data.add(position,data);
                                    notifyItemInserted(position);
                                    notifyItemRangeChanged(position,alarm_data.size());

                                    helper.insertData(database,data);

                                    alarmManager.setExact(AlarmManager.RTC_WAKEUP,data.getCalendar().getTimeInMillis(),pendingIntent);

                                }
                            });
                            snackbar.show();
                        }
                    })
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which)
                        {
                            dialog.cancel();
                        }
                    });
             AlertDialog alertDialog = builder.create();
             alertDialog.setTitle("Remove Item");
             alertDialog.show();
        }
    }
}
