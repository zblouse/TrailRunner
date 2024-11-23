package com.example.trailrunner;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
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

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class CreateTrailFragment extends Fragment {

    private final TrailDatabaseHelper trailDatabaseHelper;
    private final SharedPreferences sharedPreferences;

    public CreateTrailFragment(TrailDatabaseHelper trailDatabaseHelper, SharedPreferences sharedPreferences){
        super(R.layout.fragment_create_trail);
        this.trailDatabaseHelper = trailDatabaseHelper;
        this.sharedPreferences = sharedPreferences;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //Show main activities bottom navigation
        ((MainActivity)getActivity()).showNavigation();
        LinearLayout layout = (LinearLayout) inflater.inflate(R.layout.fragment_create_trail,container,false);

        EditText trailNameEditText = layout.findViewById(R.id.trail_name);
        EditText trailDistanceEditText = layout.findViewById(R.id.trail_distance);
        EditText trailStartLatitudeEditText = layout.findViewById(R.id.latitude_edit_text);
        EditText trailStartLongitudeEditText =layout.findViewById(R.id.longitude_edit_text);
        Button createTrailButton = layout.findViewById(R.id.create_trail_button);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        createTrailButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Trail newTrail = new Trail(trailNameEditText.getText().toString(),
                        Double.valueOf(trailDistanceEditText.getText().toString()),"Miles",
                        user.getUid(),0,Double.valueOf(trailStartLatitudeEditText.getText().toString()),
                        Double.valueOf(trailStartLongitudeEditText.getText().toString()));
                trailDatabaseHelper.addTrailToDatabase(newTrail);
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, new ViewTrailsFragment(trailDatabaseHelper, sharedPreferences)).commit();
            }
        });

        return layout;
    }
}
