package com.example.geolocation.Activity;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.VectorDrawable;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Looper;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutCompat;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.PopupWindow;

import com.adevole.customresources.CustomButton;
import com.adevole.customresources.CustomEditText;
import com.example.geolocation.Connection.APIRequest;
import com.example.geolocation.Helper.Database;
import com.example.geolocation.Helper.Preference;
import com.example.geolocation.Helper.Utility;
import com.example.geolocation.Model.Users;
import com.example.geolocation.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

import io.realm.Realm;
import io.realm.RealmResults;

import static com.example.geolocation.Helper.Preference.FCMID;
import static com.example.geolocation.Helper.Preference.LATITUDE;
import static com.example.geolocation.Helper.Preference.LONGITUDE;
import static com.example.geolocation.Helper.Preference.NAME;

public class Home extends DrawerActivity implements OnMapReadyCallback, View.OnClickListener {

    private static final String TAG = "Home";

    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private static final long UPDATE_INTERVAL_IN_MILLISECONDS = 10;
    private static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS = 1;
    GoogleMap mMap;
    private FusedLocationProviderClient mFusedLocationClient;
    Location currentLocation;
    private LocationRequest mLocationRequest;
    private LocationCallback mLocationCallback;

    Map<String, Marker> markers = new HashMap<>();

    PopupWindow mpopup;
    CustomEditText inviteEdit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LayoutInflater inflater = (LayoutInflater) this.getSystemService(LAYOUT_INFLATER_SERVICE);
        View rootView = inflater.inflate(R.layout.activity_home, null, false);
        container.addView(rootView, 0);


        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        getLastKnownLocation();

        FloatingActionButton addInvite = findViewById(R.id.add_invite);
        addInvite.setOnClickListener(this);
        FloatingActionButton sos = findViewById(R.id.sos);
        sos.setOnClickListener(this);

