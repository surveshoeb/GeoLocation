package com.example.geolocation.Helper;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by surve on 25-Dec-17.
 */

public class Preference {

    private static final String APP_NAME = "anitbunking";

    public static final String NAME = "name";
    public static final String NUMBER = "number";
    public static final String LATITUDE = "latitude";
    public static final String LONGITUDE = "longitude";
    public static final String FCMID = "fcmid";
    public static final String INVITE = "invite";


    private static Preference instance;
    private static SharedPreferences sharedPreferences;

    private Context context;

    public Preference(Context context) {
        sharedPreferences = context.getSharedPreferences(APP_NAME, Context.MODE_PRIVATE);
        this.context = context;
    }
    public static Preference getInstance(Context context) {
        if (instance == null)
            instance = new Preference(context);
        return instance;
    }

    public void setName(String username) {
        final SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(NAME, username);
        editor.apply();
    }
    public String getName() { return sharedPreferences.getString(NAME, "");}

    public void setNumber(String number) {
        final SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(NUMBER, number);
        editor.apply();
    }
    public String getNumber() { return sharedPreferences.getString(NUMBER, "");}

    public void setLatitude(float latitude) {
        final SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putFloat(LATITUDE, latitude);
        editor.apply();
    }
    public float getLatitude() { return sharedPreferences.getFloat(LATITUDE, 0);}

    public void setLongitude(float longitude) {
        final SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putFloat(LONGITUDE, longitude);
        editor.apply();
    }
    public float getLongitude() { return sharedPreferences.getFloat(LONGITUDE, 0);}

    public String getInviteCode() {
        return sharedPreferences.getString(INVITE, "");
    }

    public void setInviteCode(String inviteCode) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(INVITE, inviteCode);
        editor.apply();
    }

    public void setFcmid(String token) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(FCMID, token);
        editor.apply();
    }
    public String getFcmid() { return sharedPreferences.getString(FCMID, ""); }
}
