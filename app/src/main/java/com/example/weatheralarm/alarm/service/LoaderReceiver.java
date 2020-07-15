package com.example.weatheralarm.alarm.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.example.weatheralarm.alarm.model.Alarm;

import java.util.ArrayList;

public final class LoaderReceiver extends BroadcastReceiver {

    private OnAlarmsLoadedListener mListener;

    public LoaderReceiver(OnAlarmsLoadedListener listener){
        mListener = listener;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        final ArrayList<Alarm> alarms =
                intent.getParcelableArrayListExtra(LoaderService.ALARMS_EXTRA);
        mListener.onAlarmsLoaded(alarms);
    }

    public interface OnAlarmsLoadedListener {
        void onAlarmsLoaded(ArrayList<Alarm> alarms);
    }

}
