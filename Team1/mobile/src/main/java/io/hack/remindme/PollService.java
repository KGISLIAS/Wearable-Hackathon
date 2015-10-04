package io.hack.remindme;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.IBinder;
import android.os.SystemClock;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;
import android.widget.Toast;

import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import io.hack.remindme.database.DatabaseHandler;
import io.hack.remindme.database.Todo;

/**
 * Created by iQube_2 on 10/2/2015.
 */
public class PollService extends Service implements LocationListener{

    //GPS VARS

    boolean isGPSEnabled = false;

    // flag for network status
    boolean isNetworkEnabled = false;

    // The minimum distance to change Updates in meters
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 10; // 10 meters

    // The minimum time between updates in milliseconds
    private static final long MIN_TIME_BW_UPDATES = 1000 * 60 * 1; // 1 minute

    Location location; // location
    double latitude; // latitude
    double longitude; // longitude





    private String mPulseUrl;
    private AlarmManager alarms;
    private PendingIntent alarmIntent;
    private ConnectivityManager cnnxManager;
    LocationManager locationManager;
    boolean received=false;
    @Override
    public void onCreate() {
        super.onCreate();
        cnnxManager = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        alarms = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intentOnAlarm = new Intent(
                LaunchReceiver.ACTION_PULSE_SERVER_ALARM);
        alarmIntent = PendingIntent.getBroadcast(this, 0, intentOnAlarm, 0);
    }


    @Override
    public void onStart(Intent intent, int startId) {

    gpsLocation();
    }
    public void gpsLocation()
    {

        locationManager = (LocationManager)getSystemService(LOCATION_SERVICE);
        isGPSEnabled = locationManager
                .isProviderEnabled(LocationManager.GPS_PROVIDER);

        // getting network status
        isNetworkEnabled = locationManager
                .isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        try {
            if (isNetworkEnabled) {
                locationManager.requestLocationUpdates(
                        LocationManager.NETWORK_PROVIDER, MIN_TIME_BW_UPDATES,
                        MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                Log.d("Network", "Network");
                if (locationManager != null) {
                    location = locationManager
                            .getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                    if (location != null) {
                        latitude = location.getLatitude();
                        longitude = location.getLongitude();
                    }
                }
            }
            // if GPS Enabled get lat/long using GPS Services
            if (isGPSEnabled) {
                if (location == null) {
                    locationManager.requestLocationUpdates(
                            LocationManager.GPS_PROVIDER, MIN_TIME_BW_UPDATES,
                            MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                    Log.d("GPS Enabled", "GPS Enabled");
                    if (locationManager != null) {
                        location = locationManager
                                .getLastKnownLocation(LocationManager.GPS_PROVIDER);
                        if (location != null) {
                            latitude = location.getLatitude();
                            longitude = location.getLongitude();
                        }
                    }
                }
            }
        }catch (Exception e)
        {

        }
        }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        gpsLocation();
        return START_NOT_STICKY;

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
       try {
           locationManager.removeUpdates(this);

       }
       catch (Exception e)
       {


       }
    }

    public void setAlarm()
    {

        int interval=20;
        /*if (settings == null
                || settings.getPulseIntervalSeconds() == -1) {
            interval = Integer
                    .parseInt(getString(R.string.pulseIntervalSeconds));
        } else {
            interval = settings.getPulseIntervalSeconds();
        }
        */

        long timeToAlarm = SystemClock.elapsedRealtime() + interval
                * 1000;
        alarms.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, timeToAlarm,
                alarmIntent);
    }

    public void check(double lat,double longi)
    {
/*
        Toast.makeText(this,lat+longi+"",Toast.LENGTH_LONG).show();
        OkHttpClient client = new OkHttpClient();
        try {

            Request request = new Request.Builder()
                    .url("https://maps.googleapis.com/maps/api/place/nearbysearch/json?location="+lat+","+longi+"&radius=500&key=AIzaSyC0HAwwsnZMQ-Q_j9FOrHcJmphyLqf1ukM")
                    .build();

            Response response = client.newCall(request).execute();
            String json=response.body().string();
            JSONObject obj=new JSONObject(response.toString());
            setAlarm();
            locationManager.removeUpdates(this);
            received=false;
            stopSelf();

         }
        catch (IOException e)
        {
         e.printStackTrace();
         Toast.makeText(this,"error",Toast.LENGTH_LONG).show();
        }
        catch (Exception ee)
        {
            ee.printStackTrace();
        }
        */

    Ion.with(this).load("https://maps.googleapis.com/maps/api/place/nearbysearch/json?location="+lat+","+longi+"&radius=500&key=AIzaSyC0HAwwsnZMQ-Q_j9FOrHcJmphyLqf1ukM").asString().setCallback(
            new FutureCallback<String>() {
                @Override
                public void onCompleted(Exception e, String result) {

                    String res = result;
                    ArrayList<Todo> todos = new ArrayList<Todo>();
                    DatabaseHandler db = new DatabaseHandler(PollService.this);

                    todos = (ArrayList) db.getAllTodos();
                    try {
                        JSONObject obj = new JSONObject(result);
                        JSONArray places = obj.getJSONArray("results");
                        for (int i = 0; i < places.length(); i++) {
                            String name = places.getJSONObject(i).getString("name");

                            ArrayList<String> finalPlaces = new ArrayList();
                            if (checkTodos(name, todos)) {
                                finalPlaces.add(name);
                                createNot("Finish it",name+" is near");
                            }

                        }


                    } catch (Exception eee) {
                        eee.printStackTrace();
                    }

                    setAlarm();
                    try {

                        locationManager.removeUpdates(PollService.this);

                    } catch (Exception ee) {
                    }
                    received = false;
                    stopSelf();

                }
            }
    );

    }

    public void createNot(String title,String description)
    {
        int notificationId = 001;
// Build intent for notification content
        final  String GROUP_KEY_LOCS = "group_key_locs";

        Intent viewIntent = new Intent(this, ActivityRemindMe.class);

        PendingIntent viewPendingIntent =
                PendingIntent.getActivity(this, 0, viewIntent, 0);

        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this)
                        .setContentTitle(title)
                        .setSmallIcon(R.drawable.ic_stat_kurippu)
                        .setContentText(description)
                        .setContentIntent(viewPendingIntent);

// Get an instance of the NotificationManager service
        NotificationManagerCompat notificationManager =
                NotificationManagerCompat.from(this);

// Build the notification and issues it with notification manager.
        notificationManager.notify(notificationId, notificationBuilder.build());
    }


    public boolean checkTodos(String  name,ArrayList<Todo> todos) {
        String[] splited = name.toLowerCase().split("\\s+");
        for(int j=0;j<todos.size();j++)
        {
            for(String single:splited){
                if(todos.get(j).getContent().toLowerCase().contains(single))
                {
                    return true;
                }
            }
        }
return false;
    }

    @Override
    public void onLocationChanged(Location location) {
        double latitude=(float) location.getLatitude();
        double  longitude=location.getLongitude();
        if(latitude!=0&&longitude!=0&&!received) {
            received=true;
            check(latitude, longitude);
        }

    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}