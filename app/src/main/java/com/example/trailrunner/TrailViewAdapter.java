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
 * RecyclerView adapter for a product
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

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Trail trail = trails.get(position);

        holder.trailDataView.setText(trail.getTrailName() + " " + trail.getTrailDistance() + " " + trail.getTrailDistanceUnit());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hostFragment.getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, new TrailManagerFragment(trailDatabaseHelper, sharedPreferences, trail)).commit();
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