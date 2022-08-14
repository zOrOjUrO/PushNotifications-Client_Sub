package com.example.pushnotifications.fcm;


import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.Image;
import android.util.Log;

import com.example.pushnotifications.R;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.example.pushnotifications.util.NotificationUtil;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;



public class MyFirebaseMessagingService extends FirebaseMessagingService {


    public void onMessageReceived(@NotNull RemoteMessage remoteMessage) {
        if(!remoteMessage.getData().isEmpty()){
            Log.d("MyFirebaseMsgService",
                    "Message data payload: " + remoteMessage.getData());
            // Get Message details
            // Check that 'Automatic Date and Time' settings are turned ON.
            // If it's not turned on, Return
            if (android.provider.Settings.Global.getInt(getContentResolver()
                    , android.provider.Settings.Global.AUTO_TIME, 0) == 0) {
                Log.d("MyFirebaseMsgService", "`Automatic Date and Time` is not enabled");
                return;
            }

            boolean isPeerNotification = true;
            if (remoteMessage.getNotification() != null) {
                isPeerNotification = false;
                Log.d("Notification: ", "Sent by FCM console");
                //Log.d("Received: ", "Title: " + remoteMessage.getNotification().getTitle()
                // + " Message: " + remoteMessage.getNotification().getBody());
            }
            else Log.d("Notification: ", "Sent by Peer");

            boolean isScheduled = Boolean.parseBoolean(remoteMessage.getData().get("isScheduled"));
            Log.d("isScheduled:", String.valueOf(isScheduled));
            if(isScheduled){
                String scheduledTime = remoteMessage.getData().get("scheduledTime");
                scheduleAlarm(scheduledTime, remoteMessage, isPeerNotification);
            }
            else{
                showNotification(remoteMessage, isPeerNotification);
            }
        }
    }

    private void scheduleAlarm(String scheduledTimeString, RemoteMessage payload, boolean isPeerNotification)
    {

        String title;
        if(isPeerNotification)
            title = (payload.getData().get("title"));
        else
            title = payload.getNotification().getTitle();

        String message;
        if(isPeerNotification)
            message = (payload.getData().get("message"));
        else
            message = payload.getNotification().getBody();

        String image;
        if(isPeerNotification)
            image = payload.getData().get("media");
        else
            image = payload.getNotification().getImageUrl().toString();

        AlarmManager alarmManager = (AlarmManager) this.getApplicationContext().getSystemService(Context.ALARM_SERVICE);
        Intent alarmIntent = new Intent(this.getApplicationContext(), NotificationBroadcastReceiver.class)
                .putExtra(this.getResources().getString(R.string.NOTIFICATION_TITLE), title)
                .putExtra(this.getResources().getString(R.string.NOTIFICATION_MESSAGE), message)
                .putExtra(this.getResources().getString(R.string.NOTIFICATION_IMG), image);
        PendingIntent alarm = PendingIntent.getBroadcast(this.getApplicationContext(), 0, alarmIntent, 0);
        Date scheduledTime = null;
        try {
           scheduledTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).parse(scheduledTimeString);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if (scheduledTime != null && scheduledTime.getTime() > System.currentTimeMillis() ) {
            Log.d("FCMService: Notification Scheduled || Notification: ", title + " " + message);
            alarmManager.set(AlarmManager.RTC_WAKEUP, scheduledTime.getTime(), alarm);
        }
        else
            showNotification(payload, isPeerNotification);
    }

    private void showNotification(RemoteMessage payload, boolean isPeerNotification) {
        try {
            (new NotificationUtil(this.getApplicationContext(), payload, isPeerNotification)).showNotification();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void onNewToken(@NotNull String token) {
        Log.d("MyFirebaseMsgService", "Refreshed token: " + token);
    }
}
