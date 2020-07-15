package com.example.weatheralarm.alarm.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.PowerManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceManager;

import com.example.weatheralarm.R;
import com.example.weatheralarm.alarm.service.AlarmReceiver;
import com.example.weatheralarm.alarm.service.GPSTracker;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public final class RingingPageFragment extends Fragment {
    private PowerManager pm;
    private View v;
    TextView addressTxt, updated_atTxt, statusTxt, tempTxt, temp_minTxt, temp_maxTxt, sunriseTxt,
            sunsetTxt, windTxt, pressureTxt, humidityTxt;
    private GPSTracker tracker;
    double lat =  44.439663;
    double lon = 26.096306;
    String API = "3c08d03f1d845c4e49e78b4da02b5668";
    /***
     * I createc the alarm landing page if an alarm has started.
     * If dismiss button is clicked dismiss alarm;
     */
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_ringing_page, container, false);
//        AlarmReceiver.playingService.stopRingtone();
        final Button dismiss = (Button) v.findViewById(R.id.dismiss_button);
        dismiss.setOnClickListener(v1 -> {
            startActivity(new Intent(getContext(), MainActivity.class));
            AlarmReceiver.playingService.stopRingtone();
            getActivity().finish();
        });
        pm = (PowerManager) getActivity().getSystemService(Context.POWER_SERVICE);
        tracker = new GPSTracker(getContext());

        addressTxt = v.findViewById(R.id.address);
        updated_atTxt = v.findViewById(R.id.updated_at);
        statusTxt = v.findViewById(R.id.status);
        tempTxt = v.findViewById(R.id.temp);
        temp_minTxt = v.findViewById(R.id.temp_min);
        temp_maxTxt = v.findViewById(R.id.temp_max);
        sunriseTxt = v.findViewById(R.id.sunrise);
        sunsetTxt = v.findViewById(R.id.sunset);
        windTxt = v.findViewById(R.id.wind);
        pressureTxt = v.findViewById(R.id.pressure);
        humidityTxt = v.findViewById(R.id.humidity);

        new weatherTask().execute();
        return v;
    }

    class weatherTask extends AsyncTask<String, Void, String> {

        OkHttpClient client = new OkHttpClient();

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            v.findViewById(R.id.loader).setVisibility(View.VISIBLE);
            v.findViewById(R.id.mainContainer).setVisibility(View.GONE);
            v.findViewById(R.id.errorText).setVisibility(View.GONE);
        }

        protected String doInBackground(String... args) {
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getContext());
            float myLatitude = prefs.getFloat("current_lat", (float) lat);
            float myLongitude = prefs.getFloat("current_long", (float) lon);
            System.out.println(prefs);
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
                JSONObject sys = jsonObj.getJSONObject("sys");
                JSONObject wind = jsonObj.getJSONObject("wind");
                JSONObject weather = jsonObj.getJSONArray("weather").getJSONObject(0);

                Long updatedAt = jsonObj.getLong("dt");
                String updatedAtText = "Updated at: " + new SimpleDateFormat("dd/MM/yyyy hh:mm a", Locale.ENGLISH).format(new Date(updatedAt * 1000));
                String temp = main.getString("temp") + "°C";
                String tempMin = "Min. Temp: " + main.getString("temp_min") + "°C";
                String tempMax = "Max. Temp: " + main.getString("temp_max") + "°C";
                String pressure = main.getString("pressure");
                String humidity = main.getString("humidity");

                Long sunrise = sys.getLong("sunrise");
                Long sunset = sys.getLong("sunset");
                String windSpeed = wind.getString("speed");
                String weatherDescription = weather.getString("description");

                String address = jsonObj.getString("name") + ", " + sys.getString("country");

                addressTxt.setText(address);
                updated_atTxt.setText(updatedAtText);
                statusTxt.setText(weatherDescription.toUpperCase());
                tempTxt.setText(temp);
                temp_minTxt.setText(tempMin);
                temp_maxTxt.setText(tempMax);
                sunriseTxt.setText(new SimpleDateFormat("hh:mm a", Locale.ENGLISH).format(new Date(sunrise * 1000)));
                sunsetTxt.setText(new SimpleDateFormat("hh:mm a", Locale.ENGLISH).format(new Date(sunset * 1000)));
                windTxt.setText(windSpeed);
                pressureTxt.setText(pressure);
                humidityTxt.setText(humidity);

                v.findViewById(R.id.loader).setVisibility(View.GONE);
                v.findViewById(R.id.mainContainer).setVisibility(View.VISIBLE);

            } catch (JSONException e) {
                v.findViewById(R.id.loader).setVisibility(View.GONE);
                v.findViewById(R.id.errorText).setVisibility(View.VISIBLE);
            }

        }
    }
}