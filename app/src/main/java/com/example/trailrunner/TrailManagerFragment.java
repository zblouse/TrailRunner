package com.example.trailrunner;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

/**
 * Fragment that presents options to manage an individual trail
 * Implements OnMapReadyCallback when the map is ready
 */
public class TrailManagerFragment extends Fragment implements OnMapReadyCallback {

    private TrailDatabaseHelper trailDatabaseHelper;
    private SharedPreferences sharedPreferences;
    private final Trail trail;

    public TrailManagerFragment(Trail trail){
        super(R.layout.fragment_trail_manager);
        this.trail = trail;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //Show main activities bottom navigation
        ((MainActivity)getActivity()).showNavigation();
        LinearLayout layout = (LinearLayout) inflater.inflate(R.layout.fragment_trail_manager,container,false);

        trailDatabaseHelper = ((MainActivity)getActivity()).getTrailDatabaseHelper();
        sharedPreferences = ((MainActivity)getActivity()).getSharedPreferences();

        //Gets a reference to all UI elements
        TextView trailDataTextView = layout.findViewById(R.id.trail_data);
        //Displays information for the trail
        trailDataTextView.setText("Trail Name: " + trail.getTrailName() + "\nTrail Distance: " +
                trail.getTrailDistance() + " " + trail.getTrailDistanceUnit() + "\nUser Progress: " +
                String.format("%.2f",trail.getUserTrailDistance()) + " " + trail.getTrailDistanceUnit());
        Button setActiveTrailButton = layout.findViewById(R.id.set_active_trail_button);
        //when the setActiveTrail button is clicked this trail is set as the active trail in the user's preferences
        setActiveTrailButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString(getString(R.string.user_pref_active_trail_key), String.valueOf(trail.getId()));
                editor.apply();
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, new UserHomeFragment()).commit();
            }
        });

        Button resetUserProgressButton = layout.findViewById(R.id.reset_progress_button);
        //when the resetUserProgressButton is clicked, the user trail distance is set to 0
        resetUserProgressButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                trail.setUserTrailDistance(0);
                trailDatabaseHelper.updateTrail(trail);
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, new TrailManagerFragment(trail)).commit();
            }
        });

        Button editTrailButton = layout.findViewById(R.id.edit_trail_button);
        //when the editTrailButton is clicked the EditTrailFragment is launched for this trail
        editTrailButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, new EditTrailFragment(trail)).commit();
            }
        });

        Button deleteTrailButton = layout.findViewById(R.id.delete_trail_button);
        //When the deleteTrailButton is clicked the trail is deleted from the database
        deleteTrailButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                trailDatabaseHelper.deleteTrail(trail);
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, new ViewTrailsFragment()).commit();
            }
        });

        Button viewTrailStartButton = layout.findViewById(R.id.view_trail_start_button);
        //when the viewTrailStartButton is clicked, the displayMap() method is called
        viewTrailStartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                displayMap();
            }
        });
        return layout;
    }

    /**
     * Method that is called when a House is clicked on in the RecyclerView. Displays the MapFragment
     * @param
     */
    public void displayMap(){
        SupportMapFragment mapFragment = SupportMapFragment.newInstance();
        //Swaps the active fragment to the Google Map Fragment
        getActivity().getSupportFragmentManager().beginTransaction().add(R.id.fragment_container, mapFragment).commit();
        mapFragment.getMapAsync(this);
    }

    /**
     * Async method called when the map is ready
     * @param googleMap
     */
    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        //Creates a LatLng object from the ActiveHouse's lat and long
        LatLng trailLatLong = new LatLng(trail.getTrailStartLatitude(),trail.getTrailStartLongitude());
        //Creates a marker on the map
        googleMap.addMarker(new MarkerOptions()
                //sets the lat and long for the marker
                .position(trailLatLong)
                //Sets the description for the marker
                //Make the Icon Orange to match the orange theme of the app
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE)));
        //Sets the Zoom to a reasonable level for a house
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(trailLatLong,15));
        //enables the zoom UI on the map
        googleMap.getUiSettings().setZoomControlsEnabled(true);
    }
}
