package com.example.user.firebaseauthapp.activity;

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

import com.example.user.firebaseauthapp.BuildConfig;
import com.example.user.firebaseauthapp.R;
import com.example.user.firebaseauthapp.model.UserModel;
import com.example.user.firebaseauthapp.utils.NotificationUtils;
import com.example.user.firebaseauthapp.utils.config;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.crash.FirebaseCrash;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;

import java.io.FileInputStream;
import java.io.IOException;

public class ProfileActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView userEmail;
    private Button loguotButton;
    private FirebaseAuth firebaseAuth;
    private FirebaseRemoteConfig firebaseRemoteConfig;
    private TextView txtmessage;
    private TextView txtRegId;
    private BroadcastReceiver mRegistrationBroadcastReceiver;
    private static final String TAG = ProfileActivity.class.getSimpleName();
    private TextView mWelcomeTextView;
    private Button fetchButton;
    private DatabaseReference mDatabaseTable;

    private static final String LOADING_PHRASE_CONFIG_KEY = "loading_phrase";
    private static final String WELCOME_MESSAGE_KEY = "welcome_message";
    private static final String WELCOME_MESSAGE_CAPS_KEY = "welcome_message_caps";
    private Button updateDataButton;
    private FirebaseDatabase mInstance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        txtRegId = (TextView) findViewById(R.id.reg_id);
        txtmessage = (TextView) findViewById(R.id.push_message);
        mWelcomeTextView = (TextView) findViewById(R.id.welcomeTextView);
        fetchButton = (Button) findViewById(R.id.fetchButton);
        updateDataButton = (Button) findViewById(R.id.updateDataButton);

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

        System.out.println(user.getDisplayName());
        System.out.println(user.getEmail());
        System.out.println(user.getUid());
        System.out.println(user.getProviderData());

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

        mInstance = FirebaseDatabase.getInstance();

        // store app title to 'app_title' node
        mInstance.getReference("app_title").setValue("Realtime Database");
        mDatabaseTable = mInstance.getReference("users");
        // Read from the database
        mDatabaseTable.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                if (dataSnapshot != null) {
                    for (DataSnapshot userlist: dataSnapshot.getChildren()){
                        UserModel user = userlist.getValue(UserModel.class);
                        System.out.println( "User name: " + user.getName() + ", email " + user.getEmail());
                    }
                } else Toast.makeText(ProfileActivity.this,"No data found", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });

        userEmail = (TextView) findViewById(R.id.userEmail);
        userEmail.setText("Welcome " + user.getEmail());
        loguotButton = (Button) findViewById(R.id.logoutButton);

        loguotButton.setOnClickListener(this);
        fetchButton.setOnClickListener(this);
        updateDataButton.setOnClickListener(this);
    }

    private void database() {
        mDatabaseTable.orderByChild("email").equalTo("awon@gmail.com").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists()) {
                    //create new user
                    String userId = mDatabaseTable.push().getKey();
                    System.out.println(userId);
                    UserModel user = new UserModel("awon zaman", "awon@gmail.com");
                    // pushing user to 'users' node using the userId
                    mDatabaseTable.child(userId).setValue(user);
                } else {
                    Toast.makeText(ProfileActivity.this,"Email already exists",Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d(TAG, databaseError.getMessage()); //Don't ignore errors!
            }
        });
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
                            Toast.makeText(ProfileActivity.this, "Fetch Succeeded", Toast.LENGTH_SHORT).show();
                            firebaseRemoteConfig.activateFetched();
                        } else {
                            Toast.makeText(ProfileActivity.this, "Fetch Failed", Toast.LENGTH_SHORT).show();
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
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        } else if (v == fetchButton) {
            fetchWelcome();
        } else if (v == updateDataButton) {
            database();
        }
    }
}
