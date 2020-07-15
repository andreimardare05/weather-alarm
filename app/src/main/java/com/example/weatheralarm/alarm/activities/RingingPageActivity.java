package com.example.weatheralarm.alarm.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.weatheralarm.R;
import com.example.weatheralarm.alarm.service.AlarmReceiver;

public final class RingingPageActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ringing_page);
    }

    protected void onDestroy() {
        super.onDestroy();
        AlarmReceiver.playingService.stopRingtone();
    }

    public static Intent launchIntent(Context context) {
        final Intent i = new Intent(context, RingingPageActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        return i;
    }

    /***
     * If the native back button is pressed do nothing.
     */
    @Override
    public void onBackPressed() {
        return;
    }

}
