package com.example.trailrunner;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Random;

/**
 * MainActivity for the application. All fragments are displayed in it.
 */
public class MainActivity extends AppCompatActivity {

    public final String CHANNEL_ID = "TRAIL_RUNNER";
    private TrailDatabaseHelper trailDatabaseHelper;
    private SharedPreferences sharedPreferences;
    private LocationUtils locationUtils;
    private BottomNavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        createNotificationChannel();
        trailDatabaseHelper = new TrailDatabaseHelper(this);
        sharedPreferences = this.getSharedPreferences(getString(R.string.user_prefs), Context.MODE_PRIVATE);
        locationUtils = new LocationUtils(this);
        //setup navigation
        navigationView = findViewById(R.id.bottom_navigation);
        navigationView.setOnItemSelectedListener(navListener);

        //Check if we have notification permissions, if not, request them
        boolean notificationGranted = ActivityCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED;
        if(!notificationGranted){
            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.POST_NOTIFICATIONS}, 5);
        }

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, new LoginFragment()).commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //Inflates the UI elements in the action_bar menu
        getMenuInflater().inflate(R.menu.navigation, menu);
        getSupportActionBar().hide();
        return true;
    }

    //Called when the bottom navigation view is touched
    private final BottomNavigationView.OnItemSelectedListener navListener = item -> {

        int itemId = item.getItemId();
        //If the home button is pressed launch the user home fragment
        if (itemId == R.id.action_home) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new UserHomeFragment()).commit();
        } else if (itemId == R.id.action_settings) {
            //If the settings button is pressed launch the settings fragment
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new SettingsFragment()).commit();
        } else if (itemId == R.id.action_workout){
            //If the workout button is pressed, launch the workout fragment
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new WorkoutFragment()).commit();
        }

        return true;
    };

    /**
     * On some fragments(notable the workout fragment) we don't want to display the BottomNavigationView
     */
    public void hideNavigation(){
        navigationView.setVisibility(BottomNavigationView.GONE);
    }

    /**
     * On most fragments we do want to display the BottomNavigationView
     */
    public void showNavigation(){
        navigationView.setVisibility(BottomNavigationView.VISIBLE);
    }

    /**
     * Returns the common locationUtils object
     * @return LocationUtils
     */
    public LocationUtils getLocationUtils(){
        return this.locationUtils;
    }

    /**
     * Returns the common TrailDatabaseHelper
     * @return TrailDatabaseHelper
     */
    public TrailDatabaseHelper getTrailDatabaseHelper(){
        return this.trailDatabaseHelper;
    }

    /**
     * Returns the common sharedPreferences object
     * @return
     */
    public SharedPreferences getSharedPreferences(){
        return this.sharedPreferences;
    }

    /**
     * Creates the apps notification channel
     */
    private void createNotificationChannel() {
        CharSequence name = "trail_runner_channel";
        String description = "trail_runner_description";
        int importance = NotificationManager.IMPORTANCE_DEFAULT;
        NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
        channel.setDescription(description);
        // Register the channel with the system
        NotificationManager notificationManager = getSystemService(NotificationManager.class);
        notificationManager.createNotificationChannel(channel);

    }

    /**
     * Sends a notification to user using the provided title, description and image
     * @param title
     * @param description
     * @param imageId
     */
    public void sendNotification(String title, String description, int imageId){
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(imageId)
                .setContentTitle(title)
                .setContentText(description)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        boolean notificationGranted = ActivityCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED;
        if(!notificationGranted){
            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.POST_NOTIFICATIONS}, 5);
        }else {
            Random random = new Random();
            NotificationManagerCompat manager = NotificationManagerCompat.from(this);
            manager.notify(random.nextInt(), builder.build());
        }
    }
}