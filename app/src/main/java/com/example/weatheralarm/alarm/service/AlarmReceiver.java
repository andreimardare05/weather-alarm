package com.example.weatheralarm.alarm.service;

import android.app.AlarmManager;
import android.app.AlarmManager.AlarmClockInfo;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseBooleanArray;


import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import com.example.weatheralarm.R;
import com.example.weatheralarm.alarm.model.Alarm;
import com.example.weatheralarm.alarm.util.AlarmUtils;

import java.util.Calendar;
import java.util.List;

import static android.app.PendingIntent.FLAG_UPDATE_CURRENT;
import static android.os.Build.VERSION.SDK_INT;
import static android.os.Build.VERSION_CODES.O;
import static com.example.weatheralarm.alarm.activities.RingingPageActivity.launchIntent;

public final class AlarmReceiver extends BroadcastReceiver {

    private static final String TAG = AlarmReceiver.class.getSimpleName();
    private static final String CHANNEL_ID = "alarm_channel";
    private static final String BUNDLE_EXTRA = "bundle_extra";
    private static final String ALARM_KEY = "alarm_key";
    public static AlarmPlayingService playingService = null;

    @Override
    public void onReceive(Context context, Intent intent) {
        final Alarm alarm = intent.getBundleExtra(BUNDLE_EXTRA).getParcelable(ALARM_KEY);

        if(alarm == null) {
            Log.e(TAG, "Alarm is null", new NullPointerException());
            return;
        }

        final int id = alarm.notificationId();

        final NotificationManager manager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        createNotificationChannel(context);


        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID);
        builder.setSmallIcon(R.drawable.ic_alarm_white);
        builder.setColor(ContextCompat.getColor(context, R.color.colorPrimaryDark));
        builder.setContentTitle(context.getString(R.string.app_name));
        builder.setContentText("Press notification in order to dismiss ringing alarm.");
        builder.setTicker(alarm.getLabel());
        builder.setContentIntent(launchAlarmLandingPage(context, alarm));
        builder.setAutoCancel(true);
        builder.setPriority(Notification.PRIORITY_MAX);

        manager.notify(id, builder.build());

        playingService = new AlarmPlayingService(context, alarm);

        // Re-schedules all stored alarms
        setReminderAlarm(context, alarm);
    }

    //Convenience method for setting a notification
    public static void setReminderAlarm(Context context, Alarm alarm) {
        //Check whether the alarm is set to run on any days
        if(!AlarmUtils.isAlarmActive(alarm)) {
            //If alarm not set to run on any days, cancel any existing notifications for this alarm
            cancelReminderAlarm(context, alarm);
            return;
        }

        final Calendar nextAlarmTime = getTimeForNextAlarm(alarm);
        alarm.setTime(nextAlarmTime.getTimeInMillis());

        final Intent intent = new Intent(context, AlarmReceiver.class);
        final Bundle bundle = new Bundle();
        bundle.putParcelable(ALARM_KEY, alarm);
        intent.putExtra(BUNDLE_EXTRA, bundle);

        final PendingIntent pIntent = PendingIntent.getBroadcast(
                context,
                alarm.notificationId(),
                intent,
                FLAG_UPDATE_CURRENT
        );
        ScheduleAlarm.with(context).schedule(alarm, pIntent);
    }
    /**
     * Restart the time for the next notification/alarm based on the system time
     */
    public static void setReminderAlarms(Context context, List<Alarm> alarms) {
        for(Alarm alarm : alarms) {
            setReminderAlarm(context, alarm);
        }
    }

    /**
     * Computes the time for the next notification/alarm based on the system time
     */
    private static Calendar getTimeForNextAlarm(Alarm alarm) {

        final Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(alarm.getTime());

        final long currentTime = System.currentTimeMillis();
        final int startIndex = getStartIndexFromTime(calendar);

        int count = 0;
        boolean isAlarmSetForDay;

        final SparseBooleanArray daysArray = alarm.getDays();

        do {
            final int index = (startIndex + count) % 7;
            isAlarmSetForDay =
                    daysArray.valueAt(index) && (calendar.getTimeInMillis() > currentTime);
            if(!isAlarmSetForDay) {
                calendar.add(Calendar.DAY_OF_MONTH, 1);
                count++;
            }
        } while(!isAlarmSetForDay && count < 7);

        return calendar;

    }

    public static void cancelReminderAlarm(Context context, Alarm alarm) {

        final Intent intent = new Intent(context, AlarmReceiver.class);
        final PendingIntent pIntent = PendingIntent.getBroadcast(
                context,
                alarm.notificationId(),
                intent,
                FLAG_UPDATE_CURRENT
        );

        final AlarmManager manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        manager.cancel(pIntent);
    }

    private static int getStartIndexFromTime(Calendar c) {

        final int dayOfWeek = c.get(Calendar.DAY_OF_WEEK);

        int startIndex = 0;
        switch (dayOfWeek) {
            case Calendar.MONDAY:
                startIndex = 0;
                break;
            case Calendar.TUESDAY:
                startIndex = 1;
                break;
            case Calendar.WEDNESDAY:
                startIndex = 2;
                break;
            case Calendar.THURSDAY:
                startIndex = 3;
                break;
            case Calendar.FRIDAY:
                startIndex = 4;
                break;
            case Calendar.SATURDAY:
                startIndex = 5;
                break;
            case Calendar.SUNDAY:
                startIndex = 6;
                break;
        }

        return startIndex;

    }

    private static void createNotificationChannel(Context ctx) {
        if(SDK_INT < O) return;
        final NotificationManager mgr = ctx.getSystemService(NotificationManager.class);
        if(mgr == null) return;

        final String name = ctx.getString(R.string.channel_name);
        if(mgr.getNotificationChannel(name) == null) {
            final NotificationChannel channel =
                    new NotificationChannel(CHANNEL_ID, name,  NotificationManager.IMPORTANCE_HIGH);
            channel.setSound(null,null);
            channel.setBypassDnd(true);
            mgr.createNotificationChannel(channel);
        }
    }

    private static PendingIntent launchAlarmLandingPage(Context ctx, Alarm alarm) {
        return PendingIntent.getActivity(
                ctx, alarm.notificationId(), launchIntent(ctx), FLAG_UPDATE_CURRENT
        );
    }

    private static class ScheduleAlarm {

        @NonNull
        private final Context ctx;
        @NonNull private final AlarmManager am;

        private ScheduleAlarm(@NonNull AlarmManager am, @NonNull Context ctx) {
            this.am = am;
            this.ctx = ctx;
        }

        static ScheduleAlarm with(Context context) {
            final AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            if(am == null) {
                throw new IllegalStateException("Alarm Manager cannot be null.");
            }
            return new ScheduleAlarm(am, context);
        }

        void schedule(Alarm alarm, PendingIntent pi) {
            am.setAlarmClock(new AlarmClockInfo(alarm.getTime(), launchAlarmLandingPage(ctx, alarm)), pi);
        }

    }

}
