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
import com.firebase.ui.auth.IdpResponse;
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Arrays;
import java.util.List;

public class LoginFragment extends Fragment {

    private final TrailDatabaseHelper trailDatabaseHelper;
    private final SharedPreferences sharedPreferences;

    public LoginFragment(TrailDatabaseHelper trailDatabaseHelper, SharedPreferences sharedPreferences){
        super(R.layout.fragment_login);
        this.trailDatabaseHelper = trailDatabaseHelper;
        this.sharedPreferences = sharedPreferences;
    }

    private final ActivityResultLauncher<Intent> createAccountLauncher = registerForActivityResult(
            new FirebaseAuthUIActivityResultContract(),
            new ActivityResultCallback<FirebaseAuthUIAuthenticationResult>() {
                @Override
                public void onActivityResult(FirebaseAuthUIAuthenticationResult result) {
                    onSignInResult(result, "create");
                }
            }
    );

    private final ActivityResultLauncher<Intent> signInLauncher = registerForActivityResult(
            new FirebaseAuthUIActivityResultContract(),
            new ActivityResultCallback<FirebaseAuthUIAuthenticationResult>() {
                @Override
                public void onActivityResult(FirebaseAuthUIAuthenticationResult result) {
                    onSignInResult(result, "login");
                }
            }
    );

    private void onSignInResult(FirebaseAuthUIAuthenticationResult result, String source) {
        IdpResponse response = result.getIdpResponse();
        if (result.getResultCode() == RESULT_OK) {
            // Successfully signed in
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            System.out.println("Source: " + source + " UID: " + user.getUid());
            if(source.equals("create")){
                if(trailDatabaseHelper.getAllTrailsForUser(user.getUid()).isEmpty()){
                    initializeUserTrails(user.getUid());
                }
            }
            getActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new UserHomeFragment(trailDatabaseHelper, sharedPreferences)).commit();
            // ...
        } else {
            // Sign in failed. If response is null the user canceled the
            // sign-in flow using the back button. Otherwise check
            // response.getError().getErrorCode() and handle the error.
            // ...
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //Hide main activities bottom navigation
        ((MainActivity)getActivity()).hideNavigation();

        LinearLayout layout = (LinearLayout) inflater.inflate(R.layout.fragment_login,container,false);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user != null){
            getActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new UserHomeFragment(trailDatabaseHelper, sharedPreferences)).commit();
        }



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

    private void initializeUserTrails(String uid){
        Trail appalachianTrail = new Trail("Appalachian Trail",2197.4,"Miles",uid,0);
        trailDatabaseHelper.addTrailToDatabase(appalachianTrail);
        Trail mountNittanyLoopTrail = new Trail("Mount Nittany Loop", 5, "Miles", uid,0);
        trailDatabaseHelper.addTrailToDatabase(mountNittanyLoopTrail);
        Trail pacificCrestTrail = new Trail("Pacific Crest Trail", 2650, "Miles", uid, 0);
        trailDatabaseHelper.addTrailToDatabase(pacificCrestTrail);
    }
}