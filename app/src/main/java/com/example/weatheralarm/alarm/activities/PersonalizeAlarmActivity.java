package com.example.weatheralarm.alarm.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.IntDef;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.weatheralarm.R;
import com.example.weatheralarm.alarm.data.DatabaseHelper;
import com.example.weatheralarm.alarm.model.Alarm;
import com.example.weatheralarm.alarm.service.LoaderService;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public final class PersonalizeAlarmActivity extends AppCompatActivity {

    public static final String ALARM_EXTRA = "alarm_extra";
    public static final String MODE_EXTRA = "mode_extra";

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({EDIT_ALARM,ADD_ALARM,UNKNOWN})
    @interface Mode{}
    public static final int EDIT_ALARM = 1;
    public static final int ADD_ALARM = 2;
    public static final int UNKNOWN = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personalize_alarm);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(getToolbarTitle());

        final Alarm alarm = getAlarm();

        if(getSupportFragmentManager().findFragmentById(R.id.edit_alarm_frag_container) == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.edit_alarm_frag_container, PersonalizeAlarmFragment.newInstance(alarm))
                    .commit();
        }
    }

    private Alarm getAlarm() {
        switch (getMode()) {
            case EDIT_ALARM:
                return getIntent().getParcelableExtra(ALARM_EXTRA);
            case ADD_ALARM:
                final long id = DatabaseHelper.getInstance(this).addAlarm();
                LoaderService.launchLoadAlarmsService(this);
                return new Alarm(id);
            case UNKNOWN:
            default:
                throw new IllegalStateException("Invalid mode");
        }
    }

    private @Mode int getMode() {
        final @Mode int mode = getIntent().getIntExtra(MODE_EXTRA, UNKNOWN);
        return mode;
    }

    private String getToolbarTitle() {
        int titleResId;
        switch (getMode()) {
            case EDIT_ALARM:
                titleResId = R.string.edit_alarm;
                break;
            case ADD_ALARM:
                titleResId = R.string.add_alarm;
                break;
            case UNKNOWN:
            default:
                throw new IllegalStateException("Invalid mode");
        }
        return getString(titleResId);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    public static Intent buildAddEditAlarmActivityIntent(Context context, @Mode int mode) {
        final Intent i = new Intent(context, PersonalizeAlarmActivity.class);
        i.putExtra(MODE_EXTRA, mode);
        return i;
    }

}
