package com.example.pushnotifications.util;

import static java.lang.Math.abs;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import com.example.pushnotifications.MainActivity;
import com.example.pushnotifications.R;
import com.google.firebase.messaging.RemoteMessage;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Objects;
import java.util.Random;


public class NotificationUtil {
    private final Context context;
    private final boolean isPeerNotification;
    private final RemoteMessage notificationPayload;

    private String getTitle(){
        //Log.d("In GetTitle: ", "isPeer: " + isPeerNotification);
        if(this.isPeerNotification)
            return this.notificationPayload.getData().get("title");
        return this.notificationPayload.getNotification().getTitle();
    }

    private String getBody(){
        if(this.isPeerNotification)
            return this.notificationPayload.getData().get("message");
        return this.notificationPayload.getNotification().getBody();
    }

    private Bitmap getImage() throws MalformedURLException {
        Uri uri = null;
        try {
            uri = this.notificationPayload.getNotification().getImageUrl();
        } catch (NullPointerException e) {
            Log.d("Notification ImageURL", "Empty");
        }

        String peerTemp = this.notificationPayload.getData().get("media");
        URL imageURL = null;
        if (uri != null) imageURL = new URL(uri.toString());
        if (peerTemp != null) imageURL = new URL(peerTemp);
        Bitmap image = null;
        try {
            image = BitmapFactory.decodeStream(imageURL != null ? imageURL.openConnection().getInputStream() : null);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return image;
    }

    public final void showNotification() throws IOException {
        Intent intent = new Intent(this.context, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        @SuppressLint("UnspecifiedImmutableFlag") PendingIntent pendingIntent = PendingIntent.getActivity(this.context,
                0, intent, PendingIntent.FLAG_ONE_SHOT);

        String channelId = this.context.getString(R.string.default_notification_channel_id);
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this.context, channelId)
                .setColor(ContextCompat.getColor(context, R.color.notification_title_col))
                .setSmallIcon(R.drawable.ic_stat_ic_notification)
                .setContentTitle(getTitle())
                .setContentText(getBody())
                .setAutoCancel(true)
                .setPriority(this.notificationPayload != null ? notificationPayload.getPriority() : NotificationCompat.PRIORITY_MAX)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent)
                .setVisibility(NotificationCompat.VISIBILITY_PRIVATE);
        Bitmap notifImg = getImage();
        if(notifImg != null) notificationBuilder.setLargeIcon(notifImg);
        
        NotificationManager notificationManager = (NotificationManager)
                context.getSystemService(Context.NOTIFICATION_SERVICE);

        NotificationChannel channel = new NotificationChannel(
                channelId,
                "Default Channel",
                NotificationManager.IMPORTANCE_HIGH
        );

        notificationManager.createNotificationChannel(channel);
        notificationManager.notify( abs((new Random()).nextInt()), notificationBuilder.build());
    }

    public void showNotification(String title, String message, String imageURL) {

        Intent intent = new Intent(this.context, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        @SuppressLint("UnspecifiedImmutableFlag") PendingIntent pendingIntent = PendingIntent.getActivity(this.context,
                0, intent, PendingIntent.FLAG_ONE_SHOT);

        String channelId = this.context.getString(R.string.default_notification_channel_id);
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this.context, channelId)
                .setColor(ContextCompat.getColor(context, R.color.notification_title_col))
                .setSmallIcon(R.drawable.ic_stat_ic_notification)
                .setContentTitle(title)
                .setContentText(message)
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent)
                .setVisibility(NotificationCompat.VISIBILITY_PRIVATE);

        Bitmap image = null;
        try {
            if(imageURL != null ) image = BitmapFactory.decodeStream((new URL(imageURL)).openConnection().getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
        if(image != null) notificationBuilder.setLargeIcon(image);

        NotificationManager notificationManager = (NotificationManager)
                context.getSystemService(Context.NOTIFICATION_SERVICE);

        NotificationChannel channel = new NotificationChannel(
                channelId,
                "Default Channel",
                NotificationManager.IMPORTANCE_HIGH
        );

        notificationManager.createNotificationChannel(channel);
        notificationManager.notify( abs((new Random()).nextInt()), notificationBuilder.build());

    }


    public NotificationUtil(@NotNull Context context, RemoteMessage notificationPayload, boolean isPeerNotification) {
        this.context = context;
        this.isPeerNotification = isPeerNotification;
        this.notificationPayload = notificationPayload;
    }

    public NotificationUtil(@NotNull Context context) {
        this.context = context;
        this.isPeerNotification = true;
        this.notificationPayload = null;
    }


}
