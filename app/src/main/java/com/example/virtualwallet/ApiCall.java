

package com.example.virtualwallet;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface ApiCall {
    @POST("cloudagents")
    Call<CloudAgent> Register(@Body Registration body);

}