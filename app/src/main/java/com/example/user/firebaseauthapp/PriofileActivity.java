package com.example.user.firebaseauthapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.crash.FirebaseCrash;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;

import java.io.FileInputStream;
import java.io.IOException;

public class PriofileActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView userEmail;
    private Button loguotButton;
    private FirebaseAuth firebaseAuth;
    private FirebaseRemoteConfig firebaseRemoteConfig;
    private TextView txtmessage;
    private TextView txtRegId;
    private BroadcastReceiver mRegistrationBroadcastReceiver;
    private static final String TAG = PriofileActivity.class.getSimpleName();
    private TextView mWelcomeTextView;
    private Button fetchButton;

    private static final String LOADING_PHRASE_CONFIG_KEY = "loading_phrase";
    private static final String WELCOME_MESSAGE_KEY = "welcome_message";
    private static final String WELCOME_MESSAGE_CAPS_KEY = "welcome_message_caps";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_priofile);

        txtRegId = (TextView) findViewById(R.id.reg_id);
        txtmessage = (TextView) findViewById(R.id.push_message);
        mWelcomeTextView = (TextView) findViewById(R.id.welcomeTextView);
        fetchButton = (Button) findViewById(R.id.fetchButton);

        mRegistrationBroadcastReceiver = new BroadcastReceiver() {

            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals(config.REGISTRATION_COMPLETE)) {
                    FirebaseMessaging.getInstance().subscribeToTopic(config.TOPIC_GLOBAL);
                    displayFirebaseRegId();
                } else if (intent.getAction().equals(config.PUSH_NOTIFICATION)) {
                    String message = intent.getStringExtra("message");
                    Toast.makeText(getApplicationContext(), "Push notification: " + message, Toast.LENGTH_LONG).show();
                    txtmessage.setText(message);
                }
            }
        };
        displayFirebaseRegId();

        firebaseAuth = FirebaseAuth.getInstance();

        if (firebaseAuth.getCurrentUser() == null) {
            startActivity(new Intent(this, LoginActivity.class));
        }

        FirebaseUser user = firebaseAuth.getCurrentUser();

        firebaseRemoteConfig = FirebaseRemoteConfig.getInstance();
        FirebaseRemoteConfigSettings configSettings = new FirebaseRemoteConfigSettings.Builder()
                .setDeveloperModeEnabled(BuildConfig.DEBUG)
                .build();
        firebaseRemoteConfig.setConfigSettings(configSettings);
        firebaseRemoteConfig.setDefaults(R.xml.remote_config);
        fetchWelcome();

        try {
            FileInputStream is = new FileInputStream("/non-existent/file");
            int c = is.read();
        } catch (IOException e) {
            FirebaseCrash.report(e); // Generate report
        }

        userEmail = (TextView) findViewById(R.id.userEmail);
        userEmail.setText("Welcome " + user.getEmail());
        loguotButton = (Button) findViewById(R.id.logoutButton);

        loguotButton.setOnClickListener(this);
        fetchButton.setOnClickListener(this);
    }

    private void displayFirebaseRegId() {
        SharedPreferences pref = getApplicationContext().getSharedPreferences(config.SHARED_PREF, 0);
        String regId = pref.getString("regId", null);

        Log.e(TAG, "Firebase reg id: " + regId);


        if (!TextUtils.isEmpty(regId))
            txtRegId.setText("Firebase Reg Id: " + regId);
        else
            txtRegId.setText("Firebase Reg Id is not received yet!");
    }

    private void fetchWelcome() {
        mWelcomeTextView.setText(firebaseRemoteConfig.getString("fetch remote welcome"));

        long cacheExpiration = 3600;
        if (firebaseRemoteConfig.getInfo().getConfigSettings().isDeveloperModeEnabled()) {
            cacheExpiration = 0;
        }

        firebaseRemoteConfig.fetch(cacheExpiration)
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(PriofileActivity.this, "Fetch Succeeded", Toast.LENGTH_SHORT).show();
                            firebaseRemoteConfig.activateFetched();
                        } else {
                            Toast.makeText(PriofileActivity.this, "Fetch Failed", Toast.LENGTH_SHORT).show();
                        }
                        displayWelcomeMessage();
                    }
                });
    }

    private void displayWelcomeMessage() {
        // [START get_config_values]
        String welcomeMessage = firebaseRemoteConfig.getString(WELCOME_MESSAGE_KEY);
        // [END get_config_values]
        if (firebaseRemoteConfig.getBoolean(WELCOME_MESSAGE_CAPS_KEY)) {
            mWelcomeTextView.setAllCaps(true);
        } else {
            mWelcomeTextView.setAllCaps(false);
        }
        mWelcomeTextView.setText(welcomeMessage);
    }

    @Override
    protected void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver, new IntentFilter(config.REGISTRATION_COMPLETE));
        NotificationUtils.clearNotifications(getApplicationContext());
    }

    @Override
    protected void onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);
        super.onPause();
    }

    @Override
    public void onClick(View v) {
        if (v == loguotButton) {
            firebaseAuth.signOut();
            finish();
            startActivity(new Intent(this, LoginActivity.class));
        } else if (v == fetchButton) {
            fetchWelcome();
        }
    }
}
