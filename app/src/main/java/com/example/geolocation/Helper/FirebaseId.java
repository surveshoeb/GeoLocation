package com.example.geolocation.Helper;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

/**
 * Created by surve on 27-Feb-18.
 */

public class FirebaseId extends FirebaseInstanceIdService {

    @Override
    public void onTokenRefresh() {
        super.onTokenRefresh();
        Preference.getInstance(getApplicationContext()).setFcmid(FirebaseInstanceId.getInstance().getToken());
    }
}
