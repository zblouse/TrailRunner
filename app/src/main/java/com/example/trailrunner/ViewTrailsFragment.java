package com.example.trailrunner;

import android.content.Intent;
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

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;

public class ViewTrailsFragment extends Fragment implements AdapterView.OnItemSelectedListener {

    private final TrailDatabaseHelper trailDatabaseHelper;
    private final SharedPreferences sharedPreferences;
    private LinearLayout layout;
    private FirebaseUser user;
    private TrailViewAdapter trailViewAdapter;
    private List<Trail> trails;

    public ViewTrailsFragment(TrailDatabaseHelper trailDatabaseHelper, SharedPreferences sharedPreferences){
        super(R.layout.fragment_view_trails);
        this.trailDatabaseHelper = trailDatabaseHelper;
        this.sharedPreferences = sharedPreferences;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //Show main activities bottom navigation
        ((MainActivity)getActivity()).showNavigation();
        layout = (LinearLayout) inflater.inflate(R.layout.fragment_view_trails,container,false);

        user = FirebaseAuth.getInstance().getCurrentUser();
        trails = trailDatabaseHelper.getAllTrailsForUser(user.getUid());
        Button createCustomTrailButton = layout.findViewById(R.id.create_trail_button);
        createCustomTrailButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, new CreateTrailFragment(trailDatabaseHelper, sharedPreferences)).commit();
            }
        });

        Spinner distanceSpinner = layout.findViewById(R.id.distance_spinner);
        ArrayAdapter<CharSequence> adapter;
        if(sharedPreferences.getString("distance_unit", "metric").equals("imperial")) {
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

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String distance = (String) parent.getItemAtPosition(position);
        if(distance.equals("Any")){
            showTrailsWithinDistance(-1);
            return;
        }
        int numberValue = Integer.valueOf(distance.split(" ")[0]);
        if(sharedPreferences.getString("distance_unit", "metric").equals("imperial")) {
            showTrailsWithinDistance(LatLongUtils.convertMilesToKm(numberValue));
        } else {
            showTrailsWithinDistance(numberValue);
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    private void showTrailsWithinDistance(double distanceKm){
        List<Trail> allUserTrails = trailDatabaseHelper.getAllTrailsForUser(user.getUid());
        if(distanceKm == -1){
            showTrails(allUserTrails);
        } else {
            List<Trail> trailsWithinRange = new ArrayList<>();
            Location currentUserLocation = ((MainActivity) getActivity()).getLocationUtils().getLastKnownLocation();
            if (currentUserLocation == null) {
                Toast toast = Toast.makeText(getActivity().getApplicationContext(), "Location current unavailable. Showing all trails.", Toast.LENGTH_SHORT);
                toast.show();
                showTrails(allUserTrails);
            } else {
                System.out.println("Filtering out trails from user location: " + currentUserLocation.getLatitude() + ", " + currentUserLocation.getLongitude());
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

    private void showTrails(List<Trail> showingTrails){
        System.out.println("Showing: " + showingTrails.size() + " trails");
        trails.clear();
        trails.addAll(showingTrails);
        trailViewAdapter.notifyDataSetChanged();
        for(Trail trail: trails){
            System.out.println("Showing trail: " + trail.getTrailName());
        }
    }
}