        ImageView menuDrawer = findViewById(R.id.menu_drawer);
        menuDrawer.setOnClickListener(this);
    }

    private void getLastKnownLocation() {
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(Home.this);
        if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            getLocationPermission();
        } else {
            mFusedLocationClient.getLastLocation()
                    .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            if (location != null) {
                                if (mMap != null) {
                                    Preference.getInstance(getApplicationContext()).setLatitude((float) location.getLatitude());
                                    Preference.getInstance(getApplicationContext()).setLongitude((float) location.getLongitude());

                                    Database.getInstance(getApplicationContext()).updateUserData();

                                    createLocationRequest();
                                    createLocationCallback();
                                    if (ActivityCompat.checkSelfPermission(Home.this, Manifest.permission.ACCESS_FINE_LOCATION)
                                            != PackageManager.PERMISSION_GRANTED &&
                                            ActivityCompat.checkSelfPermission(Home.this, Manifest.permission.ACCESS_COARSE_LOCATION) !=
                                                    PackageManager.PERMISSION_GRANTED) {
                                        getLocationPermission();
                                    } else
                                        mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
                                }
                            }
                        }
                    });
        }
    }

    @Override
    public void onMapReady(final GoogleMap googleMap) {
        mMap = googleMap;

        mMap.setBuildingsEnabled(true);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mMap.setMyLocationEnabled(true);


        LatLng mumbai = new LatLng(19.0760, 72.8777);
        CameraUpdate zoom=CameraUpdateFactory.zoomTo(15);
        mMap.animateCamera(zoom);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(mumbai));

        addMarker();

        DatabaseReference connectedUsers = Database.getInstance(getApplicationContext())
                .getSharedLocationReference(Preference.getInstance(getApplicationContext()).getNumber());

        connectedUsers.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                updateUser(dataSnapshot);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                updateUser(dataSnapshot);
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                removeUser(dataSnapshot);
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
                updateUser(dataSnapshot);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        DatabaseReference meetingLocations = Database.getInstance(getApplicationContext())
                .getMeetingLocation(Preference.getInstance(getApplicationContext()).getNumber());

        meetingLocations.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Realm.init(getApplicationContext());
                Realm realm = Realm.getDefaultInstance();
                Users user = realm.where(Users.class).equalTo("number", dataSnapshot.getKey()).findFirst();
                if (user != null) {
                    LatLng latLng = new LatLng((double) dataSnapshot.child(LATITUDE).getValue(), (double) dataSnapshot.child(LONGITUDE).getValue());
                    MarkerOptions markerOptions = new MarkerOptions()
                            .title("Meeting Place with " + user.getName())
                            .position(latLng)
                            .icon(getBitmapDescriptor(R.drawable.drop_location));
                    Marker meetingMarker = googleMap.addMarker(markerOptions);
                    meetingMarker.showInfoWindow();
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void removeUser(DataSnapshot dataSnapshot) {
        if (dataSnapshot != null) {
            DatabaseReference userReference = Database.getInstance(getApplicationContext()).getUserReferences(dataSnapshot.getKey());
            userReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    try {
                        String number = dataSnapshot.getKey();
                        Realm.init(getApplicationContext());
                        Realm realm = Realm.getDefaultInstance();
                        RealmResults<Users> results = realm.where(Users.class).equalTo("number", number).findAll();
                        realm.beginTransaction();
                        results.deleteAllFromRealm();
                        realm.commitTransaction();

                        if (markers.containsKey(number))
                            markers.remove(number);
                    }
                    catch (ClassCastException e) {

                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }

    private void updateUser(DataSnapshot dataSnapshot) {
        if (dataSnapshot != null) {

            DatabaseReference userReference = Database.getInstance(getApplicationContext()).getUserReferences(dataSnapshot.getKey());
            userReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    try {
                        String number = dataSnapshot.getKey();
                        Realm.init(getApplicationContext());
                        Realm realm = Realm.getDefaultInstance();

                        Users user = new Users();

                        String name = String.valueOf(dataSnapshot.child(NAME).getValue());
                        double lat = (double) dataSnapshot.child(LATITUDE).getValue();
                        double longi = (double) dataSnapshot.child(LONGITUDE).getValue();

                        user.setNumber(number);
                        user.setName(name);
                        user.setLatitude(lat);
                        user.setLongitude(longi);

                        realm.beginTransaction();
                        realm.copyToRealmOrUpdate(user);
                        realm.commitTransaction();

                        if (markers.containsKey(number))
                            updateMarker(number);
                        else
                            addMarker(number);
                    }
                    catch (ClassCastException e) {

                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }


            });
        }
    }

    private void addMarker() {
        Realm.init(getApplicationContext());
        Realm realm = Realm.getDefaultInstance();

        RealmResults<Users> results = realm.where(Users.class).findAll();

        LatLngBounds.Builder builder = new LatLngBounds.Builder();

        LatLng latLng = null;

        for (Users users : results) {

            latLng = new LatLng(users.getLatitude(), users.getLongitude());
            MarkerOptions markerOption = new MarkerOptions()
                    .position(latLng)
                    .title(users.getName())
                    .icon(getBitmapDescriptor(R.drawable.marker_green));

            Marker marker = mMap.addMarker(markerOption);
            marker.showInfoWindow();

            if (Preference.getInstance(getApplicationContext()).getNumber().equals(users.getNumber())) {
                mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                mMap.animateCamera(CameraUpdateFactory.zoomTo(15));
            }

            if (!markers.containsKey(users.getNumber()))
                markers.put(users.getNumber(), marker);

            builder.include(marker.getPosition());
        }
    }

    private void addMarker(String number) {
        Realm.init(getApplicationContext());
        Realm realm = Realm.getDefaultInstance();

        RealmResults<Users> results = realm.where(Users.class).equalTo("number",number).findAll();

        LatLngBounds.Builder builder = new LatLngBounds.Builder();

        LatLng latLng = null;

        for (Users users : results) {

            latLng = new LatLng(users.getLatitude(), users.getLongitude());
            MarkerOptions markerOption = new MarkerOptions()
                    .position(latLng)
                    .title(users.getName())
                    .icon(getBitmapDescriptor(R.drawable.marker_green));

            Marker marker = mMap.addMarker(markerOption);
            marker.showInfoWindow();

            if (Preference.getInstance(getApplicationContext()).getNumber().equals(users.getNumber())) {
                mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                mMap.animateCamera(CameraUpdateFactory.zoomTo(15));
            }

            if (!markers.containsKey(users.getNumber()))
                markers.put(users.getNumber(), marker);

            builder.include(marker.getPosition());
        }
    }

    private void updateMarker(String number) {
        Realm.init(getApplicationContext());
        Realm realm = Realm.getDefaultInstance();

        Users user = realm.where(Users.class).equalTo("number", number).findFirst();

        if (markers.containsKey(number)) {

            LatLng latLng = new LatLng(user.getLatitude(), user.getLongitude());
//            ((Marker) markers.get(number)).remove();
            ((Marker) markers.get(number)).setPosition(latLng);
        }
    }

    private void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(UPDATE_INTERVAL_IN_MILLISECONDS);
        mLocationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    private void createLocationCallback() {
        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);

                currentLocation = locationResult.getLastLocation();

                Preference.getInstance(getApplicationContext()).setLatitude((float) currentLocation.getLatitude());
                Preference.getInstance(getApplicationContext()).setLongitude((float) currentLocation.getLongitude());

                Database.getInstance(getApplicationContext()).updateUserData();
            }
        };
    }

    private void getLocationPermission() {

        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {

        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }

    private BitmapDescriptor getBitmapDescriptor(int id) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            VectorDrawable vectorDrawable = (VectorDrawable) getDrawable(id);

            int h = vectorDrawable.getIntrinsicHeight();
            int w = vectorDrawable.getIntrinsicWidth();

            vectorDrawable.setBounds(0, 0, w, h);

            Bitmap bm = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bm);
            vectorDrawable.draw(canvas);

            return BitmapDescriptorFactory.fromBitmap(bm);

        } else {
            return BitmapDescriptorFactory.fromResource(id);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.menu_drawer:
                openDrawer();
                break;
            case R.id.sos:
                sendSosMessage();
                break;
            case R.id.add_invite:
                addInviteWindow();
                break;
            case R.id.popup_close:
                mpopup.dismiss();
                break;
            case R.id.connect_invite:
                if (inviteEdit.getText().toString().isEmpty() || inviteEdit.getText().length() < 5
                        || inviteEdit.getText().length() > 5 ) {
                    Utility.inviteCodeError(getApplicationContext());
                }
                else {
                    validatedInviteCode(inviteEdit.getText().toString());
                }
                break;
        }
    }

    private void sendSosMessage() {
        DatabaseReference sharedDatabaseReference = Database.getInstance(getApplicationContext()).getSharedLocationReference(Preference.getInstance(getApplicationContext()).getNumber());
        sharedDatabaseReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                DatabaseReference userDatabaseReference = Database.getInstance(getApplicationContext()).getUserReferences(dataSnapshot.getKey());
                userDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        APIRequest apiRequest = new APIRequest();
                        apiRequest.sendNotification(getApplicationContext(), dataSnapshot.child(FCMID).getValue().toString(),
                                Preference.getInstance(getApplicationContext()).getName()+" Is in danger! He needs help");
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void addInviteWindow() {
        View popUpView = getLayoutInflater().inflate(R.layout.popup_add_invite, null);
        mpopup = new PopupWindow(popUpView, LinearLayoutCompat.LayoutParams.MATCH_PARENT,
                LinearLayoutCompat.LayoutParams.MATCH_PARENT, true);
        mpopup.setAnimationStyle(android.R.style.Animation_Dialog);

        ImageView closePopup = popUpView.findViewById(R.id.popup_close);
        closePopup.setOnClickListener(this);

        CustomButton connectButton = popUpView.findViewById(R.id.connect_invite);
        connectButton.setOnClickListener(this);

        inviteEdit = popUpView.findViewById(R.id.invite_code_edit);

        mpopup.showAtLocation(popUpView, Gravity.CENTER, 0, 0);
    }

    private void validatedInviteCode(String code) {
        Database.validateCodeListener(new Database.ValidateCode() {
            @Override
            public void validCode(String code) {

            }

            @Override
            public void validCode(boolean status, String userId, String fcmid) {
                if (status) {
                    if (!fcmid.isEmpty()) {
                        Database.getInstance(getApplicationContext()).shareLocation(getApplicationContext(), userId);
                        APIRequest apiRequest = new APIRequest();
                        apiRequest.sendNotification(getApplicationContext(), fcmid,
                                Preference.getInstance(getApplicationContext()).getName()+" Started to share Location with your");
                    }
                    mpopup.dismiss();
                }
                else {
                    Utility.inviteCodeError(getApplicationContext());
                }
            }
        });
        if (code.equals(Preference.getInstance(getApplicationContext()).getInviteCode()))
            Utility.toast(getApplicationContext(),"You can't add yourself");
        else
            Database.getInstance(getApplicationContext()).validateCode(code);
    }
}
