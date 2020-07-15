package com.example.weatheralarm.alarm.service;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

import androidx.preference.PreferenceManager;


public class GPSTracker implements LocationListener {
    Context context;
    Location lastLocation;
    double longitude;
    double latitude;
    @SuppressLint("MissingPermission")
    public GPSTracker(Context c)  {
        context = c;
        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        locationManager.requestLocationUpdates( LocationManager.GPS_PROVIDER,
                1000,
                10, this);
    }

    public Location getLastLocation() {
        return lastLocation;
    }

    @Override
    public void onLocationChanged(Location location) {
        lastLocation = location;
        saveLocation();
    }

    public double getLongitude() {
        return longitude;
    }

    public void saveLocation() {
        if (this.latitude!=0 && this.longitude!=0) {
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putFloat("current_long", (float)  this.latitude);
            editor.putFloat("current_lat", (float) this.longitude);
            editor.commit();
        }
    }

    public double getLatitude() {
        return latitude;
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }
}