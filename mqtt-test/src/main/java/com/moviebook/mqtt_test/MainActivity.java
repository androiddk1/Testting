package com.moviebook.mqtt_test;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.living.sdk.LivingSdk;
import com.living.sdk.MQTTService;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        LivingSdk.getInstance().initService(this, "mqtt");
    }
}
