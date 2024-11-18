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
import android.widget.EditText;
import android.widget.LinearLayout;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;

public class EditTrailFragment extends Fragment {

    private final TrailDatabaseHelper trailDatabaseHelper;
    private final SharedPreferences sharedPreferences;
    private final Trail trail;

    public EditTrailFragment(TrailDatabaseHelper trailDatabaseHelper, SharedPreferences sharedPreferences, Trail trail){
        super(R.layout.fragment_edit_trail);
        this.trailDatabaseHelper = trailDatabaseHelper;
        this.sharedPreferences = sharedPreferences;
        this.trail = trail;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        LinearLayout layout = (LinearLayout) inflater.inflate(R.layout.fragment_edit_trail,container,false);

        EditText trailNameEditText = layout.findViewById(R.id.trail_name);
        trailNameEditText.setText(trail.getTrailName());
        EditText trailDistanceEditText = layout.findViewById(R.id.trail_distance);
        trailDistanceEditText.setText(String.valueOf(trail.getTrailDistance()));
        Button editTrailButton = layout.findViewById(R.id.edit_trail_button);
        editTrailButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                trail.setTrailName(trailNameEditText.getText().toString());
                trail.setTrailDistance(Double.valueOf(trailDistanceEditText.getText().toString()));
                trailDatabaseHelper.updateTrail(trail);
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, new ViewTrailsFragment(trailDatabaseHelper, sharedPreferences)).commit();
            }
        });
        Button cancelEditTrailButton = layout.findViewById(R.id.cancel_edit_trail_button);
        cancelEditTrailButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, new TrailManagerFragment(trailDatabaseHelper, sharedPreferences, trail)).commit();
            }
        });

        return layout;
    }
}
