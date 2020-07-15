package com.example.weatheralarm.alarm.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.speech.tts.TextToSpeech;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.preference.PreferenceManager;

import com.example.weatheralarm.alarm.model.Alarm;
import com.google.android.gms.location.FusedLocationProviderClient;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Locale;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class AlarmPlayingService extends Service {
    private TextToSpeech t1;
    private String API = "3c08d03f1d845c4e49e78b4da02b5668";
    private double lat =  44.439663;
    private double lon = 26.096306;
    private Context mcontext;
    private Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
    private static Ringtone mRingtone  = null;
    private boolean ringing = true;


    public AlarmPlayingService(Context context, Alarm alarm) {
        mRingtone = RingtoneManager.getRingtone(context, notification);
        mcontext = context;
        t1 = new TextToSpeech(context.getApplicationContext(), status -> {
            if(status != TextToSpeech.ERROR) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    if (alarm.isWeatherVoice()) {
                        t1.setLanguage(Locale.UK);
                        new AlarmPlayingService.weatherTask().execute();
                    }
                    else {
                        if (ringing) {
                            mRingtone.play();
                            final Handler handler = new Handler();
                            handler.postDelayed(() -> {
                                if (mRingtone.isPlaying())
                                    mRingtone.stop();
                            }, 60000);
                        }
                    }
                }
            }
        });
    }

    public void stopRingtone() {
        t1.stop();
        ringing = false;
        mRingtone.stop();
    }

    class weatherTask extends AsyncTask<String, Void, String> {
        private FusedLocationProviderClient fusedLocationClient;

        OkHttpClient client = new OkHttpClient();

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        protected String doInBackground(String... args) {
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mcontext);
            float myLatitude = prefs.getFloat("current_lat", (float) lat);
            float myLongitude = prefs.getFloat("current_long", (float) lon);
            String requestURL = "https://api.openweathermap.org/data/2.5/weather?lat=" + myLatitude + "&lon=" + myLongitude + "&units=metric&appid=" + API;
            Request request = new Request.Builder()
                    .url(requestURL)
                    .build();
            System.out.println(request);
            try {
                Response response = client.newCall(request).execute();
                return response.body().string();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            try {
                JSONObject jsonObj = new JSONObject(result);
                JSONObject main = jsonObj.getJSONObject("main");
                JSONObject wind = jsonObj.getJSONObject("wind");
                JSONObject weather = jsonObj.getJSONArray("weather").getJSONObject(0);

                String humidity = main.getString("humidity");
                String windSpeed = wind.getString("speed");
                String weatherDescription = weather.getString("description");

                String toSpeak = "Currently " + weatherDescription + "." + " In " + jsonObj.getString("name")
                        + " currently are "+ main.getString("temp_max") + " celsius degrees. " + "Humidity level is "
                        + humidity + " procent." + " Wind power is " + windSpeed + " kilometers per hour.";

                System.out.println(toSpeak);
                t1.speak(toSpeak, TextToSpeech.QUEUE_FLUSH, null, null);
                new Handler().postDelayed(
                        () -> {
                            if (ringing) {
                                Log.i("Ringtone", "This'll run 15000 milliseconds later");
                                mRingtone.play();
                            }
                        },
                        15000);
                final Handler handler = new Handler();
                handler.postDelayed(() -> {
                    if (mRingtone.isPlaying())
                        mRingtone.stop();
                }, 45000);

            } catch (JSONException e) {
                mRingtone.play();
                final Handler handler = new Handler();
                handler.postDelayed(() -> {
                    if (mRingtone.isPlaying())
                        mRingtone.stop();
                }, 60000);
            }
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
