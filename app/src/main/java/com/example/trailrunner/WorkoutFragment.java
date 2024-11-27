package com.example.trailrunner;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.core.app.NotificationCompat;
import androidx.fragment.app.Fragment;

import com.google.android.gms.maps.model.LatLng;

import java.text.DecimalFormat;
import java.util.Locale;

public class WorkoutFragment extends Fragment implements LocationListener {

    private LinearLayout layout;
    private SharedPreferences sharedPreferences;
    private TrailDatabaseHelper trailDatabaseHelper;
    private MainActivity mainActivity;
    private TextView timerTextView;
    private TextView distanceTextView;
    private Button startButton;
    private Button pauseButton;
    private Button stopButton;
    private Button saveButton;
    private Button discardButton;
    private boolean paused;
    private Location lastLocation;
    private int seconds;
    private double distance;

    public WorkoutFragment(){
        super(R.layout.fragment_workout);
        paused = false;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //Hide main activities bottom navigation during run, to prevent accidental clicking
        mainActivity = ((MainActivity) getActivity());
        mainActivity.showNavigation();
        sharedPreferences = mainActivity.getSharedPreferences();
        trailDatabaseHelper = mainActivity.getTrailDatabaseHelper();
        layout = (LinearLayout) inflater.inflate(R.layout.fragment_workout,container,false);
        timerTextView = layout.findViewById(R.id.time_display);
        distanceTextView = layout.findViewById(R.id.distance_display);

        startButton = layout.findViewById(R.id.start_run_button);
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                seconds = 0;
                startWorkout();
            }
        });

        pauseButton = layout.findViewById(R.id.pause_run_button);
        pauseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pauseWorkout();
            }
        });

        stopButton = layout.findViewById(R.id.finish_run_button);
        stopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopWorkout();
            }
        });

        saveButton = layout.findViewById(R.id.save_run_button);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveWorkout();
            }
        });

        discardButton = layout.findViewById(R.id.discard_run_button);
        discardButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                discardWorkout();
            }
        });

        ((ViewGroup)layout).removeView(pauseButton);
        ((ViewGroup)layout).removeView(stopButton);
        ((ViewGroup)layout).removeView(discardButton);
        ((ViewGroup)layout).removeView(saveButton);

        return layout;
    }

    private void startWorkout(){
        ((ViewGroup)layout).addView(pauseButton);
        ((ViewGroup)layout).addView(stopButton);
        ((ViewGroup)layout).removeView(startButton);

        mainActivity.hideNavigation();
        mainActivity.getLocationUtils().subscribeToLocationUpdates(this);
        Handler handler = new Handler(Looper.myLooper());
        DecimalFormat distanceFormat = new DecimalFormat("#.00");
        handler.post(new Runnable() {
            @Override
            public void run() {
                if(!paused) {
                    int hours = seconds / 3600;
                    int minutes = (seconds % 3600) / 60;
                    int formattedSeconds = seconds % 60;
                    String formattedTime;
                    if (hours > 0) {
                        formattedTime = String.format(Locale.getDefault(), "%d:%02d:%02d", hours, minutes, formattedSeconds);
                    } else {
                        formattedTime = String.format(Locale.getDefault(), "%02d:%02d", minutes, formattedSeconds);
                    }
                    timerTextView.setText(formattedTime);
                    if (!paused) {
                        seconds++;
                    }
                    if (sharedPreferences.getString(getString(R.string.user_pref_unit_key), "Metric").equals("Imperial")) {
                        distanceTextView.setText(distanceFormat.format(distance) + " Miles");
                    } else {
                        distanceTextView.setText(distanceFormat.format(distance) + " Km");
                    }

                    handler.postDelayed(this, 1000);
                }
            }
        });

    }

    private void pauseWorkout(){
        paused = !paused;
        if(paused){
            pauseButton.setText("Unpause Workout");
            lastLocation = null;
        } else {
            pauseButton.setText("Pause Workout");
        }

    }

    private void stopWorkout(){
        paused = true;
        AlertDialog.Builder confirmDoneBuilder = new AlertDialog.Builder(mainActivity);
        confirmDoneBuilder.setMessage("Finish workout?");
        confirmDoneBuilder.setCancelable(false);
        confirmDoneBuilder.setPositiveButton("Finish", (DialogInterface.OnClickListener)(dialog, which) -> {
            ((ViewGroup)layout).addView(discardButton);
            ((ViewGroup)layout).addView(saveButton);
            ((ViewGroup)layout).removeView(pauseButton);
            ((ViewGroup)layout).removeView(stopButton);
            mainActivity.getLocationUtils().unsubscribeToLocationUpdates(this);
        });
        confirmDoneBuilder.setNegativeButton("Resume",(DialogInterface.OnClickListener)(dialog, which) -> {
            pauseWorkout();
            startWorkout();
            dialog.cancel();
        });

        AlertDialog alertDialog = confirmDoneBuilder.create();
        alertDialog.show();
    }

    private void saveWorkout(){
        System.out.println("Inside save workout");
        String currentTrailId = sharedPreferences.getString(getString(R.string.user_pref_active_trail_key), null);
        if(currentTrailId == null){
            Toast toast = Toast.makeText(mainActivity,"No active trail. Cannot save progress", Toast.LENGTH_SHORT);
            toast.show();
        } else {
            System.out.println("Current Trail ID: " + currentTrailId);
            Trail activeTrail = trailDatabaseHelper.getTrailById(currentTrailId);
            if(activeTrail != null){
                if(sharedPreferences.getString(getString(R.string.user_pref_unit_key), "Metric").equals("Imperial")) {
                    if(activeTrail.getTrailDistanceUnit().equals("Miles")){
                        activeTrail.setUserTrailDistance(activeTrail.getUserTrailDistance() + distance);
                    } else {
                        activeTrail.setUserTrailDistance(activeTrail.getUserTrailDistance() + LatLongUtils.convertMilesToKm(distance));
                    }
                } else {
                    if(activeTrail.getTrailDistanceUnit().equals("Kilometers")){
                        activeTrail.setUserTrailDistance(activeTrail.getUserTrailDistance() + distance);
                    } else {
                        activeTrail.setUserTrailDistance(activeTrail.getUserTrailDistance() + LatLongUtils.convertKmToMiles(distance));
                    }
                }
                if(activeTrail.getUserTrailDistance() >= activeTrail.getTrailDistance()){
                    mainActivity.sendNotification("Finished Trail", "Congratulations! You have completed the " + activeTrail.getTrailName(), R.drawable.run);
                }

                System.out.println("Trail New distance: " + activeTrail.getUserTrailDistance());
                trailDatabaseHelper.updateTrail(activeTrail);

            } else {
                Toast toast = Toast.makeText(mainActivity,"Failed to load trail. Cannot save progress", Toast.LENGTH_SHORT);
                toast.show();
            }
        }
        mainActivity.getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, new UserHomeFragment()).commit();
    }

    private void discardWorkout(){
        mainActivity.getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, new UserHomeFragment()).commit();
    }

    @Override
    public void locationUpdate(Location location) {
        System.out.println("Got location update: " + location);
        if(location == null){
            return;
        }
        if(lastLocation == null){
            lastLocation = location;
        } else if (lastLocation != location){
            LatLng previousLatLng = new LatLng(lastLocation.getLatitude(), lastLocation.getLongitude());
            LatLng currentLatLng = new LatLng(location.getLatitude(), location.getLongitude());
            double distanceKm = LatLongUtils.calculateDistanceKm(previousLatLng, currentLatLng);
            if(sharedPreferences.getString(getString(R.string.user_pref_unit_key), "Metric").equals("Imperial")) {
                distance += LatLongUtils.convertKmToMiles(distanceKm);
            } else {
                distance+=distanceKm;
            }
            lastLocation = location;
        }
    }
}
