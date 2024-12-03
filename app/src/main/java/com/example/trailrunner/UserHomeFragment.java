package com.example.trailrunner;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;

/**
 * Fragment for displaying the user home. This screen consists of information about the user's current
 * active trail and contains buttons for navigating to the other fragments
 */
public class UserHomeFragment extends Fragment {

    private TrailDatabaseHelper trailDatabaseHelper;
    private SharedPreferences sharedPreferences;

    public UserHomeFragment(){
        super(R.layout.fragment_user_home);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //Show main activities bottom navigation
        ((MainActivity)getActivity()).showNavigation();
        trailDatabaseHelper = ((MainActivity)getActivity()).getTrailDatabaseHelper();
        sharedPreferences = ((MainActivity)getActivity()).getSharedPreferences();

        LinearLayout layout = (LinearLayout) inflater.inflate(R.layout.fragment_user_home,container,false);

        TextView currentTrailTextView = layout.findViewById(R.id.currentProgress);
        String currentTrailId = sharedPreferences.getString(getString(R.string.user_pref_active_trail_key), null);
        if(currentTrailId == null){
            currentTrailTextView.setText("No active trail. Trail progress is not being saved.");
        } else {
            Trail activeTrail = trailDatabaseHelper.getTrailById(currentTrailId);
            if(activeTrail != null){
                currentTrailTextView.setText("Current Trail: " + activeTrail.getTrailName() +
                        "\nProgress: " + String.format("%.2f",activeTrail.getUserTrailDistance()) + "\nPercent Complete: " +
                        String.format("%.2f",activeTrail.getUserTrailDistance()/activeTrail.getTrailDistance()*100) + "%");
            } else {
                currentTrailTextView.setText("Error with active trail. Trail progress is not being saved.");
            }
        }

        Button selectTrailButton = layout.findViewById(R.id.select_trail_button);
        selectTrailButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, new ViewTrailsFragment()).commit();
            }
        });

        Button logoutButton = layout.findViewById(R.id.logout_button);
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, new LoginFragment()).commit();
            }
        });

        Button workoutButton = layout.findViewById(R.id.start_workout_button);
        workoutButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, new WorkoutFragment()).commit();
            }
        });

        Button manualWorkoutButton = layout.findViewById(R.id.manual_workout_button);
        manualWorkoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, new ManualWorkoutEntryFragment()).commit();
            }
        });

        return layout;
    }
}
