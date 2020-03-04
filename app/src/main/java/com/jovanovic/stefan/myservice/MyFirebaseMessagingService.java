package com.jovanovic.stefan.myservice;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.jovanovic.stefan.notificationtest.R;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    String CHANNEL_ID = "2";
    String image_url = null;
    Bitmap image_bitmap = null;

    public MyFirebaseMessagingService() {
    }

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        if(remoteMessage.getNotification() != null){
            //Get Image from Firebase
            if(remoteMessage.getNotification().getImageUrl() != null){
                image_url = remoteMessage.getNotification().getImageUrl().toString();
                image_bitmap = getBitmapFromURL(image_url);
            }

            //Create and Display Notificaiton
            showNotification(remoteMessage.getNotification().getTitle(),
                        remoteMessage.getNotification().getBody());
        }
        if(!remoteMessage.getData().isEmpty()){
            Map<String, String> myData = remoteMessage.getData();
            Log.d("MYDATA", myData.get("key1"));
            Log.d("MYDATA", myData.get("key2"));
        }
    }

    @Override
    public void onNewToken(@NonNull String s) {
        super.onNewToken(s);
    }

    private void showNotification(String title, String text) {

        //Create Notification Channel
        createNotificationChannel();

        NotificationCompat.Builder builder = new NotificationCompat.Builder(
                this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_chat)
                .setContentTitle(title)
                .setContentText(text)
                .setLargeIcon(image_bitmap)
                .setAutoCancel(true)
                .setColor(getResources().getColor(R.color.colorAccent))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.
                from(this);
        //Notification ID is unique for each notification you create
        notificationManagerCompat.notify(2,builder.build());
    }

    private Bitmap getBitmapFromURL(String image_url) {

        try {
            URL url = new URL(image_url);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();

            InputStream inputStream = connection.getInputStream();
            return BitmapFactory.decodeStream(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private void createNotificationChannel(){
        //Create Notification channel only on API Level 26+
        //NotificationChannel is a new Class and not in a support library
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            String name = "My Channel Name2";
            String description = "My Channel description2";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;

            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            //Register the channel with the system.
            //You cannot change importance or other notification behaviors after this
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }
    }
}
