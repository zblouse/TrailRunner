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
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.google.android.gms.maps.model.LatLng;

import java.text.DecimalFormat;
import java.util.Locale;

/**
 * Fragment that times and calculates distance for the user's workout
 * Implements LocationListener to be able to subscribe to location updates
 */
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
        //When the startButton is clicked, measurements are reset and the workout is started
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                seconds = 0;
                distance = 0;
                startWorkout();
            }
        });

        pauseButton = layout.findViewById(R.id.pause_run_button);
        //When the pauseButton is clicked, the workout is paused
        pauseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pauseWorkout();
            }
        });

        stopButton = layout.findViewById(R.id.finish_run_button);
        //When the stop button is clicekd the workout is stopped
        stopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopWorkout();
            }
        });

        saveButton = layout.findViewById(R.id.save_run_button);
        //When the save button is clicked the workout distance is saved
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveWorkout();
            }
        });

        discardButton = layout.findViewById(R.id.discard_run_button);
        //When the discard button is clicked, the workout is not saved
        discardButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                discardWorkout();
            }
        });

        //The pause, stop, discard, and save buttons are hidden when the fragment is initially loaded
        ((ViewGroup)layout).removeView(pauseButton);
        ((ViewGroup)layout).removeView(stopButton);
        ((ViewGroup)layout).removeView(discardButton);
        ((ViewGroup)layout).removeView(saveButton);

        return layout;
    }

    /**
     * method called to start the workout
     */
    private void startWorkout(){
        //The pause and stop buttons are re-added to the view and the start button is hidden
        ((ViewGroup)layout).addView(pauseButton);
        ((ViewGroup)layout).addView(stopButton);
        ((ViewGroup)layout).removeView(startButton);

        //Navigation is hidden during the workout to prevent the user from accidentally clicking it
        mainActivity.hideNavigation();
        //subscribe to get location updates during the workout to track distance
        mainActivity.getLocationUtils().subscribeToLocationUpdates(this);
        Handler handler = new Handler(Looper.myLooper());
        DecimalFormat distanceFormat = new DecimalFormat("#.00");
        //Runable is repeated every second
        handler.post(new Runnable() {
            @Override
            public void run() {
                //If the workout is paused, don't update the time or distance
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
                        //Increment seconds
                        seconds++;
                    }
                    //Formats displaying the distance, distance is updated using the locationUpdated method
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

    /**
     * Sets the last location to null to not accumulate distance while the workout is paused
     * also updates the pause button text
     */
    private void pauseWorkout(){
        paused = !paused;
        if(paused){
            pauseButton.setText("Unpause Workout");
            lastLocation = null;
        } else {
            pauseButton.setText("Pause Workout");
        }

    }

    /**
     * Method called when the user wants to finish their workout, it presents them with two options
     * save or discard. User has the option to resume the workout
     */
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

    /**
     * Adds the total distance from the workout to the currently active trail. Checks if the user has
     * completed the trail, and if so sends a notification
     */
    private void saveWorkout(){
        String currentTrailId = sharedPreferences.getString(getString(R.string.user_pref_active_trail_key), null);
        if(currentTrailId == null){
            //Lets the user know that no progress can be saved if they do not have an active trail
            Toast toast = Toast.makeText(mainActivity,"No active trail. Cannot save progress", Toast.LENGTH_SHORT);
            toast.show();
        } else {
            Trail activeTrail = trailDatabaseHelper.getTrailById(currentTrailId);
            if(activeTrail != null){
                //Checks the user's preferred units and converts the distance as needed
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
                //If the user has completed the trail, send a congratulations notification
                if(activeTrail.getUserTrailDistance() >= activeTrail.getTrailDistance()){
                    mainActivity.sendNotification("Finished Trail", "Congratulations! You have completed the " + activeTrail.getTrailName(), R.drawable.run);
                }

                trailDatabaseHelper.updateTrail(activeTrail);

            } else {
                //Error state where the active trail cannot be retrieved from the database.
                Toast toast = Toast.makeText(mainActivity,"Failed to load trail. Cannot save progress", Toast.LENGTH_SHORT);
                toast.show();
            }
        }
        //Launches the user home fragment
        mainActivity.getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, new UserHomeFragment()).commit();
    }

    /**
     * Discards the workout by not saving anything and launching the UserHomeFragment
     */
    private void discardWorkout(){
        mainActivity.getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, new UserHomeFragment()).commit();
    }

    /**
     * Method implemented from the LocationListener interface, called by LocationUtils when a new
     * location has been received
     * @param location
     */
    @Override
    public void locationUpdate(Location location) {
        //It's rare but possible for the location to be null
        if(location == null){
            return;
        }
        //If this is the first location for the workout, just set it to lastLocation, need two points
        //to calculate distance
        if(lastLocation == null){
            lastLocation = location;
        } else if (lastLocation != location){
            //Calculates the distance between the two points and adds it to the current distance
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
