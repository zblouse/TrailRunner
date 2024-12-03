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
 * Fragment that allows the user to edit an existing trail
 */
public class EditTrailFragment extends Fragment {

    private TrailDatabaseHelper trailDatabaseHelper;
    private SharedPreferences sharedPreferences;
    private final Trail trail;

    /**
     * Constructor with the trail we are editing in this instance of the fragment
     * @param trail
     */
    public EditTrailFragment( Trail trail){
        super(R.layout.fragment_edit_trail);
        this.trail = trail;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //Show main activities bottom navigation
        ((MainActivity)getActivity()).showNavigation();
        LinearLayout layout = (LinearLayout) inflater.inflate(R.layout.fragment_edit_trail,container,false);
        trailDatabaseHelper = ((MainActivity)getActivity()).getTrailDatabaseHelper();
        sharedPreferences = ((MainActivity)getActivity()).getSharedPreferences();
        //Get references to all UI elements
        EditText trailNameEditText = layout.findViewById(R.id.trail_name);
        trailNameEditText.setText(trail.getTrailName());
        EditText trailDistanceEditText = layout.findViewById(R.id.trail_distance);
        trailDistanceEditText.setText(String.valueOf(trail.getTrailDistance()));
        Button editTrailButton = layout.findViewById(R.id.edit_trail_button);
        //When the edit trail button is clicked, update the trail in the database
        editTrailButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!trailNameEditText.getText().toString().isEmpty() && !trailDistanceEditText.getText().toString().isEmpty()) {
                    trail.setTrailName(trailNameEditText.getText().toString());
                    trail.setTrailDistance(Double.valueOf(trailDistanceEditText.getText().toString()));
                    trailDatabaseHelper.updateTrail(trail);
                    getActivity().getSupportFragmentManager().beginTransaction()
                            .replace(R.id.fragment_container, new ViewTrailsFragment()).commit();
                } else {
                    Toast toast = Toast.makeText(getActivity(),"All fields must be filled out", Toast.LENGTH_SHORT);
                    toast.show();
                }
            }
        });
        Button cancelEditTrailButton = layout.findViewById(R.id.cancel_edit_trail_button);
        //When the button is clicked, return to the TrailManagerFragment
        cancelEditTrailButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, new TrailManagerFragment(trail)).commit();
            }
        });

        return layout;
    }
}
