package com.example.geolocation.Helper;

import android.content.Context;
import android.widget.Toast;

import com.example.geolocation.R;

/**
 * Created by surve on 25-Dec-17.
 */

public class Utility {

    public static void toast(Context context, Object o) {
        Toast.makeText(context, o.toString(), Toast.LENGTH_SHORT).show();
    }
    public static void longtoast(Context context, Object o) {
        Toast.makeText(context, o.toString(), Toast.LENGTH_LONG).show();
    }
    public static void internetError(Context context) {
        Toast.makeText(context, context.getString(R.string.internet_error_message), Toast.LENGTH_SHORT).show();
    }

    public static void error(Context context) {
        Toast.makeText(context, context.getString(R.string.error), Toast.LENGTH_SHORT).show();
    }

    public static void inviteCodeError(Context context) {
        Toast.makeText(context, "Enter Correct Invite Code", Toast.LENGTH_SHORT).show();
    }
}
