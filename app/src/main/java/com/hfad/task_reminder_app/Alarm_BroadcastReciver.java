package com.hfad.task_reminder_app;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Vibrator;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class Alarm_BroadcastReciver extends BroadcastReceiver
{
    final String NotificationChannelName = "MyChannel_1";
    Vibrator vibrator;
    @Override
    public void onReceive(Context context, Intent intent)
    {
        String taskReminder = intent.getStringExtra("Task");
        int position = intent.getIntExtra("Position",0);

        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O)
        {
            NotificationChannel channel = new NotificationChannel(NotificationChannelName,NotificationChannelName, NotificationManager.IMPORTANCE_HIGH);
            NotificationManager manager = context.getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context,NotificationChannelName)
                .setAutoCancel(true)
                .setSmallIcon(R.drawable.notification)
                .setContentTitle(taskReminder)
                .setContentText("Please Check this ...!");
        NotificationManagerCompat managerCompat = NotificationManagerCompat.from(context);
        managerCompat.notify(99,builder.build());

        vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        vibrator.vibrate(1000);

    }
}
