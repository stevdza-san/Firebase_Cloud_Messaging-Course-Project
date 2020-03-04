package com.jovanovic.stefan.notificationtest;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

public class SecondActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);

        String numberOfCookies = getIntent().getStringExtra("cookie");

        int cookies = Integer.parseInt(numberOfCookies);
        if(cookies < 50){
            Toast.makeText(this, "You get small bonus.", Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(this, "You get HUGE bonus!", Toast.LENGTH_SHORT).show();
        }
    }


}
