package com.hfad.task_reminder_app;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Vibrator;
import android.widget.Switch;

public class Settings extends AppCompatActivity
{
    Switch vibrateSwitch;
    static boolean ischeck;
    static
    {
        ischeck = true;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        vibrateSwitch = findViewById(R.id.switch_Vibrate);
        check();
    }

    public void check()
    {
        if(vibrateSwitch.isChecked())
        {
          ischeck = true;
        }
        else if(vibrateSwitch.isChecked()==false)
        {
            ischeck = false;
        }
    }
}
