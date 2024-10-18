package com.example.trailrunner;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Parcelable;
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

    private final List<Trail> trails;
    private final ViewTrailsActivity viewTrailsActivity;

    public TrailViewAdapter(List<Trail> trails, ViewTrailsActivity viewTrailsActivity){
        this.trails = trails;
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
                Intent sendToTrailManagerActivity = new Intent(viewTrailsActivity, TrailManagerActivity.class);
                sendToTrailManagerActivity.putExtra("trail", (Parcelable) trail);
                viewTrailsActivity.startActivity(sendToTrailManagerActivity);
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