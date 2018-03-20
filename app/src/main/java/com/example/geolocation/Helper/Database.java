package com.example.geolocation.Helper;

import android.content.Context;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.HashMap;
import java.util.Random;

import static com.example.geolocation.Helper.Preference.FCMID;
import static com.example.geolocation.Helper.Preference.INVITE;
import static com.example.geolocation.Helper.Preference.LATITUDE;
import static com.example.geolocation.Helper.Preference.LONGITUDE;
import static com.example.geolocation.Helper.Preference.NAME;

/**
 * Created by surve on 25-Dec-17.
 */

public class Database {

    FirebaseDatabase  database = FirebaseDatabase.getInstance();
    DatabaseReference userReference = database.getReference("users");
    DatabaseReference inviteReference = database.getReference("invite");
    DatabaseReference sharedLocationReference = database.getReference("sharedLocation");
    DatabaseReference meetingLocationReference = database.getReference("meetingLocation");

    Context context;

    private static Database instance;

    private static ValidateCode validateCodeListener;

    public Database(Context context) {
        this.context = context;
    }
    public static Database getInstance(final Context context) {
        if (instance == null) {
            instance = new Database(context.getApplicationContext());
        }
        return instance;
    }

    public DatabaseReference getUserReferences() {
        if (!Preference.getInstance(context).getNumber().isEmpty()) {
            return userReference.child(String.valueOf(Preference.getInstance(context).getNumber()));
        }
        else
            return null;
    }

    public DatabaseReference getUserReferences(String userId) {
        return userReference.child(userId);
    }

    public DatabaseReference getAllUserReferences() {
        return userReference;
    }

    public DatabaseReference getSharedLocationReference(String userId) { return sharedLocationReference.child(userId); }

    public DatabaseReference getMeetingLocation(String userId) {
        return meetingLocationReference.child(userId);
    }

    public void saveUserData() {
        final HashMap userData = new HashMap();
        userData.put(NAME,Preference.getInstance(context).getName());
        userData.put(LATITUDE,Preference.getInstance(context).getLatitude());
        userData.put(LONGITUDE, Preference.getInstance(context).getLongitude());
        userData.put(FCMID, FirebaseInstanceId.getInstance().getToken());
        if (Preference.getInstance(context).getInviteCode().isEmpty()) {
            validateCodeListener(new ValidateCode() {
                @Override
                public void validCode(String code) {
                    userData.put(INVITE, code);
                    Preference.getInstance(context).setInviteCode(code);
                    userReference.child(Preference.getInstance(context).getNumber()).setValue(userData);
                    inviteReference.child(code).setValue(Preference.getInstance(context).getNumber());
                }

                @Override
                public void validCode(boolean status, String userId, String fcmid) {

                }
            });
        }
        else {
            userData.put(INVITE, Preference.getInstance(context).getInviteCode());
            userReference.child(Preference.getInstance(context).getNumber()).setValue(userData);

        }
        getCode();
    }

    public String getName(String key) {
        return userReference.child(key).child(NAME).toString();
    }

    public void updateUserData() {
        HashMap userData = new HashMap();
        userData.put(FCMID, FirebaseInstanceId.getInstance().getToken());
        userData.put(LATITUDE,Preference.getInstance(context).getLatitude());
        userData.put(LONGITUDE, Preference.getInstance(context).getLongitude());
        userReference.child(Preference.getInstance(context).getNumber()).updateChildren(userData);
    }

    public String generateInviteCode() {
        char[] chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789".toCharArray();
        StringBuilder stringBuilder =new StringBuilder();
        Random random = new Random();
        for(int i = 0; i < 5; i++){
            char c = chars[random.nextInt(chars.length)];
            stringBuilder.append(c);
        }
        return stringBuilder.toString();
    }

    private void getCode() {
        final String code = generateInviteCode();

        inviteReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild(generateInviteCode()))
                    getCode();
                else
                    validateCodeListener.validCode(code);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void validateCode(final String code) {

        inviteReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild(code)) {
                    getFcmid(dataSnapshot, code, dataSnapshot.child(code).getValue().toString());
                }
                else
                    validateCodeListener.validCode(false, "", "");
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void getFcmid(final DataSnapshot dataSnapshot, final String code, String userId) {

        DatabaseReference userRef = Database.getInstance(context).getUserReferences(userId);
        if (userRef != null) {
            userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    if (snapshot.hasChild(INVITE))
                    if (snapshot.child(INVITE).getValue().toString().equals(code)) {
                        validateCodeListener.validCode(true, dataSnapshot.child(code).getValue().toString(),
                                snapshot.child(FCMID).getValue().toString());
                    }
                    else {

                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }

    public void shareLocation(final Context context, final String userId) {
        final DatabaseReference sharedLocation = sharedLocationReference.child(Preference.getInstance(context).getNumber());
        final DatabaseReference friendSharedLocation = sharedLocationReference.child(userId);

        sharedLocation.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild(userId))
                    Utility.toast(context,"Code Already Added");
                else {
                    HashMap hashMap = new HashMap();
                    hashMap.put("connected_time", ServerValue.TIMESTAMP);

                    sharedLocation.child(userId).setValue(hashMap);
                    friendSharedLocation.child(Preference.getInstance(context).getNumber()).setValue(hashMap);

                    Utility.toast(context,"Invite Code added successfully");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public static void validateCodeListener(ValidateCode listener) {
        validateCodeListener = listener;
    }

    public void setMeetingLocation(final String userId, final LatLng selectedLocation) {
        final DatabaseReference ownLocation = meetingLocationReference.child(Preference.getInstance(context).getNumber());
        final DatabaseReference friendLocation = meetingLocationReference.child(userId);
        HashMap hashMap = new HashMap();
        hashMap.put(LATITUDE, selectedLocation.latitude);
        hashMap.put(LONGITUDE, selectedLocation.longitude);
        hashMap.put("connected_time", ServerValue.TIMESTAMP);

        ownLocation.child(userId).setValue(hashMap);
        friendLocation.child(Preference.getInstance(context).getNumber()).setValue(hashMap);

        Utility.toast(context,"Meeting Location Added");
    }

    public interface ValidateCode {
        void validCode(String code);

        void validCode(boolean status, String userId, String fcmid);
    }
}
