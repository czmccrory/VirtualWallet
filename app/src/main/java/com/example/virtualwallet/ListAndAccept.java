package com.example.virtualwallet;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.HeaderMap;
import retrofit2.http.POST;

public interface ListAndAccept {
    @POST("cloudagents/connections")
    Call<Map<String, Connections>> GetConnections(@HeaderMap Map<String, String> headers);
}