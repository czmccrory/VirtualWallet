package com.example.virtualwallet.service;

import android.os.AsyncTask;

import com.example.virtualwallet.model.CredentialRequest;
import com.example.virtualwallet.model.CredentialResult;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ListCredentials extends AsyncTask<CredentialRequest, Void, CredentialResult> {

    public interface ListCredentialsHandler {
        void HandleCredentials(CredentialResult result);
    }

    private final ListCredentialsHandler handler;

    private String cloudAgentId;
    private String signature;

    public ListCredentials(ListCredentialsHandler handler, String cloudAgentId, String signature) {
        this.handler = handler;
        this.cloudAgentId = cloudAgentId;
        this.signature = signature;
    }

    @Override
    protected CredentialResult doInBackground(CredentialRequest... reqs) {
        //Generates the List Credentials Interface
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://canis.scoir.ninja/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ApiCall apiCall = retrofit.create(ApiCall.class);

        //Http request to the remote webserver
        Call<CredentialResult> call = apiCall.ListCredentials(this.cloudAgentId, this.signature, reqs[0]);
        try {
            Response<CredentialResult> resp = call.execute();
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
    protected void onPostExecute(CredentialResult result) {
        super.onPostExecute(result);
        this.handler.HandleCredentials(result);
    }
}
