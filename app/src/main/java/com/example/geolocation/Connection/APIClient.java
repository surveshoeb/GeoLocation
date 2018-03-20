package com.example.geolocation.Connection;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.example.geolocation.Connection.APIList.BASE_URL;

/**
 * Created by surve on 27-Feb-18.
 */

public class APIClient {

    public static API getApiClient() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create()).build();

        API api = retrofit.create(API.class);
        return api;
    }
}
