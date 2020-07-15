package com.example.weatheralarm.alarm.service;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.example.weatheralarm.alarm.data.DatabaseHelper;
import com.example.weatheralarm.alarm.model.Alarm;

import java.util.ArrayList;
import java.util.List;

public final class LoaderService extends IntentService {

    private static final String TAG = LoaderService.class.getSimpleName();
    public static final String ACTION_COMPLETE = TAG + ".ACTION_COMPLETE";
    public static final String ALARMS_EXTRA = "alarms_extra";

    public LoaderService() {
        this(TAG);
    }

    public LoaderService(String name){
        super(name);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        final List<Alarm> alarms = DatabaseHelper.getInstance(this).getAlarms();
        final Intent i = new Intent(ACTION_COMPLETE);
        i.putParcelableArrayListExtra(ALARMS_EXTRA, new ArrayList<>(alarms));
        LocalBroadcastManager.getInstance(this).sendBroadcast(i);
    }

    public static void launchLoadAlarmsService(Context context) {
        final Intent launchLoadAlarmsServiceIntent = new Intent(context, LoaderService.class);
        context.startService(launchLoadAlarmsServiceIntent);
    }

}
