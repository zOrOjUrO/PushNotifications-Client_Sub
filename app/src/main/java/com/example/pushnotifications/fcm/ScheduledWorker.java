package com.example.pushnotifications.fcm;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.example.pushnotifications.R;
import com.example.pushnotifications.util.NotificationUtil;
import com.google.firebase.messaging.RemoteMessage;

import org.jetbrains.annotations.NotNull;


public class ScheduledWorker extends Worker{

    @NonNull
    public Result doWork() {
        Log.d("ScheduledWorker", "Work START");

        Log.i("STRING CHECK", this.getApplicationContext().getResources().getString(R.string.NOTIFICATION_TITLE));
        String title = this.getInputData().getString(this.getApplicationContext().getResources().getString(R.string.NOTIFICATION_TITLE));
        String message = this.getInputData().getString(this.getApplicationContext().getResources().getString(R.string.NOTIFICATION_MESSAGE));
        String image = this.getInputData().getString(this.getApplicationContext().getResources().getString(R.string.NOTIFICATION_IMG));

        assert message != null;
        new NotificationUtil(this.getApplicationContext()).showNotification(title, message, image);

        Log.d("ScheduledWorker", "Work DONE");
        return Result.success();
    }

    public ScheduledWorker(@NotNull Context appContext, @NotNull WorkerParameters workerParams) {
        super(appContext, workerParams);
    }

}