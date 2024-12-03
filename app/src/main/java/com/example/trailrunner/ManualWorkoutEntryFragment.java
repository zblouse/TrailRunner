package com.example.trailrunner;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

/**
 * Fragment to allow the user to manually enter workout information
 */
public class ManualWorkoutEntryFragment extends Fragment {

    private LinearLayout layout;
    private MainActivity mainActivity;
    private SharedPreferences sharedPreferences;
    private TrailDatabaseHelper trailDatabaseHelper;

    public ManualWorkoutEntryFragment() {
        super(R.layout.fragment_manual_workout_entry);
    }

    //Method called when the fragment is created
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mainActivity = ((MainActivity) getActivity());
        //Show main activities bottom navigation
        mainActivity.showNavigation();
        sharedPreferences = mainActivity.getSharedPreferences();
        trailDatabaseHelper = mainActivity.getTrailDatabaseHelper();
        layout = (LinearLayout) inflater.inflate(R.layout.fragment_manual_workout_entry, container, false);

        //Get a reference to all UI elements
        EditText distanceEditText = layout.findViewById(R.id.trail_distance);
        //Check which units the user has selected, then display the right unit
        if(sharedPreferences.getString(getString(R.string.user_pref_unit_key), "Metric").equals("Imperial")) {
            distanceEditText.setHint("Workout Distance (Miles)");
        } else {
            distanceEditText.setHint("Workout Distance (Kilometers)");
        }

        //Clicking the saveWorkoutButton saves the entered workout in the database
        Button saveWorkoutButton = layout.findViewById(R.id.save_workout_button);
        saveWorkoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(distanceEditText.getText().toString().isEmpty()){
                        Toast toast = Toast.makeText(mainActivity,"Distance cannot be empty", Toast.LENGTH_SHORT);
                        toast.show();
                } else {
                    double distance = Double.valueOf(distanceEditText.getText().toString());
                    String currentTrailId = sharedPreferences.getString(getString(R.string.user_pref_active_trail_key), null);
                    if(currentTrailId == null){
                        Toast toast = Toast.makeText(mainActivity,"No active trail. Cannot save progress", Toast.LENGTH_SHORT);
                        toast.show();
                    } else {
                        Trail activeTrail = trailDatabaseHelper.getTrailById(currentTrailId);
                        //Updates the user's trail progress in the database
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

                            trailDatabaseHelper.updateTrail(activeTrail);
                            if(activeTrail.getUserTrailDistance() >= activeTrail.getTrailDistance()){
                                mainActivity.sendNotification("Finished Trail", "Congratulations! You have completed the " + activeTrail.getTrailName(), R.drawable.run);
                            }

                            mainActivity.getSupportFragmentManager().beginTransaction()
                                    .replace(R.id.fragment_container, new UserHomeFragment()).commit();

                        } else {
                            Toast toast = Toast.makeText(mainActivity,"Failed to load trail. Cannot save progress", Toast.LENGTH_SHORT);
                            toast.show();
                        }
                    }
                }
            }
        });

        return layout;
    }
}
