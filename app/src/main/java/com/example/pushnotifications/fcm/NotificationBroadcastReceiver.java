package com.example.pushnotifications.fcm;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import com.example.pushnotifications.R;

public class NotificationBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if(intent != null){
            String title = intent.getStringExtra(context.getResources().getString(R.string.NOTIFICATION_TITLE));
            String message = intent.getStringExtra(context.getResources().getString(R.string.NOTIFICATION_MESSAGE));
            String image = intent.getStringExtra(context.getResources().getString(R.string.NOTIFICATION_IMG));

            Data notificationData = (new Data.Builder())
                    .putString(context.getResources().getString(R.string.NOTIFICATION_TITLE), title)
                    .putString(context.getResources().getString(R.string.NOTIFICATION_MESSAGE), message)
                    .putString(context.getResources().getString(R.string.NOTIFICATION_IMG), image)
                    .build();

            OneTimeWorkRequest work = new OneTimeWorkRequest.Builder(ScheduledWorker.class)
                    .setInputData(notificationData)
                    .build();

            WorkManager.getInstance().beginWith(work).enqueue();
            Log.d(this.getClass().getName(), "WorkManager is Enqueued.");
            Log.d("Notification: ", title + " " + message + " " + image);
        }
    }
}
