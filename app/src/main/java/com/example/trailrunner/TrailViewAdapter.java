package com.example.trailrunner;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

/**
 * RecyclerView adapter for a trail
 */
public class TrailViewAdapter extends RecyclerView.Adapter<TrailViewAdapter.ViewHolder> {

    private final List<Trail> trails;
    private final TrailDatabaseHelper trailDatabaseHelper;
    private final SharedPreferences sharedPreferences;
    private final Fragment hostFragment;

    public TrailViewAdapter(List<Trail> trails, Fragment hostFragment,
            TrailDatabaseHelper trailDatabaseHelper, SharedPreferences sharedPreferences){
        this.trails = trails;
        this.hostFragment = hostFragment;
        this.trailDatabaseHelper = trailDatabaseHelper;
        this.sharedPreferences = sharedPreferences;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.trail_display,parent,false);
        return new ViewHolder(view);
    }

    /**
     * Initializes the UI elements for the Trail in the recycler view
     * @param holder The ViewHolder which should be updated to represent the contents of the
     *        item at the given position in the data set.
     * @param position The position of the item within the adapter's data set.
     */
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Trail trail = trails.get(position);
        String trailDistance = "";
        //Checks the units in the user preferences and the units of the trail to figure out what number and unit to display
        if(sharedPreferences.getString(hostFragment.getString(R.string.user_pref_unit_key),"Metric").equals("Metric")){
            if(trail.getTrailDistanceUnit().equals("Miles")){
                trailDistance += String.format("%.2f",LatLongUtils.convertMilesToKm(trail.getTrailDistance())) + " km";
            } else {
                trailDistance += trail.getUserTrailDistance() + " km";
            }
        } else {
            if(trail.getTrailDistanceUnit().equals("Miles")){
                trailDistance += trail.getTrailDistance() + " Miles";
            } else {
                trailDistance +=  String.format("%.2f", LatLongUtils.convertKmToMiles(trail.getTrailDistance())) + " Miles";
            }
        }

        holder.trailDataView.setText(trail.getTrailName() + " " + trailDistance);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hostFragment.getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, new TrailManagerFragment(trail)).commit();
            }
        });

    }

    @Override
    public int getItemCount() {
        return trails.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView trailDataView;

        public ViewHolder(View itemView){
            super(itemView);
            trailDataView = itemView.findViewById(R.id.trail_data);
        }
    }
}