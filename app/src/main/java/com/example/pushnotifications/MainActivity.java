package com.example.pushnotifications;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.FirebaseApp;
import com.google.firebase.messaging.FirebaseMessaging;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        FirebaseApp.initializeApp(getApplicationContext());
    }

    /*
    public void scheduleNotif(View view) {
        Snackbar.make(view.getRootView(), "Scheduling Notification...", BaseTransientBottomBar.LENGTH_SHORT).show();
    }
     */

    public void unsubscribe(View view) {
        //unsubscribing from the topic
        FirebaseMessaging.getInstance().unsubscribeFromTopic(
                getString(R.string.default_notification_channel_id))
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        String msg = "UnSubscribed Successfully";
                        if (!task.isSuccessful()) {
                            msg = "Transaction Failed";
                        }
                        Log.d("STATUS", msg);
                        Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public void subscribe(View view) {
        //subscribing to the topic
        FirebaseMessaging.getInstance().subscribeToTopic(
                getString(R.string.default_notification_channel_id))
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        String msg = "Subscribed Successfully";
                        if (!task.isSuccessful()) {
                            msg = "Subscription Failed";
                        }
                        Log.d("STATUS", msg);
                        Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
                    }
                });
    }
}