package com.example.weatheralarm.alarm.activities;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.weatheralarm.R;
import com.example.weatheralarm.alarm.util.AlarmUtils;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.preference.PreferenceManager;

import static com.example.weatheralarm.alarm.activities.PersonalizeAlarmActivity.ADD_ALARM;
import static com.example.weatheralarm.alarm.activities.PersonalizeAlarmActivity.buildAddEditAlarmActivityIntent;

public class MainActivity extends AppCompatActivity {
    private static final int MY_PERMISSIONS_REQUEST_CODE = 123;
    private Context mContext;
    private MainActivity mActivity;
    private FusedLocationProviderClient fusedLocationClient;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm);

        mContext = getApplicationContext();
        mActivity = MainActivity.this;

        getSupportActionBar().setTitle("Weathery Alarm");

        checkPermission();

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        if (location != null) {
                            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mContext);
                            SharedPreferences.Editor editor = prefs.edit();
                            editor.putFloat("current_long", (float) location.getLongitude());
                            editor.putFloat("current_lat", (float) location.getLatitude());
                            editor.commit();
                        }
                    }
                })
                .addOnFailureListener(this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        System.out.println("Location: " + e.getMessage());
                    }
                });
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.weather_menu, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        System.out.println(id);

        if(id == R.id.action_settings){
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
        }
        else if(id == R.id.action_set_alarm){
            AlarmUtils.checkAlarmPermissions(this);
            final Intent i = buildAddEditAlarmActivityIntent(this, ADD_ALARM);
            startActivity(i);
        }
        else if(id == R.id.action_weather){
            Intent intent = new Intent(this, WeatherActivity.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    protected void checkPermission(){
        if(ContextCompat.checkSelfPermission(mActivity, Manifest.permission.ACCESS_FINE_LOCATION)
                + ContextCompat.checkSelfPermission(
                mActivity,Manifest.permission.ACCESS_COARSE_LOCATION)
                + ContextCompat.checkSelfPermission(
                mActivity,Manifest.permission.INTERNET)
                != PackageManager.PERMISSION_GRANTED){

            // Do something, when permissions not granted
            if(ActivityCompat.shouldShowRequestPermissionRationale(
                    mActivity,Manifest.permission.ACCESS_FINE_LOCATION)
                    || ActivityCompat.shouldShowRequestPermissionRationale(
                    mActivity,Manifest.permission.ACCESS_COARSE_LOCATION)
                    || ActivityCompat.shouldShowRequestPermissionRationale(
                    mActivity,Manifest.permission.INTERNET)){
                // If we should give explanation of requested permissions

                // Show an alert dialog here with request explanation
                AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
                builder.setMessage("Location, Internet settings " +
                        "permissions are required to do the task.");
                builder.setTitle("Please grant those permissions");
                builder.setPositiveButton("Sure", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        ActivityCompat.requestPermissions(
                                mActivity,
                                new String[]{
                                        Manifest.permission.ACCESS_COARSE_LOCATION,
                                        Manifest.permission.ACCESS_FINE_LOCATION,
                                        Manifest.permission.INTERNET,
                                        Manifest.permission.MODIFY_AUDIO_SETTINGS,
                                        Manifest.permission.RECEIVE_BOOT_COMPLETED
                                },
                                MY_PERMISSIONS_REQUEST_CODE
                        );
                    }
                });
                builder.setNeutralButton("Cancel",null);
                AlertDialog dialog = builder.create();
                dialog.show();
            }else{
                // Directly request for required permissions, without explanation
                ActivityCompat.requestPermissions(
                        mActivity,
                        new String[]{
                                Manifest.permission.ACCESS_COARSE_LOCATION,
                                Manifest.permission.ACCESS_FINE_LOCATION,
                                Manifest.permission.INTERNET,
                                Manifest.permission.MODIFY_AUDIO_SETTINGS,
                                Manifest.permission.RECEIVE_BOOT_COMPLETED
                        },
                        MY_PERMISSIONS_REQUEST_CODE
                );
            }
        } else {
            System.out.println("Permissions been granted successfully before.");
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults){
        switch (requestCode){
            case MY_PERMISSIONS_REQUEST_CODE:{
                if((grantResults.length >0) && (grantResults[0] + grantResults[1] + grantResults[2]  == PackageManager.PERMISSION_GRANTED)) {
                    Toast.makeText(mContext,"Permissions granted.",Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(mContext,"Permissions denied.", Toast.LENGTH_SHORT).show();
                }
                break;
            }
            default:
                break;
        }
    }
}
