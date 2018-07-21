package com.example.sang.mplayer.data;


import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface RequestInterface {

    @GET("/search")
    Call<JsonResponse> getJSON(@Query("term") String key);


}
