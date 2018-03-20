package com.example.geolocation.Activity;

import android.os.Build;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.example.geolocation.Adapter.ConnectedAdapter;
import com.example.geolocation.Model.Users;
import com.example.geolocation.R;

import io.realm.Realm;
import io.realm.RealmResults;

public class ConnectedUsers extends AppCompatActivity {

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

        actionBar.setTitle(getString(R.string.connected_users));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connected_users);

        setToolbar();

        RecyclerView  recyclerView = (RecyclerView)findViewById(R.id.recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        Realm.init(getApplicationContext());
        Realm realm = Realm.getDefaultInstance();
        RealmResults<Users> users = realm.where(Users.class).findAll();
        recyclerView.setAdapter(new ConnectedAdapter(getApplicationContext(), users));
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
