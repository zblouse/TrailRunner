package com.example.trailrunner;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.provider.Settings;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class LocationUtils {

    public static final int PERMISSIONS_REQUEST_CODE = 40;
    private FusedLocationProviderClient fusedLocationClient;
    private Activity activity;
    private Location lastKnownLocation;
    private List<LocationListener> locationListeners;

    public LocationUtils(Activity activity){
        this.activity = activity;
        this.locationListeners = new ArrayList<>();
        this.fusedLocationClient = LocationServices.getFusedLocationProviderClient(activity);
        requestNewLocationData();
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                scheduledLocationUpdateCheck();
            }
        },1000,30000);
    }

    public Location getLastKnownLocation(){
        System.out.println("Getting last known location");
        requestNewLocationData();
        return lastKnownLocation;
    }

    public void subscribeToLocationUpdates(LocationListener locationListener){
        locationListeners.add(locationListener);
    }

    public void unsubscribeToLocationUpdates(LocationListener locationListener){
        locationListeners.remove(locationListener);
    }

    private void scheduledLocationUpdateCheck(){
        if(!locationListeners.isEmpty()){
            requestNewLocationData();
        }
    }

    private boolean handleLocationPermissions(){
        boolean coarseLocationGranted = ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED;
        boolean fineLocationGranted = ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
        boolean backgroundLocationGranted = ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_BACKGROUND_LOCATION) == PackageManager.PERMISSION_GRANTED;

        if(!coarseLocationGranted){
            System.out.println("Does not have coarse location permission");
            ActivityCompat.requestPermissions(activity, new String[]{
                    Manifest.permission.ACCESS_COARSE_LOCATION}, PERMISSIONS_REQUEST_CODE);
        }
        if(!fineLocationGranted){
            System.out.println("Does not have fine location permission");
            ActivityCompat.requestPermissions(activity, new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSIONS_REQUEST_CODE);
        }
        if(!backgroundLocationGranted){
            System.out.println("Does not have background location permission");
            ActivityCompat.requestPermissions(activity, new String[]{
                    Manifest.permission.ACCESS_BACKGROUND_LOCATION}, PERMISSIONS_REQUEST_CODE);
        }
        return coarseLocationGranted && fineLocationGranted && backgroundLocationGranted;
    }

    private boolean isLocationEnabled() {
        LocationManager locationManager = (LocationManager) activity.getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

    @SuppressLint("MissingPermission")
    private void requestNewLocationData() {
        System.out.println("Requesting new location data");
        if(!isLocationEnabled()){
            Toast toast = Toast.makeText(activity.getApplicationContext(), "Please enable location", Toast.LENGTH_SHORT);
            toast.show();
            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            activity.startActivity(intent);
        }
        if(handleLocationPermissions()) {
            System.out.println("Has permissions");
            fusedLocationClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, null).addOnSuccessListener(activity, new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    // Got last known location. In some rare situations this can be null.
                    System.out.println("Inside onSuccess");
                    if (location != null) {
                        lastKnownLocation = location;
                        sendLocationUpdates(location);
                    }
                }
            });;
        }
    }

    private void sendLocationUpdates(Location location){
        for(LocationListener listener: locationListeners){
            listener.locationUpdate(location);
        }
    }
}
