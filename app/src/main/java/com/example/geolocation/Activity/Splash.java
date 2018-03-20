package com.example.geolocation.Activity;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.crashlytics.android.Crashlytics;
import com.example.geolocation.Helper.Preference;
import com.example.geolocation.R;
import com.google.firebase.FirebaseApp;
import com.google.firebase.iid.FirebaseInstanceId;
import io.fabric.sdk.android.Fabric;

public class Splash extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        setContentView(R.layout.activity_splash);

        FirebaseApp.initializeApp(getApplicationContext());
        Preference.getInstance(getApplicationContext()).setFcmid(FirebaseInstanceId.getInstance().getToken());

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                verifyLogin();
            }
        },2000);
    }

    private void verifyLogin() {
        if(Preference.getInstance(getApplicationContext()).getNumber().isEmpty())
            callLogin();
        else
            callHome();
    }

    private void callLogin() {
        Intent i = new Intent(Splash.this, Login.class);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(i);
        finish();
    }

    private void callHome() {
        Intent i = new Intent(Splash.this, Home.class);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(i);
        finish();
    }
}
