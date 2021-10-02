package com.example.moneytracker;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {

        showNotification(remoteMessage.getNotification().getBody());
//        super.onMessageReceived(remoteMessage);
//        getFirebaseMessage(remoteMessage.getNotification().getTitle(), remoteMessage.getNotification().getBody());

    }

    public void showNotification(String message){

        PendingIntent pi = PendingIntent.getActivity(this,0,new Intent(this,SpendingActivity.class),0);
        Notification notification = new NotificationCompat.Builder(this)
        //NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "myFirebasechannel")
                .setSmallIcon(R.drawable.ic_outline_monetization_on_24)
                .setContentTitle("Money Tracker")
                .setContentText(message)
                .setContentIntent(pi)
                .setAutoCancel(true)
                .build();

       // NotificationManagerCompat manager = NotificationManagerCompat.from(this);

        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.notify(0,notification);
    }
}
