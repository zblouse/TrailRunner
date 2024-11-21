package com.example.trailrunner;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
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

public class MainActivity extends AppCompatActivity {

    private TrailDatabaseHelper trailDatabaseHelper;
    private SharedPreferences sharedPreferences;

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
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        trailDatabaseHelper = new TrailDatabaseHelper(this);
        sharedPreferences = this.getSharedPreferences(getString(R.string.user_prefs), Context.MODE_PRIVATE);

        //setup navigation
        navigationView = findViewById(R.id.bottom_navigation);
        navigationView.setOnItemSelectedListener(navListener);

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

}
