package com.example.trailrunner;

import static android.icu.number.NumberRangeFormatter.with;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;
import java.util.Random;

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
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        trailDatabaseHelper = new TrailDatabaseHelper(this);
        sharedPreferences = this.getSharedPreferences(getString(R.string.user_prefs), Context.MODE_PRIVATE);
        locationUtils = new LocationUtils(this);
        //setup navigation
        navigationView = findViewById(R.id.bottom_navigation);
        navigationView.setOnItemSelectedListener(navListener);

        boolean notificationGranted = ActivityCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED;
        if(!notificationGranted){
            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.POST_NOTIFICATIONS}, 5);
        }

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, new LoginFragment(trailDatabaseHelper, sharedPreferences)).commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //Inflates the UI elements in the action_bar menu
        getMenuInflater().inflate(R.menu.navigation, menu);
        getSupportActionBar().hide();
        return true;
    }

    private final BottomNavigationView.OnItemSelectedListener navListener = item -> {

        int itemId = item.getItemId();
        if (itemId == R.id.action_home) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new LoginFragment(trailDatabaseHelper, sharedPreferences)).commit();
        } else if (itemId == R.id.action_settings) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new SettingsFragment()).commit();
        } else if (itemId == R.id.action_workout){
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new WorkoutFragment()).commit();
        }

        return true;
    };

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection.
        if(item.getItemId() == R.id.action_home){
            //when the home button is clicked, switch the active fragment to the ListViewFragment
            //that displays the list of houses in the RecyclerView
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new UserHomeFragment(trailDatabaseHelper, sharedPreferences)).commit();
        }
        return super.onOptionsItemSelected(item);

    }

    public void hideNavigation(){
        navigationView.setVisibility(BottomNavigationView.GONE);
    }

    public void showNavigation(){
        navigationView.setVisibility(BottomNavigationView.VISIBLE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == LocationUtils.PERMISSIONS_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            }
        }
    }

    public LocationUtils getLocationUtils(){
        return this.locationUtils;
    }

    public TrailDatabaseHelper getTrailDatabaseHelper(){
        return this.trailDatabaseHelper;
    }

    public SharedPreferences getSharedPreferences(){
        return this.sharedPreferences;
    }

    private void createNotificationChannel() {
        CharSequence name = "trail_runner_channel";
        String description = "trail_runner_description";
        int importance = NotificationManager.IMPORTANCE_DEFAULT;
        NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
        channel.setDescription(description);
        // Register the channel with the system; you can't change the importance
        // or other notification behaviors after this.
        NotificationManager notificationManager = getSystemService(NotificationManager.class);
        notificationManager.createNotificationChannel(channel);

    }

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
