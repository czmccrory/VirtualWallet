package com.example.virtualwallet.service;

import android.os.AsyncTask;

import com.example.virtualwallet.model.AcceptCredentialResult;
import com.example.virtualwallet.model.Credential;

import java.io.IOException;
import java.util.HashMap;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class AcceptCredential extends AsyncTask<Credential, Void, AcceptCredentialResult> {

    public interface AcceptCredentialHandler {
        void HandleConnections(AcceptCredentialResult result);
    }

    private final AcceptCredentialHandler handler;

    private String cloudAgentId;
    private String signature;

    public AcceptCredential(AcceptCredentialHandler handler, String cloudAgentId, String signature) {
        this.handler = handler;
        this.cloudAgentId = cloudAgentId;
        this.signature = signature;
    }

    @Override
    protected AcceptCredentialResult doInBackground(Credential... creds) {
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        // set your desired log level
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        // add your other interceptors …
        // add logging as last interceptor
        httpClient.addInterceptor(logging);  // <-- this is the important line!

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://canis.scoir.ninja/")
                .addConverterFactory(GsonConverterFactory.create())
                .client(httpClient.build())
                .build();

        ApiCall apiCall = retrofit.create(ApiCall.class);

        Call<AcceptCredentialResult> call = apiCall.AcceptCredential(this.cloudAgentId, this.signature, creds[0].id, new HashMap());
        try {
            Response<AcceptCredentialResult> resp = call.execute();
            if (resp.isSuccessful()) {
                return resp.body();
            } else {
                throw new IOException(resp.errorBody().string());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    protected void onPostExecute(AcceptCredentialResult result) {
        super.onPostExecute(result);
        this.handler.HandleConnections(result);
    }
}
