package com.example.trailrunner;

import android.content.SharedPreferences;
import android.location.Location;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;

/**
 * Fragment for viewing the user's list of trails in the database. They are displayed in a RecyclerView
 */
public class ViewTrailsFragment extends Fragment implements AdapterView.OnItemSelectedListener {

    private TrailDatabaseHelper trailDatabaseHelper;
    private SharedPreferences sharedPreferences;
    private LinearLayout layout;
    private FirebaseUser user;
    private TrailViewAdapter trailViewAdapter;
    private List<Trail> trails;

    public ViewTrailsFragment(){
        super(R.layout.fragment_view_trails);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //Show main activities bottom navigation
        ((MainActivity)getActivity()).showNavigation();
        layout = (LinearLayout) inflater.inflate(R.layout.fragment_view_trails,container,false);
        trailDatabaseHelper = ((MainActivity)getActivity()).getTrailDatabaseHelper();
        sharedPreferences = ((MainActivity)getActivity()).getSharedPreferences();
        user = FirebaseAuth.getInstance().getCurrentUser();
        trails = trailDatabaseHelper.getAllTrailsForUser(user.getUid());
        //Gets references to all UI elements
        Button createCustomTrailButton = layout.findViewById(R.id.create_trail_button);
        //clicking the createCustomTrailButton launches the CreateCustomTrailFragment
        createCustomTrailButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, new CreateTrailFragment()).commit();
            }
        });
        //A spinner(dropdown) for selecting a distance filter, displays a different list based on the user's prefered unit
        Spinner distanceSpinner = layout.findViewById(R.id.distance_spinner);
        ArrayAdapter<CharSequence> adapter;
        if(sharedPreferences.getString(getString(R.string.user_pref_unit_key), "Metric").equals("Imperial")) {
            adapter = ArrayAdapter.createFromResource(
                    getContext(),
                    R.array.trail_distance_filter_array_imperial,
                    android.R.layout.simple_spinner_item
            );
        } else {
            adapter = ArrayAdapter.createFromResource(
                    getContext(),
                    R.array.trail_distance_filter_array_metric,
                    android.R.layout.simple_spinner_item
            );
        }

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        distanceSpinner.setAdapter(adapter);
        distanceSpinner.setOnItemSelectedListener(this);
        RecyclerView recyclerView = layout.findViewById(R.id.trail_list);
        trailViewAdapter = new TrailViewAdapter(trails, ViewTrailsFragment.this, trailDatabaseHelper, sharedPreferences);
        recyclerView.setAdapter(trailViewAdapter);
        LinearLayoutManager layoutManager = new LinearLayoutManager(layout.getContext());
        recyclerView.setLayoutManager(layoutManager);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(),
                layoutManager.getOrientation());
        recyclerView.addItemDecoration(dividerItemDecoration);

        return layout;
    }

    /**
     * Method called when the user selects an item in the spinner. Changes the trails that are displayed based on distance
     * @param parent The AdapterView where the selection happened
     * @param view The view within the AdapterView that was clicked
     * @param position The position of the view in the adapter
     * @param id The row id of the item that is selected
     */
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String distance = (String) parent.getItemAtPosition(position);
        if(distance.equals("Any")){
            showTrailsWithinDistance(-1);
            return;
        }
        int numberValue = Integer.valueOf(distance.split(" ")[0]);
        if(sharedPreferences.getString(getString(R.string.user_pref_unit_key), "Metric").equals("Imperial")) {
            showTrailsWithinDistance(LatLongUtils.convertMilesToKm(numberValue));
        } else {
            showTrailsWithinDistance(numberValue);
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    /**
     * Shows trails within the provided distance in KM
     * @param distanceKm
     */
    private void showTrailsWithinDistance(double distanceKm){
        List<Trail> allUserTrails = trailDatabaseHelper.getAllTrailsForUser(user.getUid());
        //If the distance is negative 1, show all trails
        if(distanceKm == -1){
            showTrails(allUserTrails);
        } else {
            List<Trail> trailsWithinRange = new ArrayList<>();
            Location currentUserLocation = ((MainActivity) getActivity()).getLocationUtils().getLastKnownLocation();
            //If the user's location is currently unavailable, show all trails
            if (currentUserLocation == null) {
                Toast toast = Toast.makeText(getActivity().getApplicationContext(), "Location current unavailable. Showing all trails.", Toast.LENGTH_SHORT);
                toast.show();
                showTrails(allUserTrails);
            } else {
                //Filters trails to only those within range
                for (Trail trail : allUserTrails) {
                    LatLng trailStart = new LatLng(trail.getTrailStartLatitude(), trail.getTrailStartLongitude());
                    LatLng userLatLng = new LatLng(currentUserLocation.getLatitude(), currentUserLocation.getLongitude());
                    if (LatLongUtils.calculateDistanceKm(trailStart, userLatLng) <= distanceKm) {
                        trailsWithinRange.add(trail);
                    }
                }
                showTrails(trailsWithinRange);
            }
        }
    }

    /**
     * Updates the list of trails and updates the trailViewAdapter
     * @param showingTrails
     */
    private void showTrails(List<Trail> showingTrails){
        trails.clear();
        trails.addAll(showingTrails);
        trailViewAdapter.notifyDataSetChanged();
    }
}
