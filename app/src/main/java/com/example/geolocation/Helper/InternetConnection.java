package com.example.geolocation.Helper;

import android.content.Context;
import android.net.ConnectivityManager;

/**
 * Created by surve on 25-Dec-17.
 */

public class InternetConnection {
    /** CHECK WHETHER INTERNET CONNECTION IS AVAILABLE OR NOT */
    public static boolean checkConnection(Context context) {
        return  ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo() != null;
    }
}
