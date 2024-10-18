package com.example.trailrunner;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
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

public class CreateTrailActivity extends AppCompatActivity {

    Context context = this;
    private TrailDatabaseHelper trailDatabaseHelper = new TrailDatabaseHelper(context);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_create_trail);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        EditText trailNameEditText = findViewById(R.id.trail_name);
        EditText trailDistanceEditText = findViewById(R.id.trail_distance);
        Button createTrailButton = findViewById(R.id.create_trail_button);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        createTrailButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Trail newTrail = new Trail(trailNameEditText.getText().toString(),
                        Double.valueOf(trailDistanceEditText.getText().toString()),"Miles", user.getUid(),0);
                trailDatabaseHelper.addTrailToDatabase(newTrail);
                Intent returnToViewTrailsIntent = new Intent(CreateTrailActivity.this, ViewTrailsActivity.class);
                startActivity(returnToViewTrailsIntent);
            }
        });
    }
}
