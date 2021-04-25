package com.example.virtualwallet.service;

import android.os.AsyncTask;

import com.example.virtualwallet.model.ConnectionRequest;
import com.example.virtualwallet.model.ConnectionResult;
import com.example.virtualwallet.service.ApiCall;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ListConnections extends AsyncTask<ConnectionRequest, Void, ConnectionResult> {

    public interface ListConnectionsHandler {
        void HandleConnections(ConnectionResult result);
    }

    private final ListConnectionsHandler handler;

    private String cloudAgentId;
    private String signature;

    public ListConnections(ListConnectionsHandler handler, String cloudAgentId, String signature) {
        this.handler = handler;
        this.cloudAgentId = cloudAgentId;
        this.signature = signature;
    }


    @Override
    protected ConnectionResult doInBackground(ConnectionRequest... connectionRequests) {
        //Generates the List Connections Interface
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://canis.scoir.ninja/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ApiCall apiCall = retrofit.create(ApiCall.class);

        //Http request to the remote webserver
        Call<ConnectionResult> call = apiCall.ListConnections(this.cloudAgentId, this.signature, connectionRequests[0]);
        try {
            Response<ConnectionResult> resp = call.execute();
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
    protected void onPostExecute(ConnectionResult result) {
        super.onPostExecute(result);
        this.handler.HandleConnections(result);
    }
}
