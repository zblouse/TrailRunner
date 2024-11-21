package com.example.trailrunner;

import android.content.Intent;
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

public class UserHomeFragment extends Fragment {

    private final TrailDatabaseHelper trailDatabaseHelper;
    private final SharedPreferences sharedPreferences;

    public UserHomeFragment(TrailDatabaseHelper trailDatabaseHelper, SharedPreferences sharedPreferences){
        super(R.layout.fragment_user_home);
        this.trailDatabaseHelper = trailDatabaseHelper;
        this.sharedPreferences = sharedPreferences;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //Show main activities bottom navigation
        ((MainActivity)getActivity()).showNavigation();

        LinearLayout layout = (LinearLayout) inflater.inflate(R.layout.fragment_user_home,container,false);

        TextView currentTrailTextView = layout.findViewById(R.id.currentProgress);
        String currentTrailId = sharedPreferences.getString(getString(R.string.user_pref_active_trail_key), null);
        if(currentTrailId == null){
            currentTrailTextView.setText("No active trail. Trail progress is not being saved.");
        } else {
            Trail activeTrail = trailDatabaseHelper.getTrailById(currentTrailId);
            if(activeTrail != null){
                currentTrailTextView.setText("Current Trail: " + activeTrail.getTrailName() +
                        "\nProgress: " + activeTrail.getUserTrailDistance() + "\nPercent Complete: " +
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
                        .replace(R.id.fragment_container, new ViewTrailsFragment(trailDatabaseHelper, sharedPreferences)).commit();
            }
        });

        Button logoutButton = layout.findViewById(R.id.logout_button);
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, new LoginFragment(trailDatabaseHelper, sharedPreferences)).commit();
            }
        });

        return layout;
    }
}
