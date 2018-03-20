package com.example.geolocation.Activity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.RelativeLayout;

import com.example.geolocation.Helper.Preference;
import com.example.geolocation.R;

import java.io.ByteArrayOutputStream;
import java.util.List;

public class Invite extends AppCompatActivity implements View.OnClickListener{

    private void initCreate() {
        RelativeLayout smsLayout = findViewById(R.id.sms_share);
        RelativeLayout mailLayout = findViewById(R.id.mail_share);
        RelativeLayout fbLayout = findViewById(R.id.fb_share);
        RelativeLayout otherLayout = findViewById(R.id.other_share);
        smsLayout.setOnClickListener(this);
        mailLayout.setOnClickListener(this);
        fbLayout.setOnClickListener(this);
        otherLayout.setOnClickListener(this);
    }

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

        actionBar.setTitle(getString(R.string.spread_connection));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invite);

        initCreate();
        setToolbar();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.sms_share:
                smsShare();
                break;
            case R.id.mail_share:
                mailShare();
                break;
            case R.id.fb_share:
                shareOn("facebook");
                break;
            case R.id.other_share:
                otherShare();
                break;
        }
    }

    private void smsShare() {
        Uri smsUri = Uri.parse("smsto:");
        Intent smsIntent = new Intent(android.content.Intent.ACTION_SENDTO, smsUri);
        String playStoreLink = " Download App here https://play.google.com/store/apps/details?id=" + Invite.this.getPackageName();
        smsIntent.putExtra("sms_body",getString(R.string.share_message) +" "+ Preference.getInstance(getApplicationContext()).getInviteCode() + playStoreLink);
        startActivity(smsIntent);
    }

    private void shareOn(String appName) {
        Intent shareIntent = new Intent(android.content.Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        String playStoreLink = " Download App here https://play.google.com/store/apps/details?id=" + Invite.this.getPackageName();
        shareIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.share_message)+" "+ Preference.getInstance(getApplicationContext()).getInviteCode() + playStoreLink);
        shareIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, getString(R.string.app_name));

        PackageManager pm = getApplicationContext().getPackageManager();
        List<ResolveInfo> activityList = pm.queryIntentActivities(shareIntent, 0);
        for (final ResolveInfo app : activityList) {
            if ((app.activityInfo.name).contains(appName)) {
                final ActivityInfo activity = app.activityInfo;
                final ComponentName name = new ComponentName(activity.applicationInfo.packageName, activity.name);
                shareIntent.addCategory(Intent.CATEGORY_LAUNCHER);
                shareIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
                shareIntent.setComponent(name);
                startActivity(shareIntent);
                break;
            }
        }
    }

    private void mailShare() {
        Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                "mailto","yourfriend@gmail.com", null));
        String playStoreLink = "  Download App here https://play.google.com/store/apps/details?id=" + Invite.this.getPackageName();
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Connect to GeoLocation");
        emailIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.share_message)+ " "+Preference.getInstance(getApplicationContext()).getInviteCode() +playStoreLink);
        startActivity(Intent.createChooser(emailIntent, "Send email..."));
    }

    private void otherShare() {
        Intent shareIntent = new Intent(android.content.Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        String playStoreLink = "  Download App here https://play.google.com/store/apps/details?id=" + Invite.this.getPackageName();
        shareIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.share_message)+ " "+Preference.getInstance(getApplicationContext()).getInviteCode()  + playStoreLink);
        startActivity(Intent.createChooser(shareIntent, "Share on..."));
    }
}
