package com.example.weatheralarm.alarm.service;

import android.app.AlarmManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.example.weatheralarm.alarm.data.DatabaseHelper;
import com.example.weatheralarm.alarm.model.Alarm;

import java.util.List;
import java.util.concurrent.Executors;

import static android.content.Intent.ACTION_BOOT_COMPLETED;
import static com.example.weatheralarm.alarm.service.AlarmReceiver.setReminderAlarms;

/**
 * Re-schedules all stored alarms.
 */
public class BootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
            Executors.newSingleThreadExecutor().execute(() -> {
                final List<Alarm> alarms = DatabaseHelper.getInstance(context).getAlarms();
                setReminderAlarms(context, alarms);
            });
        }
    }
}
