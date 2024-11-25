package com.example.trailrunner;

import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import java.util.Locale;

public class WorkoutFragment extends Fragment {

    private LinearLayout layout;
    private MainActivity mainActivity;
    private TextView timerTextView;
    private TextView distanceTextView;
    private Button startButton;
    private Button pauseButton;
    private Button stopButton;
    private Button saveButton;
    private Button discardButton;
    private boolean paused;

    private int seconds;

    public WorkoutFragment(){
        super(R.layout.fragment_workout);
        paused = false;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //Hide main activities bottom navigation during run, to prevent accidental clicking
        mainActivity = ((MainActivity) getActivity());
        mainActivity.showNavigation();
        layout = (LinearLayout) inflater.inflate(R.layout.fragment_workout,container,false);
        timerTextView = layout.findViewById(R.id.time_display);
        distanceTextView = layout.findViewById(R.id.distance_display);

        startButton = layout.findViewById(R.id.start_run_button);
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startWorkout();
            }
        });

        pauseButton = layout.findViewById(R.id.pause_run_button);
        pauseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pauseWorkout();
            }
        });

        stopButton = layout.findViewById(R.id.finish_run_button);
        stopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopWorkout();
            }
        });

        saveButton = layout.findViewById(R.id.save_run_button);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveWorkout();
            }
        });

        discardButton = layout.findViewById(R.id.discard_run_button);
        discardButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                discardWorkout();
            }
        });

        ((ViewGroup)layout).removeView(pauseButton);
        ((ViewGroup)layout).removeView(stopButton);
        ((ViewGroup)layout).removeView(discardButton);
        ((ViewGroup)layout).removeView(saveButton);

        return layout;
    }

    private void startWorkout(){
        ((ViewGroup)layout).addView(pauseButton);
        ((ViewGroup)layout).addView(stopButton);
        ((ViewGroup)layout).removeView(startButton);
        seconds = 0;
        mainActivity.hideNavigation();
        Handler handler = new Handler(Looper.myLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {
                int hours = seconds / 3600;
                int minutes = (seconds % 3600) / 60;
                int formattedSeconds = seconds % 60;
                String formattedTime;
                if(hours > 0){
                    formattedTime = String.format(Locale.getDefault(), "%d:%02d:%02d", hours, minutes, formattedSeconds);
                } else {
                    formattedTime = String.format(Locale.getDefault(), "%02d:%02d", minutes, formattedSeconds);
                }
                timerTextView.setText(formattedTime);
                if(!paused){
                    seconds++;
                }
                handler.postDelayed(this,1000);
            }
        });

    }

    private void pauseWorkout(){
        paused = !paused;
        if(paused){
            pauseButton.setText("Unpause Workout");
        } else {
            pauseButton.setText("Pause Workout");
        }

    }

    private void stopWorkout(){
        paused = true;
        AlertDialog.Builder confirmDoneBuilder = new AlertDialog.Builder(mainActivity);
        confirmDoneBuilder.setMessage("Finish workout?");
        confirmDoneBuilder.setCancelable(false);
        confirmDoneBuilder.setPositiveButton("Finish", (DialogInterface.OnClickListener)(dialog, which) -> {
            ((ViewGroup)layout).addView(discardButton);
            ((ViewGroup)layout).addView(saveButton);
            ((ViewGroup)layout).removeView(pauseButton);
            ((ViewGroup)layout).removeView(stopButton);
        });
        confirmDoneBuilder.setNegativeButton("Resume",(DialogInterface.OnClickListener)(dialog, which) -> {
            pauseWorkout();
            dialog.cancel();
        });

        AlertDialog alertDialog = confirmDoneBuilder.create();
        alertDialog.show();
    }

    private void saveWorkout(){

    }

    private void discardWorkout(){
        mainActivity.getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, new UserHomeFragment()).commit();
    }
}
