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

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

/**
 * Fragment that allows the user to create trails and add them to the database
 */
public class CreateTrailFragment extends Fragment {

    private TrailDatabaseHelper trailDatabaseHelper;
    private SharedPreferences sharedPreferences;

    public CreateTrailFragment(){
        super(R.layout.fragment_create_trail);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //Show main activities bottom navigation
        ((MainActivity)getActivity()).showNavigation();
        LinearLayout layout = (LinearLayout) inflater.inflate(R.layout.fragment_create_trail,container,false);
        trailDatabaseHelper = ((MainActivity)getActivity()).getTrailDatabaseHelper();
        sharedPreferences = ((MainActivity)getActivity()).getSharedPreferences();
        //Get references to UI elements in the fragment
        EditText trailNameEditText = layout.findViewById(R.id.trail_name);
        EditText trailDistanceEditText = layout.findViewById(R.id.trail_distance);
        EditText trailStartLatitudeEditText = layout.findViewById(R.id.latitude_edit_text);
        EditText trailStartLongitudeEditText =layout.findViewById(R.id.longitude_edit_text);
        Button createTrailButton = layout.findViewById(R.id.create_trail_button);
        //Get the current authenticated user
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        //When the create trail button is clicked, add the new trail to the database. Checks for empty EditTexts
        createTrailButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!trailNameEditText.getText().toString().isEmpty() && !trailDistanceEditText.getText().toString().isEmpty()
                        && !trailStartLatitudeEditText.getText().toString().isEmpty() && !trailStartLongitudeEditText.getText().toString().isEmpty()) {
                    Trail newTrail = new Trail(trailNameEditText.getText().toString(),
                            Double.valueOf(trailDistanceEditText.getText().toString()), "Miles",
                            user.getUid(), 0, Double.valueOf(trailStartLatitudeEditText.getText().toString()),
                            Double.valueOf(trailStartLongitudeEditText.getText().toString()));
                    trailDatabaseHelper.addTrailToDatabase(newTrail);
                    getActivity().getSupportFragmentManager().beginTransaction()
                            .replace(R.id.fragment_container, new ViewTrailsFragment()).commit();
                } else {
                    Toast toast = Toast.makeText(getActivity(),"All fields must be filled out", Toast.LENGTH_SHORT);
                    toast.show();
                }
            }
        });

        return layout;
    }
}
