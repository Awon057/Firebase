package com.example.user.firebaseauthapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.crash.FirebaseCrash;
import com.google.firebase.messaging.FirebaseMessaging;

import java.io.FileInputStream;
import java.io.IOException;

public class PriofileActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView userEmail;
    private Button loguotButton;
    private FirebaseAuth firebaseAuth;
    private TextView txtmessage;
    private TextView txtRegId;
    private BroadcastReceiver mRegistrationBroadcastReceiver;
    private static final String TAG = PriofileActivity.class.getSimpleName();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_priofile);

        txtRegId = (TextView)findViewById(R.id.reg_id);
        txtmessage=(TextView)findViewById(R.id.push_message);

        mRegistrationBroadcastReceiver = new BroadcastReceiver(){

            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals(config.REGISTRATION_COMPLETE)){
                    FirebaseMessaging.getInstance().subscribeToTopic(config.TOPIC_GLOBAL);
                    displayFirebaseRegId();
                } else if(intent.getAction().equals(config.PUSH_NOTIFICATION)){
                    String message=intent.getStringExtra("message");
                    Toast.makeText(getApplicationContext(),"Push notification: "+message,Toast.LENGTH_LONG).show();
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

        try {
            FileInputStream is = new FileInputStream("/non-existent/file");
            int c = is.read();
        } catch(IOException e) {
            FirebaseCrash.report(e); // Generate report
        }

        userEmail = (TextView) findViewById(R.id.userEmail);
        userEmail.setText("Welcome " + user.getEmail());
        loguotButton = (Button) findViewById(R.id.logoutButton);

        loguotButton.setOnClickListener(this);
 }

    private void displayFirebaseRegId() {
        SharedPreferences pref=getApplicationContext().getSharedPreferences(config.SHARED_PREF,0);
        String regId=pref.getString("regId",null);

        Log.e(TAG,"Firebase reg id: "+regId);


        if(!TextUtils.isEmpty(regId))
            txtRegId.setText("Firebase Reg Id: "+regId);
        else
            txtRegId.setText("Firebase Reg Id is not received yet!");
    }

    @Override
    protected void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,new IntentFilter(config.REGISTRATION_COMPLETE));
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
        }
    }
}
