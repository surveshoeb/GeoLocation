package com.example.geolocation.Connection;

import retrofit2.Call;
import retrofit2.http.POST;
import retrofit2.http.Query;

/**
 * Created by surve on 27-Feb-18.
 */

public interface API {

    @POST(APIList.SEND_NOTIFICATION)
    Call<String> sendNotification(@Query("server_key") String serverKey,
                                  @Query("registraionId") String registraionId,
                                  @Query("title") String title,
                                  @Query("description") String description,
                                  @Query("agent") String agent);
}
