package com.example.trailrunner;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class TrailManagerActivity extends AppCompatActivity {

    private Context context = this;
    private TrailDatabaseHelper trailDatabaseHelper = new TrailDatabaseHelper(context);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_trail_manager);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        SharedPreferences sharedPreferences = context.getSharedPreferences(getString(R.string.user_prefs), Context.MODE_PRIVATE);
        Trail trail = getIntent().getParcelableExtra("trail",Trail.class);

        TextView trailDataTextView = findViewById(R.id.trail_data);
        trailDataTextView.setText("Trail Name: " + trail.getTrailName() + "\nTrail Distance: " +
                trail.getTrailDistance() + " " + trail.getTrailDistanceUnit() + "\nUser Progress: " +
                trail.getUserTrailDistance() + " " + trail.getTrailDistanceUnit());
        Button setActiveTrailButton = findViewById(R.id.set_active_trail_button);
        setActiveTrailButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString(getString(R.string.user_pref_active_trail_key), String.valueOf(trail.getId()));
                editor.apply();
                Intent sendToUserHomeIntent = new Intent(TrailManagerActivity.this, UserHomeActivity.class);
                startActivity(sendToUserHomeIntent);
            }
        });

        Button resetUserProgressButton = findViewById(R.id.reset_progress_button);
        resetUserProgressButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                trail.setUserTrailDistance(0);
                trailDatabaseHelper.updateTrail(trail);
                Intent sendToSelf = new Intent(TrailManagerActivity.this, TrailManagerActivity.class);
                sendToSelf.putExtra("trail", (Parcelable) trail);
                startActivity(sendToSelf);
            }
        });

        Button editTrailButton = findViewById(R.id.edit_trail_button);
        editTrailButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent sendToEditTrailActivity = new Intent(TrailManagerActivity.this, EditTrailActivity.class);
                sendToEditTrailActivity.putExtra("trail", (Parcelable) trail);
                startActivity(sendToEditTrailActivity);
            }
        });

        Button deleteTrailButton = findViewById(R.id.delete_trail_button);
        deleteTrailButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                trailDatabaseHelper.deleteTrail(trail);
                Intent sendToViewTrailsActivity = new Intent(TrailManagerActivity.this, ViewTrailsActivity.class);
                startActivity(sendToViewTrailsActivity);
            }
        });
    }
}
