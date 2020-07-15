package com.example.weatheralarm.alarm.activities;

import android.content.IntentFilter;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.weatheralarm.R;
import com.example.weatheralarm.alarm.util.AlarmAdapter;
import com.example.weatheralarm.alarm.model.Alarm;
import com.example.weatheralarm.alarm.service.LoaderReceiver;
import com.example.weatheralarm.alarm.service.LoaderService;
import com.example.weatheralarm.alarm.util.LineBelowAlarm;
import com.example.weatheralarm.alarm.util.EmptyRecyclerView;

import java.util.ArrayList;

public final class FirstFragment extends Fragment
        implements LoaderReceiver.OnAlarmsLoadedListener {

    private LoaderReceiver mReceiver;
    private AlarmAdapter mAdapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mReceiver = new LoaderReceiver(this);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        final View v = inflater.inflate(R.layout.fragment_first, container, false);

        final EmptyRecyclerView rv = v.findViewById(R.id.recycler);
        mAdapter = new AlarmAdapter();
        rv.setEmptyView(v.findViewById(R.id.empty_view));
        rv.setAdapter(mAdapter);
        rv.addItemDecoration(new LineBelowAlarm(getContext()));
        rv.setLayoutManager(new LinearLayoutManager(getContext()));
        rv.setItemAnimator(new DefaultItemAnimator());
        return v;

    }

    @Override
    public void onStart() {
        super.onStart();
        final IntentFilter filter = new IntentFilter(LoaderService.ACTION_COMPLETE);
        LocalBroadcastManager.getInstance(getContext()).registerReceiver(mReceiver, filter);
        LoaderService.launchLoadAlarmsService(getContext());
    }

    @Override
    public void onStop() {
        super.onStop();
        LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(mReceiver);
    }

    @Override
    public void onAlarmsLoaded(ArrayList<Alarm> alarms) {
        mAdapter.setAlarms(alarms);
    }

}
