package com.example.user.firebaseauthapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by User on 8/17/2017.
 */

public class MessagingService extends FirebaseMessagingService {
    private static final String TAG = MessagingService.class.getSimpleName();

    private NotificationUtils mNotificationUtils;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Log.e(TAG, "From: " + remoteMessage.getFrom());

        if (remoteMessage == null)
            return;

        //Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            Log.e(TAG, "Notification Body: " + remoteMessage.getNotification().getBody());
            handleNotification(remoteMessage.getNotification().getBody());
        }

        //Check if message contains a notification payload
        if (remoteMessage.getData().size() > 0) {
            Log.e(TAG, "Data Payload: " + remoteMessage.getData().toString());

            try {
                JSONObject json = new JSONObject(remoteMessage.getData().toString());
                handleMessage(json);
            } catch (Exception e) {
                Log.e(TAG, "Exception: " + e.getMessage());
            }
        }
    }

    private void handleNotification(String message) {
        if (!NotificationUtils.isAppIsInBackground(getApplicationContext())) {
            Intent pushNotification = new Intent(config.PUSH_NOTIFICATION);
            pushNotification.putExtra("message", message);
            LocalBroadcastManager.getInstance(this).sendBroadcast(pushNotification);

            //play notificaiotn sound
            /*NotificationUtils mNotificationUtils=new NotificationUtils(getApplicationContext());
            mNotificationUtils.playNotificationSound();*/
        }
    }

    private void handleMessage(JSONObject json) {
        Log.e(TAG, "push json: " + json.toString());
        try {
            JSONObject data = json.getJSONObject("data");

            String title = data.getString("title");
            String message = data.getString("message");
            boolean isBackground = data.getBoolean("is_background");
            String imageUrl = data.getString("image");
            String timeStamp = data.getString("timestamp");
            JSONObject payload = data.getJSONObject("payload");

            Log.e(TAG, "title: " + title);
            Log.e(TAG, "message: " + message);
            Log.e(TAG, "isBackground: " + isBackground);
            Log.e(TAG, "imageUrl: " + imageUrl);
            Log.e(TAG, "timeStamp: " + timeStamp);
            Log.e(TAG, "payload: " + payload.toString());

            if (!NotificationUtils.isAppIsInBackground(getApplicationContext())) {
                //app is in foreground, broadcast the push message
                Intent pushNotification = new Intent(config.PUSH_NOTIFICATION);
                pushNotification.putExtra("message", message);
                LocalBroadcastManager.getInstance(this).sendBroadcast(pushNotification);
            } else {
                Intent resultIntent = new Intent(getApplicationContext(), ProfileActivity.class);
                resultIntent.putExtra("message", message);

                //check for image attachment
                if (TextUtils.isEmpty(imageUrl)) {
                    showNotificationMessage(getApplicationContext(), title, message, timeStamp, resultIntent);
                } else {
                    showNotificationWithBigImage(getApplicationContext(), title, message, timeStamp, resultIntent, imageUrl);
                }

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void showNotificationWithBigImage(Context context, String title, String message, String timeStamp, Intent intent, String imageUrl) {
        mNotificationUtils=new NotificationUtils(context);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
        mNotificationUtils.showNotificationMessage(title,message,timeStamp,intent,imageUrl);
    }


    private void showNotificationMessage(Context context, String title, String message, String timeStamp, Intent intent) {
        mNotificationUtils = new NotificationUtils(context);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
        mNotificationUtils.showNotificationMessage(title,message,timeStamp,intent);
    }

    @Override
    public void onNewToken(String s) {
        super.onNewToken(s);
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();

        storeRegIdInPref(refreshedToken);

        sendRegistrationToServer(refreshedToken);

        Intent registrationComplete = new Intent(config.REGISTRATION_COMPLETE);
        registrationComplete.putExtra("token", refreshedToken);
        LocalBroadcastManager.getInstance(this).sendBroadcast(registrationComplete);
    }

    private void sendRegistrationToServer(final String token) {
        Log.e(TAG, "sendRegistrationToServer: " + token);
    }

    private void storeRegIdInPref(String token) {
        //String Token=FirebaseInstanceId.getInstance().getToken();
        SharedPreferences pref=getApplicationContext().getSharedPreferences(config.SHARED_PREF,0);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("regId",token);
        editor.commit();
    }
}
