package com.example.weatheralarm.alarm.util;

import android.content.Context;
import android.content.Intent;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.weatheralarm.R;
import com.example.weatheralarm.alarm.model.Alarm;
import com.example.weatheralarm.alarm.activities.PersonalizeAlarmActivity;

import java.util.List;

public final class AlarmAdapter extends RecyclerView.Adapter<AlarmAdapter.ViewHolder> {

    private List<Alarm> mAlarms;

    private String[] mDays;
    private int mEmphasize = -1;

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final Context c = parent.getContext();
        final View v = LayoutInflater.from(c).inflate(R.layout.item_alarm, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        final Context c = holder.itemView.getContext();

        if(mEmphasize == -1) {
            mEmphasize = ContextCompat.getColor(c, R.color.colorPrimaryDark);
        }

        if(mDays == null){
            mDays = c.getResources().getStringArray(R.array.days_abbreviated);
        }

        final Alarm alarm = mAlarms.get(position);

        holder.time.setText(AlarmUtils.getReadableTime(alarm.getTime()));
        holder.amPm.setText(AlarmUtils.getAmPm(alarm.getTime()));
        holder.label.setText(alarm.getLabel());
        holder.days.setText(buildSelectedDays(alarm));
        if(alarm.isWeatherVoice()) {
            holder.weatherColor.setVisibility(View.VISIBLE);
            holder.weatherBlack.setVisibility(View.GONE);
        }
        else {
            holder.weatherColor.setVisibility(View.GONE);
            holder.weatherBlack.setVisibility(View.VISIBLE);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Context c = view.getContext();
                final Intent launchEditAlarmIntent =
                        PersonalizeAlarmActivity.buildAddEditAlarmActivityIntent(
                                c, PersonalizeAlarmActivity.EDIT_ALARM
                        );
                launchEditAlarmIntent.putExtra(PersonalizeAlarmActivity.ALARM_EXTRA, alarm);
                c.startActivity(launchEditAlarmIntent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return (mAlarms == null) ? 0 : mAlarms.size();
    }

    private Spannable buildSelectedDays(Alarm alarm) {

        final int numDays = 7;
        final SparseBooleanArray days = alarm.getDays();

        final SpannableStringBuilder builder = new SpannableStringBuilder();
        ForegroundColorSpan span;

        int startIndex, endIndex;
        for (int i = 0; i < numDays; i++) {

            startIndex = builder.length();

            final String dayText = mDays[i];
            builder.append(dayText);
            builder.append(" ");

            endIndex = startIndex + dayText.length();

            final boolean isSelected = days.valueAt(i);
            if(isSelected) {
                span = new ForegroundColorSpan(mEmphasize);
                builder.setSpan(span, startIndex, endIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
        }

        return builder;

    }

    public void setAlarms(List<Alarm> alarms) {
        mAlarms = alarms;
        notifyDataSetChanged();
    }

    static final class ViewHolder extends RecyclerView.ViewHolder {

        final TextView time, amPm, label, days;
        final ImageView weatherColor, weatherBlack;

        ViewHolder(View itemView) {
            super(itemView);

            time = itemView.findViewById(R.id.ar_time);
            amPm = itemView.findViewById(R.id.ar_am_pm);
            label = itemView.findViewById(R.id.ar_label);
            days = itemView.findViewById(R.id.ar_days);
            weatherColor = itemView.findViewById(R.id.weather_color);
            weatherBlack = itemView.findViewById(R.id.weather_black);

        }
    }

}
