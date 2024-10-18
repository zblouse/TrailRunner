package com.example.trailrunner;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.datastore.preferences.core.MutablePreferences;
import androidx.datastore.preferences.core.Preferences;
import androidx.datastore.preferences.core.PreferencesKeys;
import androidx.datastore.rxjava3.RxDataStore;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import io.reactivex.rxjava3.core.Single;

/**
 * RecyclerView adapter for a product
 */
public class TrailViewAdapter extends RecyclerView.Adapter<TrailViewAdapter.ViewHolder> {

    SharedPreferences sharedPref;
    private final List<Trail> trails;
    private final ViewTrailsActivity viewTrailsActivity;

    public TrailViewAdapter(List<Trail> trails, SharedPreferences sharedPref, ViewTrailsActivity viewTrailsActivity){
        this.trails = trails;
        this.sharedPref = sharedPref;
        this.viewTrailsActivity = viewTrailsActivity;
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
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putString(viewTrailsActivity.getString(R.string.user_pref_active_trail_key), String.valueOf(trail.getId()));
                editor.apply();
                Intent sendToUserHomeIntent = new Intent(viewTrailsActivity, UserHomeActivity.class);
                viewTrailsActivity.startActivity(sendToUserHomeIntent);
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