package com.example.weatheralarm.alarm.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.SparseBooleanArray;

import androidx.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public final class Alarm implements Parcelable {
    @Retention(RetentionPolicy.SOURCE)
    @IntDef({MON,TUES,WED,THURS,FRI,SAT,SUN})

    @interface Days{}
    public static final int MON = 1;
    public static final int TUES = 2;
    public static final int WED = 3;
    public static final int THURS = 4;
    public static final int FRI = 5;
    public static final int SAT = 6;
    public static final int SUN = 7;

    private static final long NO_ID = -1;

    private final long id;
    private long time;
    private String label;
    private SparseBooleanArray allDays;
    private boolean isEnabled;
    private boolean weatherVoice;

    private Alarm(Parcel in) {
        id = in.readLong();
        time = in.readLong();
        label = in.readString();
        allDays = in.readSparseBooleanArray();
        isEnabled = in.readByte() != 0;
        weatherVoice = in.readBoolean();
    }

    public static final Creator<Alarm> CREATOR = new Creator<Alarm>() {
        @Override
        public Alarm createFromParcel(Parcel in) {
            return new Alarm(in);
        }

        @Override
        public Alarm[] newArray(int size) {
            return new Alarm[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeLong(id);
        parcel.writeLong(time);
        parcel.writeString(label);
        parcel.writeSparseBooleanArray(allDays);
        parcel.writeByte((byte) (isEnabled ? 1 : 0));
        parcel.writeByte((byte) (weatherVoice ? 1 : 0));
    }

    public Alarm() {
        this(NO_ID);
    }

    public Alarm(long id) {
        this(id, System.currentTimeMillis());
    }

    public Alarm(long id, long time, @Days int... days) {
        this(id, time, null, false, days);
    }

    public Alarm(long id, long time, String label, boolean weatherVoice, @Days int... days) {
        this.id = id;
        this.time = time;
        this.label = label;
        this.allDays = buildDaysArray(days);
        this.weatherVoice = weatherVoice;
    }

    public long getId() {
        return id;
    }

    public boolean isWeatherVoice() {
        return weatherVoice;
    }

    public void setWeatherVoice(boolean weatherVoice) {
        this.weatherVoice = weatherVoice;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public long getTime() {
        return time;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    public void setDay(@Days int day, boolean isAlarmed) {
        allDays.append(day, isAlarmed);
    }

    public SparseBooleanArray getDays() {
        return allDays;
    }

    public boolean getDay(@Days int day){
        return allDays.get(day);
    }

    public void setIsEnabled(boolean isEnabled) {
        this.isEnabled = isEnabled;
    }

    public boolean isEnabled() {
        return isEnabled;
    }

    public int notificationId() {
        final long id = getId();
        return (int) (id^(id>>>32));
    }

    @Override
    public String toString() {
        return "Alarm{" +
                "id=" + id +
                ", time=" + time +
                ", label='" + label + '\'' +
                ", allDays=" + allDays +
                ", isEnabled=" + isEnabled +
                '}';
    }

    private static SparseBooleanArray buildDaysArray(@Days int... days) {

        final SparseBooleanArray array = buildBaseDaysArray();

        for (@Days int day : days) {
            array.append(day, true);
        }

        return array;

    }

    private static SparseBooleanArray buildBaseDaysArray() {

        final int numDays = 7;

        final SparseBooleanArray array = new SparseBooleanArray(numDays);

        array.put(MON, false);
        array.put(TUES, false);
        array.put(WED, false);
        array.put(THURS, false);
        array.put(FRI, false);
        array.put(SAT, false);
        array.put(SUN, false);

        return array;

    }

}
