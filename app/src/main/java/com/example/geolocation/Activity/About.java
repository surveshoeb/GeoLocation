package com.example.geolocation.Activity;

import android.os.Build;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import com.example.geolocation.R;

public class About extends AppCompatActivity {

    private void setToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            toolbar.setTitleTextColor(getColor(R.color.colorPrimary));
        else
            toolbar.setTitleTextColor(getResources().getColor(R.color.colorPrimary));

        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            actionBar.setHomeAsUpIndicator(getDrawable(R.drawable.back));
        else
            actionBar.setHomeAsUpIndicator(getResources().getDrawable(R.drawable.back));

        actionBar.setTitle("About us");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        setToolbar();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
