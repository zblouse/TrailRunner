package com.example.trailrunner;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;

import androidx.fragment.app.Fragment;

/**
 * Fragment that displays the settings options to the user and allows them to set their settings
 */
public class SettingsFragment extends Fragment implements AdapterView.OnItemSelectedListener {

    private LinearLayout layout;

    public SettingsFragment(){
        super(R.layout.fragment_settings);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //Show main activities bottom navigation
        ((MainActivity) getActivity()).showNavigation();
        layout = (LinearLayout) inflater.inflate(R.layout.fragment_settings, container, false);
        Spinner unitSpinner = layout.findViewById(R.id.unit_spinner);
        //sets the options in the unit spinner(dropdown) to the strings in the unit_array
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                getContext(),
                R.array.unit_array,
                android.R.layout.simple_spinner_item
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        unitSpinner.setAdapter(adapter);
        String selectedUnit = ((MainActivity)getActivity()).getSharedPreferences().getString(getString(R.string.user_pref_unit_key), "Metric");
        unitSpinner.setSelection(adapter.getPosition(selectedUnit));
        unitSpinner.setOnItemSelectedListener(this);
        return layout;
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        //When the unit spinner is updated set the units in the user shared preferences
        if(parent.getId() == R.id.unit_spinner) {
            String unit = (String) parent.getItemAtPosition(position);
            SharedPreferences.Editor editor = ((MainActivity)getActivity()).getSharedPreferences().edit();
            editor.putString(getString(R.string.user_pref_unit_key), unit);
            editor.apply();
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
