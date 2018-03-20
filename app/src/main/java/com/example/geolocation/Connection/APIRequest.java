package com.example.geolocation.Connection;

import android.content.Context;
import android.util.Log;

import com.example.geolocation.Helper.Preference;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by surve on 27-Feb-18.
 */

public class APIRequest {

    public void sendNotification(Context context, String fcmid, String title) {

        String TAG = "Send Notification";

        String serverKey = "AIzaSyAWwi20Az4oscAa902gkR9jRhKI6mCIiaI";
        String description = "";
        String agent = "android";

        Call<String> call = APIClient.getApiClient().sendNotification(serverKey, fcmid, title, description, agent);
        Log.d(TAG, "sendNotification: "+call.request().url().toString());

        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {

            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {

            }
        });
    }
}
