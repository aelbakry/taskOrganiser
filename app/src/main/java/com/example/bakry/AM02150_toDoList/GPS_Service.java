package com.example.bakry.AM02150_toDoList;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;

/**
 * Service that extracts the GPS coordinates of the device and sends it back as a broadcast for the
 * intended activities to access and update the location on map.
 */
public class GPS_Service extends Service {

    private LocationListener locationListener;
    private LocationManager locationManager;


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onCreate() {

        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {

                //Putting the data of the location into the intent to be sent later
                Intent i = new Intent("location_update");
                i.putExtra("lat", location.getLatitude());
                i.putExtra("lng", location.getLongitude());
                sendBroadcast(i);

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
        };

        //Setting the location manager object and requesting updates every 10 second
        locationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 0, locationListener);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if(locationManager != null){

            //disabling the service
            locationManager.removeUpdates(locationListener);
        }
    }
}
