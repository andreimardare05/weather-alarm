package com.example.weatheralarm;


import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;

import androidx.fragment.app.DialogFragment;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

import static android.content.Context.ALARM_SERVICE;

public class AlarmDialogFragment extends DialogFragment {
    @Override
    public Dialog onCreateDialog(final Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();

        builder.setView(inflater.inflate(R.layout.alarm_dialog, null))
                .setTitle(R.string.title_dialog)
                .setPositiveButton(R.string.fire, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        System.out.println("The alarm has been set.");
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        System.out.println("The alarm has not been saved.");
                    }
                });
        // Create the AlertDialog object and return it
        return builder.create();
    }

    public void setAlarm(){

        int hour = 18;
        int minute = 15;
        String myTime = String.valueOf(hour) + ":" + String.valueOf(minute);

        Date date = null;

        // today at your defined time Calendar
        Calendar customCalendar = new GregorianCalendar();
        // set hours and minutes
        customCalendar.set(Calendar.HOUR_OF_DAY, hour);
        customCalendar.set(Calendar.MINUTE, minute);
        customCalendar.set(Calendar.SECOND, 0);
        customCalendar.set(Calendar.MILLISECOND, 0);

        Date customDate = customCalendar.getTime();

        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());
        try {

            date = sdf.parse(myTime);

        } catch (ParseException e) {

            e.printStackTrace();
        }

        if (date != null) {
            timeInMs = customDate.getTime();
        }

        Intent intent = new Intent(this, AlarmClockRingRing.class);
        PendingIntent action = PendingIntent.getActivity(this, 0,intent, Intent.FLAG_ACTIVITY_NEW_TASK);

        AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);
        am.set(AlarmManager.RTC_WAKEUP, timeInMs, action);
    }
}