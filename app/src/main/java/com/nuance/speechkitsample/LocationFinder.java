package com.nuance.speechkitsample;

import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.util.Log;

public class LocationFinder implements LocationListener {

    @Override
    public void onLocationChanged(Location location) {
        location.getLatitude();
        location.getLongitude();

        String myLocation = "Latitude = " + location.getLatitude() + " Longitude = " + location.getLongitude();

        Log.d("smh", "Current Location: " + myLocation);
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {
        Log.d("smh", "Current Location: ");
    }

    @Override
    public void onProviderEnabled(String s) {

        Log.d("smh", "Current Location: ");
    }

    @Override
    public void onProviderDisabled(String s) {

        Log.d("smh", "Current Location: ");
    }
}
