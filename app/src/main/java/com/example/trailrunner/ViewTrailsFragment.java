package com.example.trailrunner;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;

public class ViewTrailsFragment extends Fragment {

    private final TrailDatabaseHelper trailDatabaseHelper;
    private final SharedPreferences sharedPreferences;

    public ViewTrailsFragment(TrailDatabaseHelper trailDatabaseHelper, SharedPreferences sharedPreferences){
        super(R.layout.fragment_view_trails);
        this.trailDatabaseHelper = trailDatabaseHelper;
        this.sharedPreferences = sharedPreferences;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        LinearLayout layout = (LinearLayout) inflater.inflate(R.layout.fragment_view_trails,container,false);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        List<Trail> trails = trailDatabaseHelper.getAllTrailsForUser(user.getUid());

        RecyclerView recyclerView = layout.findViewById(R.id.trail_list);
        TrailViewAdapter trailViewAdapter = new TrailViewAdapter(trails, ViewTrailsFragment.this, trailDatabaseHelper, sharedPreferences);
        recyclerView.setAdapter(trailViewAdapter);
        LinearLayoutManager layoutManager = new LinearLayoutManager(layout.getContext());
        recyclerView.setLayoutManager(layoutManager);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(),
                layoutManager.getOrientation());
        recyclerView.addItemDecoration(dividerItemDecoration);

        Button createCustomTrailButton = layout.findViewById(R.id.create_trail_button);
        createCustomTrailButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, new CreateTrailFragment(trailDatabaseHelper, sharedPreferences)).commit();
            }
        });

        return layout;
    }
}
