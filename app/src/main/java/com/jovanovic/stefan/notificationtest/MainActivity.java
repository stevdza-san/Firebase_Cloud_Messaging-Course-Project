package com.jovanovic.stefan.notificationtest;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.firebase.messaging.FirebaseMessaging;

public class MainActivity extends AppCompatActivity {

    EditText cookies;
    Button buy_btn;

    Uri defaultNotificationSound;

    String CHANNEL_ID = "1";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Create Notification channel if device is using API 26+
        createNotificationChannel();

        //Get my Registration Token
        myRegistrationToken();

        cookies = findViewById(R.id.cookies);
        buy_btn = findViewById(R.id.buy_btn);
        buy_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String numberOfCookies = cookies.getText().toString();

                //Subscribe to Topic - Firebase Cloud Messaging
                subscribeToDiscount(Integer.parseInt(numberOfCookies));

                Intent intent = new Intent(MainActivity.this, SecondActivity.class);
                intent.putExtra("cookie", numberOfCookies);
                PendingIntent pendingIntent = PendingIntent.getActivity(MainActivity.this,
                        0,
                        intent,
                        PendingIntent.FLAG_UPDATE_CURRENT);

                Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.cookies);

                defaultNotificationSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

                NotificationCompat.Builder builder = new NotificationCompat.Builder(
                        MainActivity.this, CHANNEL_ID)
                        .setSmallIcon(R.drawable.ic_chat)
                        .setContentTitle("Cookies")
                        .setContentText("You just bought " + numberOfCookies + " Cookies!")
                        .setContentIntent(pendingIntent)
                        .setAutoCancel(true)
                        .setLargeIcon(bitmap)
                        .setSound(defaultNotificationSound)
                        .setLights(Color.GREEN, 500, 200)
                        .setVibrate(new long[] { 0, 250, 250, 250})
                        .addAction(R.mipmap.ic_launcher, "Get BONUS!", pendingIntent)
                        .setColor(getResources().getColor(R.color.colorAccent))
                        .setPriority(NotificationCompat.PRIORITY_HIGH);

                NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.
                        from(MainActivity.this);
                //Notification ID is unique for each notification you create
                notificationManagerCompat.notify(1,builder.build());
            }
        });

    }

    public void createNotificationChannel(){
        //Create Notification channel only on API Level 26+
        //NotificationChannel is a new Class and not in a support library
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            String name = "My Channel Name";
            String description = "My Channel description";
            int importance = NotificationManager.IMPORTANCE_HIGH;

            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setVibrationPattern(new long[]{ 0, 250, 250, 250});
            //Register the channel with the system.
            //You cannot change importance or other notification behaviors after this
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }
    }

    void myRegistrationToken(){
        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if (!task.isSuccessful()) {
                            Log.w("TAG", "getInstanceId failed", task.getException());
                            return;
                        }
                        // Get new Instance ID token
                        String token = task.getResult().getToken();
                        // Log and toast
                        Toast.makeText(MainActivity.this, token, Toast.LENGTH_SHORT).show();
                        Log.d("Token", token);
                    }
                });
    }

    void subscribeToDiscount(int cookies){
        if(cookies <= 50){
            FirebaseMessaging.getInstance().subscribeToTopic("small_discount")
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(!task.isSuccessful()){
                        Toast.makeText(MainActivity.this, "Failed to Subscribe to Small Discount", Toast.LENGTH_SHORT).show();
                    }else{
                        Toast.makeText(MainActivity.this, "Successfully Subscribed to Small Discount!", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }else {
            FirebaseMessaging.getInstance().subscribeToTopic("huge_discount")
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(!task.isSuccessful()){
                                Toast.makeText(MainActivity.this, "Failed to Subscribe to Huge Discount", Toast.LENGTH_SHORT).show();
                            }else{
                                Toast.makeText(MainActivity.this, "Successfully Subscribed to Huge Discount!", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }

}
