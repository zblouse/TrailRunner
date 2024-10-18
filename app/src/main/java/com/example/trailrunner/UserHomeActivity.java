package com.example.trailrunner;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.datastore.preferences.core.Preferences;
import androidx.datastore.preferences.core.PreferencesKeys;
import androidx.datastore.preferences.rxjava3.RxPreferenceDataStoreBuilder;
import androidx.datastore.rxjava3.RxDataStore;

import com.google.firebase.Firebase;
import com.google.firebase.auth.FirebaseAuth;

import java.io.File;
import java.util.concurrent.Callable;

import io.reactivex.rxjava3.core.Flowable;

public class UserHomeActivity extends AppCompatActivity {

    private Context context = this;
    private TrailDatabaseHelper trailDatabaseHelper = new TrailDatabaseHelper(context);
    private SharedPreferences sharedPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_user_home);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        sharedPref = context.getSharedPreferences(getString(R.string.user_prefs), Context.MODE_PRIVATE);
        TextView currentTrailTextView = findViewById(R.id.currentProgress);
        String currentTrailId = sharedPref.getString(getString(R.string.user_pref_active_trail_key), null);
        if(currentTrailId == null){
            currentTrailTextView.setText("No active trail. Trail progress is not being saved.");
        } else {
            Trail activeTrail = trailDatabaseHelper.getTrailById(currentTrailId);
            if(activeTrail != null){
                currentTrailTextView.setText("Current Trail: " + activeTrail.getTrailName() +
                        "\nProgress: " + activeTrail.getUserTrailDistance() + "\nPercent Complete: " +
                        String.format("%.2f",activeTrail.getUserTrailDistance()/activeTrail.getTrailDistance()*100) + "%");
            } else {
                currentTrailTextView.setText("Error with active trail. Trail progress is not being saved.");
            }
        }

        Button selectTrailButton = findViewById(R.id.select_trail_button);
        selectTrailButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent sendToViewTrailsIntent = new Intent(UserHomeActivity.this, ViewTrailsActivity.class);
                startActivity(sendToViewTrailsIntent);
            }
        });

        Button logoutButton = findViewById(R.id.logout_button);
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                Intent returnToLoginIntent = new Intent(UserHomeActivity.this, MainActivity.class);
                startActivity(returnToLoginIntent);
            }
        });
    }
}
