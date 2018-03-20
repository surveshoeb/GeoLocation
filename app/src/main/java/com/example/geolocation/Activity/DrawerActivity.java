package com.example.geolocation.Activity;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MenuItem;
import android.widget.FrameLayout;

import com.example.geolocation.R;

public class DrawerActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout mDrawerLayout;

    private ActionBarDrawerToggle mToggle;

    protected FrameLayout container;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drawer);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer);
        container = findViewById(R.id.container);
        mToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.open, R.string.close);
        mDrawerLayout.addDrawerListener(mToggle);
        mToggle.syncState();

        NavigationView navigationView = findViewById(R.id.navigation_view);
        navigationView.setNavigationItemSelectedListener(this);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mToggle.onOptionsItemSelected(item))
            return true;
        return super.onOptionsItemSelected(item);


    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.cu:
                Intent intentcu = new Intent(DrawerActivity.this, ConnectedUsers.class);
                intentcu.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intentcu);
                closeDrawer();
                break;

            case R.id.in:
                Intent intentin = new Intent(DrawerActivity.this, Invite.class);
                intentin.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intentin);
                closeDrawer();
                break;
            case R.id.au:
                Intent intentab = new Intent(DrawerActivity.this, About.class);
                intentab.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intentab);
                closeDrawer();
                break;
        }
        return false;
    }

    public void openDrawer() {
        mDrawerLayout.openDrawer(Gravity.START, true);
    }

    public void closeDrawer() {
        mDrawerLayout.closeDrawers();
    }
}

