package com.example.trailrunner;

import static android.app.Activity.RESULT_OK;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.fragment.app.Fragment;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract;
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Arrays;
import java.util.List;

/**
 * Fragment that handles user authentication
 */
public class LoginFragment extends Fragment {

    private TrailDatabaseHelper trailDatabaseHelper;
    private SharedPreferences sharedPreferences;

    public LoginFragment(){
        super(R.layout.fragment_login);
    }

    //ActivityResultLauncher that is used when the user is creating an account
    private final ActivityResultLauncher<Intent> createAccountLauncher = registerForActivityResult(
            new FirebaseAuthUIActivityResultContract(),
            new ActivityResultCallback<FirebaseAuthUIAuthenticationResult>() {
                @Override
                public void onActivityResult(FirebaseAuthUIAuthenticationResult result) {
                    onSignInResult(result, "create");
                }
            }
    );

    //ActivityResultLauncher that is used when an existing user is logging in
    private final ActivityResultLauncher<Intent> signInLauncher = registerForActivityResult(
            new FirebaseAuthUIActivityResultContract(),
            new ActivityResultCallback<FirebaseAuthUIAuthenticationResult>() {
                @Override
                public void onActivityResult(FirebaseAuthUIAuthenticationResult result) {
                    onSignInResult(result, "login");
                }
            }
    );

    /**
     * Method called by the two ActivityResultLaunchers above
     * @param result
     * @param source
     */
    private void onSignInResult(FirebaseAuthUIAuthenticationResult result, String source) {

        if (result.getResultCode() == RESULT_OK) {
            // Successfully signed in
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            //If we got here from the create account button, initialize the user's trails
            if(source.equals("create")){
                if(trailDatabaseHelper.getAllTrailsForUser(user.getUid()).isEmpty()){
                    initializeUserTrails(user.getUid());
                }
            }
            //Launch the UserHomeFragment
            getActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new UserHomeFragment()).commit();
            // ...
        } else {
            // Sign in failed
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //Hide main activities bottom navigation
        ((MainActivity)getActivity()).hideNavigation();
        trailDatabaseHelper = ((MainActivity)getActivity()).getTrailDatabaseHelper();
        sharedPreferences = ((MainActivity)getActivity()).getSharedPreferences();
        LinearLayout layout = (LinearLayout) inflater.inflate(R.layout.fragment_login,container,false);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        //If the user is already authenticated, launch the UserHomeFragment
        if(user != null){
            getActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new UserHomeFragment()).commit();
        }
        //Login button launches the Firebase authentication intent using the signInLauncher
        Button loginButton = layout.findViewById(R.id.login_button);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // Choose authentication providers
                List<AuthUI.IdpConfig> providers = Arrays.asList(
                        new AuthUI.IdpConfig.PhoneBuilder().build(),
                        new AuthUI.IdpConfig.GoogleBuilder().build());

                // Create and launch sign-in intent
                Intent signInIntent = AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAvailableProviders(providers)
                        .build();
                signInLauncher.launch(signInIntent);
            }
        });

        //Register button launches the Firebase authentication intent using the createAccountLauncher
        Button registerButton = layout.findViewById(R.id.create_account_button);
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // Choose authentication providers
                List<AuthUI.IdpConfig> providers = Arrays.asList(
                        new AuthUI.IdpConfig.PhoneBuilder().build(),
                        new AuthUI.IdpConfig.GoogleBuilder().build());

                // Create and launch sign-in intent
                Intent signInIntent = AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAvailableProviders(providers)
                        .build();
                createAccountLauncher.launch(signInIntent);
            }
        });
        return layout;
    }

    /**
     * Creates the initial user trails in the database using the TrailDatabaseHelper
     * @param uid
     */
    private void initializeUserTrails(String uid){
        Trail appalachianTrail = new Trail("Appalachian Trail",2197.4,"Miles",uid,0,34.62671573943575, -84.1938571527857);
        trailDatabaseHelper.addTrailToDatabase(appalachianTrail);
        Trail mountNittanyLoopTrail = new Trail("Mount Nittany Loop", 5, "Miles", uid,0, 40.8115455730065, -77.80694111132333);
        trailDatabaseHelper.addTrailToDatabase(mountNittanyLoopTrail);
        Trail pacificCrestTrail = new Trail("Pacific Crest Trail", 2650, "Miles", uid, 0, 49.00027269044327, -120.80229954598605);
        trailDatabaseHelper.addTrailToDatabase(pacificCrestTrail);
    }
}