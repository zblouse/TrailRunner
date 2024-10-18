package com.example.trailrunner;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class EditTrailActivity extends AppCompatActivity {

    Context context = this;
    private TrailDatabaseHelper trailDatabaseHelper = new TrailDatabaseHelper(context);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_edit_trail);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        Trail trail = getIntent().getParcelableExtra("trail",Trail.class);

        EditText trailNameEditText = findViewById(R.id.trail_name);
        trailNameEditText.setText(trail.getTrailName());
        EditText trailDistanceEditText = findViewById(R.id.trail_distance);
        trailDistanceEditText.setText(String.valueOf(trail.getTrailDistance()));
        Button editTrailButton = findViewById(R.id.edit_trail_button);
        editTrailButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                trail.setTrailName(trailNameEditText.getText().toString());
                trail.setTrailDistance(Double.valueOf(trailDistanceEditText.getText().toString()));
                trailDatabaseHelper.updateTrail(trail);
                Intent returnToViewTrailsIntent = new Intent(EditTrailActivity.this, ViewTrailsActivity.class);
                startActivity(returnToViewTrailsIntent);
            }
        });
        Button cancelEditTrailButton = findViewById(R.id.cancel_edit_trail_button);
        cancelEditTrailButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent sendToTrailManagerActivity = new Intent(EditTrailActivity.this, TrailManagerActivity.class);
                sendToTrailManagerActivity.putExtra("trail", (Parcelable) trail);
                startActivity(sendToTrailManagerActivity);
            }
        });
    }
}
