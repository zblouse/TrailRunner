package com.example.trailrunner;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;

public class TrailManagerFragment extends Fragment {

    private final TrailDatabaseHelper trailDatabaseHelper;
    private final SharedPreferences sharedPreferences;
    private final Trail trail;

    public TrailManagerFragment(TrailDatabaseHelper trailDatabaseHelper, SharedPreferences sharedPreferences, Trail trail){
        super(R.layout.fragment_trail_manager);
        this.trailDatabaseHelper = trailDatabaseHelper;
        this.sharedPreferences = sharedPreferences;
        this.trail = trail;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //Show main activities bottom navigation
        ((MainActivity)getActivity()).showNavigation();
        LinearLayout layout = (LinearLayout) inflater.inflate(R.layout.fragment_trail_manager,container,false);

        TextView trailDataTextView = layout.findViewById(R.id.trail_data);
        trailDataTextView.setText("Trail Name: " + trail.getTrailName() + "\nTrail Distance: " +
                trail.getTrailDistance() + " " + trail.getTrailDistanceUnit() + "\nUser Progress: " +
                trail.getUserTrailDistance() + " " + trail.getTrailDistanceUnit());
        Button setActiveTrailButton = layout.findViewById(R.id.set_active_trail_button);
        setActiveTrailButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString(getString(R.string.user_pref_active_trail_key), String.valueOf(trail.getId()));
                editor.apply();
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, new UserHomeFragment(trailDatabaseHelper, sharedPreferences)).commit();
            }
        });

        Button resetUserProgressButton = layout.findViewById(R.id.reset_progress_button);
        resetUserProgressButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                trail.setUserTrailDistance(0);
                trailDatabaseHelper.updateTrail(trail);
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, new TrailManagerFragment(trailDatabaseHelper, sharedPreferences, trail)).commit();
            }
        });

        Button editTrailButton = layout.findViewById(R.id.edit_trail_button);
        editTrailButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, new EditTrailFragment(trailDatabaseHelper, sharedPreferences, trail)).commit();
            }
        });

        Button deleteTrailButton = layout.findViewById(R.id.delete_trail_button);
        deleteTrailButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                trailDatabaseHelper.deleteTrail(trail);
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, new ViewTrailsFragment(trailDatabaseHelper, sharedPreferences)).commit();
            }
        });
        return layout;
    }
}
