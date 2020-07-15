package com.example.weatheralarm.alarm.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.weatheralarm.R;
import com.example.weatheralarm.alarm.data.DatabaseHelper;
import com.example.weatheralarm.alarm.model.Alarm;
import com.example.weatheralarm.alarm.service.AlarmReceiver;
import com.example.weatheralarm.alarm.service.LoaderService;
import com.example.weatheralarm.alarm.util.TimepickerUtils;
import java.util.Calendar;

public final class PersonalizeAlarmFragment extends Fragment {

    private TimePicker mTimePicker;
    private EditText mLabel;
    private CheckBox mMon, mTues, mWed, mThurs, mFri, mSat, mSun;
    private CheckBox voiceWeather;

    public static PersonalizeAlarmFragment newInstance(Alarm alarm) {

        Bundle args = new Bundle();
        args.putParcelable(PersonalizeAlarmActivity.ALARM_EXTRA, alarm);

        PersonalizeAlarmFragment fragment = new PersonalizeAlarmFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        final View v = inflater.inflate(R.layout.fragment_personalize_alarm, container, false);

        setHasOptionsMenu(true);

        final Alarm alarm = getAlarm();

        mTimePicker = (TimePicker) v.findViewById(R.id.edit_alarm_time_picker);
        TimepickerUtils.setTimePickerTime(mTimePicker, alarm.getTime());

        mLabel = (EditText) v.findViewById(R.id.edit_alarm_label);
        mLabel.setText(alarm.getLabel());

        mMon = (CheckBox) v.findViewById(R.id.edit_alarm_mon);
        mTues = (CheckBox) v.findViewById(R.id.edit_alarm_tues);
        mWed = (CheckBox) v.findViewById(R.id.edit_alarm_wed);
        mThurs = (CheckBox) v.findViewById(R.id.edit_alarm_thurs);
        mFri = (CheckBox) v.findViewById(R.id.edit_alarm_fri);
        mSat = (CheckBox) v.findViewById(R.id.edit_alarm_sat);
        mSun = (CheckBox) v.findViewById(R.id.edit_alarm_sun);

        voiceWeather = (CheckBox) v.findViewById(R.id.checkbox_weather);

        setDayCheckboxes(alarm);

        setWeatherCheckbox(alarm);

        return v;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.edit_alarm_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_save:
                save();
                break;
            case R.id.action_delete:
                delete();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private Alarm getAlarm() {
        return getArguments().getParcelable(PersonalizeAlarmActivity.ALARM_EXTRA);
    }

    private void setDayCheckboxes(Alarm alarm) {
        mMon.setChecked(alarm.getDay(Alarm.MON));
        mTues.setChecked(alarm.getDay(Alarm.TUES));
        mWed.setChecked(alarm.getDay(Alarm.WED));
        mThurs.setChecked(alarm.getDay(Alarm.THURS));
        mFri.setChecked(alarm.getDay(Alarm.FRI));
        mSat.setChecked(alarm.getDay(Alarm.SAT));
        mSun.setChecked(alarm.getDay(Alarm.SUN));
    }

    private void setWeatherCheckbox(Alarm alarm) {
        voiceWeather.setChecked(alarm.isWeatherVoice());
    }

    private void save() {

        final Alarm alarm = getAlarm();

        final Calendar time = Calendar.getInstance();
        time.set(Calendar.MINUTE, TimepickerUtils.getTimePickerMinute(mTimePicker));
        time.set(Calendar.HOUR_OF_DAY, TimepickerUtils.getTimePickerHour(mTimePicker));
        alarm.setTime(time.getTimeInMillis());

        if (mLabel.getText().toString().length() > 0) {
            alarm.setLabel(mLabel.getText().toString());
        }
        else {
            alarm.setLabel("Alarm");
        }

        alarm.setDay(Alarm.MON, mMon.isChecked());
        alarm.setDay(Alarm.TUES, mTues.isChecked());
        alarm.setDay(Alarm.WED, mWed.isChecked());
        alarm.setDay(Alarm.THURS, mThurs.isChecked());
        alarm.setDay(Alarm.FRI, mFri.isChecked());
        alarm.setDay(Alarm.SAT, mSat.isChecked());
        alarm.setDay(Alarm.SUN, mSun.isChecked());

        alarm.setWeatherVoice(voiceWeather.isChecked());

        final int rowsUpdated = DatabaseHelper.getInstance(getContext()).updateAlarm(alarm);
        final int messageId = (rowsUpdated == 1) ? R.string.update_complete : R.string.update_failed;

        Toast.makeText(getContext(), messageId, Toast.LENGTH_SHORT).show();

        AlarmReceiver.setReminderAlarm(getContext(), alarm);

        getActivity().finish();

    }

    private void delete() {

        final Alarm alarm = getAlarm();

        final AlertDialog.Builder builder =
                new AlertDialog.Builder(getContext(), R.style.DialogTheme);
        builder.setTitle(R.string.delete_dialog_title);
        builder.setMessage(R.string.delete_dialog_content);
        builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                AlarmReceiver.cancelReminderAlarm(getContext(), alarm);
                final int rowsDeleted = DatabaseHelper.getInstance(getContext()).deleteAlarm(alarm);
                int messageId;
                if(rowsDeleted == 1) {
                    messageId = R.string.delete_complete;
                    Toast.makeText(getContext(), messageId, Toast.LENGTH_SHORT).show();
                    LoaderService.launchLoadAlarmsService(getContext());
                    getActivity().finish();
                } else {
                    messageId = R.string.delete_failed;
                    Toast.makeText(getContext(), messageId, Toast.LENGTH_SHORT).show();
                }
            }
        });
        builder.setNegativeButton(R.string.no, null);
        builder.show();

    }

}
