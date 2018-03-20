package com.example.geolocation.Activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.adevole.customresources.CustomButton;
import com.example.geolocation.Helper.Database;
import com.example.geolocation.Helper.Preference;
import com.example.geolocation.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class AddMeeting extends AppCompatActivity implements View.OnClickListener, OnMapReadyCallback{

    LatLng currentLocation, selectedLocation;
    String id;

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

        actionBar.setTitle(getString(R.string.meeting_place));
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_meeting);

        Bundle extras = getIntent().getExtras();
        if (extras != null)
            id = extras.getString("id", "");
        else
            return;

        setToolbar();

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        currentLocation = new LatLng(Preference.getInstance(getApplicationContext()).getLatitude(),
                Preference.getInstance(getApplicationContext()).getLongitude());

        selectedLocation = currentLocation;

        CustomButton connectButton = findViewById(R.id.connect_invite);
        connectButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.connect_invite:
                Database.getInstance(getApplicationContext()).setMeetingLocation(id, selectedLocation);
                Intent intent = new Intent(AddMeeting.this, Home.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                break;
        }
    }

    @Override
    public void onMapReady(final GoogleMap googleMap) {
        googleMap.addMarker(new MarkerOptions().position(currentLocation)
                .icon(bitmapDescriptorFromVector(getApplicationContext(), R.drawable.drop_location)).draggable(true).title("Drag to select Location")).showInfoWindow();
        moveCamera(googleMap, currentLocation);

        if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        googleMap.setMyLocationEnabled(true);
        googleMap.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {
            @Override
            public void onMarkerDragStart(Marker marker) {

            }

            @Override
            public void onMarkerDrag(Marker marker) {

            }

            @Override
            public void onMarkerDragEnd(Marker marker) {
                selectedLocation = marker.getPosition();
                moveCamera(googleMap, selectedLocation);
            }
        });

    }

    private void moveCamera(GoogleMap googleMap, LatLng location) {
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 15));
        googleMap.animateCamera(CameraUpdateFactory.zoomIn());
        googleMap.animateCamera(CameraUpdateFactory.zoomTo(15), 2000, null);
    }

    private BitmapDescriptor bitmapDescriptorFromVector(Context context, int vectorResId) {
        Drawable vectorDrawable = ContextCompat.getDrawable(context, vectorResId);
        vectorDrawable.setBounds(0, 0, vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight());
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }
}
